/*
 * Copyright (c) 2020-2022 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.dirtmv.network.versions.Release28To23.chunk;

import com.github.dirtpowered.dirtmv.api.Configuration;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.chunk.biome.OldChunkData;
import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_2RChunkStorage;
import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_3BChunkStorage;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2MultiBlockArray;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BChunk;
import com.github.dirtpowered.dirtmv.data.transformers.block.Block;
import com.github.dirtpowered.dirtmv.data.transformers.block.ItemBlockDataTransformer;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BetaToV1_2ChunkTranslator extends PacketTranslator {

    private static final int BLOCK_CHANGE_CHUNK_SIZE = 128;
    private static final int MAX_SINGLE_BLOCK_UPDATE_PACKETS = 8;
    private final ItemBlockDataTransformer blockDataTransformer;

    public BetaToV1_2ChunkTranslator(ItemBlockDataTransformer blockDataTransformer) {
        this.blockDataTransformer = blockDataTransformer;
    }

    @Override
    public PacketData translate(ServerSession session, PacketData data) {
        ProtocolStorage storage = session.getStorage();

        LoadedChunkTracker chunkTracker = storage.get(LoadedChunkTracker.class);
        DimensionTracker dimensionTracker = storage.get(DimensionTracker.class);

        boolean hasSkylight = dimensionTracker.getDimension() == 0;

        V1_3BChunk oldChunk = (V1_3BChunk) data.read(0).getObject();
        boolean groundUp = true;

        if (oldChunk.getXSize() * oldChunk.getYSize() * oldChunk.getZSize() != 32768) {
            groundUp = false;
        }

        int chunkX = oldChunk.getX() >> 4;
        int chunkZ = oldChunk.getZ() >> 4;

        if (groundUp) {
            V1_3BChunkStorage oldChunkStorage = new V1_3BChunkStorage(chunkX, chunkZ);
            V1_2RChunkStorage newChunkStorage = new V1_2RChunkStorage(hasSkylight, true, chunkX, chunkZ);

            oldChunkStorage.setChunkData(
                    oldChunk.getChunk(),
                    oldChunk.getX(),
                    oldChunk.getY(),
                    oldChunk.getZ(),
                    oldChunk.getXSize(),
                    oldChunk.getYSize(),
                    oldChunk.getZSize(),
                    0,
                    true
            );

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 128; y++) {
                    for (int z = 0; z < 16; z++) {
                        int oldBlockId = oldChunkStorage.getBlockId(x, y, z);
                        int oldBlockData = oldChunkStorage.getBlockData(x, y, z);

                        Block replacement = blockDataTransformer.replaceBlock(oldBlockId, oldBlockData);

                        newChunkStorage.setBlockId(x, y, z, replacement.getBlockId());
                        newChunkStorage.setBlockMetadata(x, y, z, replacement.getBlockData());
                        newChunkStorage.setBlockLight(x, y, z, oldChunkStorage.getBlockLight(x, y, z));
                        if (hasSkylight) {
                            newChunkStorage.setSkyLight(x, y, z, oldChunkStorage.getSkyLight(x, y, z));
                        }
                    }
                }
            }
            byte[] biomes = new byte[256];

            if (storage.hasObject(OldChunkData.class)) {
                OldChunkData biomeData = storage.get(OldChunkData.class);
                biomes = biomeData.getBiomeDataAt(chunkX, chunkZ, !hasSkylight);
            } else {
                Arrays.fill(biomes, (byte) 0x04); // forest
            }

            newChunkStorage.setBiomeData(biomes);

            byte[] compressedData = newChunkStorage.getCompressedData(true, 0xff);

            session.sendPacket(PacketUtil.createPacket(0x33, new TypeHolder[]{
                    new TypeHolder<>(Type.V1_2_CHUNK, new V1_2Chunk(
                            chunkX,
                            chunkZ,
                            true,
                            (short) newChunkStorage.getPrimaryBitmap(),
                            (short) 0,
                            newChunkStorage.getCompressedSize(),
                            compressedData,
                            new byte[0],
                            newChunkStorage
                    ))
            }), PacketDirection.TO_CLIENT, MinecraftVersion.R1_2_1);

            chunkTracker.setChunkLoaded(chunkX, chunkZ);
            return ServerProtocol.cancel();
        } else {
            if (!chunkTracker.isChunkLoaded(chunkX, chunkZ)) {
                return ServerProtocol.cancel();
            }

            Configuration c = session.getMain().getConfiguration();
            boolean replaceChests = session.getUserData()
                    .getClientVersion().getRegistryId() >= 39 && c.replaceChests();

            List<WorldBlock> worldBlocks = getUpdatedBlockList(
                    oldChunk.getX(),
                    oldChunk.getY(),
                    oldChunk.getZ(),
                    oldChunk.getXSize(),
                    oldChunk.getYSize(),
                    oldChunk.getZSize(),
                    oldChunk.getChunk(),
                    replaceChests
            );

            int records = worldBlocks.size();

            if (worldBlocks.isEmpty()) {
                return ServerProtocol.cancel();
            }

            List<List<WorldBlock>> slicedList = getSlicedData(worldBlocks);

            if (records > MAX_SINGLE_BLOCK_UPDATE_PACKETS) {
                for (List<WorldBlock> slicedData : slicedList) {
                    chunkX = slicedData.get(0).getX() >> 4;
                    chunkZ = slicedData.get(0).getZ() >> 4;

                    int totalDataSize = 4 * slicedData.size();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream(totalDataSize);
                    DataOutputStream dos = new DataOutputStream(baos);
                    V1_2MultiBlockArray blockArray = null;
                    try {
                        for (WorldBlock record : slicedData) {
                            dos.writeShort((record.getX() - (chunkX << 4)) << 12 | (record.getZ() - (chunkZ << 4)) << 8 | record.getY());
                            dos.writeShort((record.getBlockId() & 4095) << 4 | record.getBlockData() & 15);
                        }

                        byte[] bytes = baos.toByteArray();
                        blockArray = new V1_2MultiBlockArray(slicedData.size(), bytes.length, bytes);

                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    PacketData multiBlockChange = PacketUtil.createPacket(0x34, new TypeHolder[]{
                            new TypeHolder<>(Type.INT, chunkX),
                            new TypeHolder<>(Type.INT, chunkZ),
                            new TypeHolder<>(Type.V1_2MULTIBLOCK_ARRAY, blockArray)
                    });

                    session.sendPacket(multiBlockChange, PacketDirection.TO_CLIENT, MinecraftVersion.R1_2_1);
                }
            } else {
                for (WorldBlock block : worldBlocks) {
                    PacketData blockUpdate = PacketUtil.createPacket(0x35, new TypeHolder[]{
                            new TypeHolder<>(Type.INT, block.getX()),
                            new TypeHolder<>(Type.BYTE, (byte) block.getY()),
                            new TypeHolder<>(Type.INT, block.getZ()),
                            new TypeHolder<>(Type.BYTE, (byte) block.getBlockId()),
                            new TypeHolder<>(Type.BYTE, (byte) block.getBlockData())
                    });

                    session.sendPacket(blockUpdate, PacketDirection.TO_CLIENT, MinecraftVersion.R1_2_1);
                }
            }
        }

        return ServerProtocol.cancel();
    }

    private List<WorldBlock> getUpdatedBlockList(int x, int y, int z, int xSize, int ySize, int zSize, byte[] packetData, boolean replaceChests) {
        List<WorldBlock> worldBlocks = new ArrayList<>();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        int startPosition = x + xSize - 1 >> 4;
        int endPosition = z + zSize - 1 >> 4;

        int offset = 0;

        int startY = Math.max(y, 0);
        int endY = Math.min(y + ySize, 128);

        for (int i = chunkX; i <= startPosition; ++i) {
            int startX = Math.max(x - i * 16, 0);
            int newXSize = Math.min(x + xSize - i * 16, 16);

            for (int j = chunkZ; j <= endPosition; ++j) {
                int startZ = Math.max(z - j * 16, 0);
                int newZSize = Math.min(z + zSize - j * 16, 16);

                V1_3BChunkStorage oldChunk = new V1_3BChunkStorage(i, j);
                offset = oldChunk.setChunkData(packetData, startX, startY, startZ, newXSize, endY, newZSize, offset, false);

                for (int posX = startX; posX < newXSize; posX++) {
                    for (int posY = startY; posY < endY; posY++) {
                        for (int posZ = startZ; posZ < newZSize; posZ++) {
                            int oldBlockId = oldChunk.getBlockId(posX, posY, posZ);
                            int oldBlockData = oldChunk.getBlockData(posX, posY, posZ);

                            if (oldBlockId == 54 && replaceChests) {
                                oldBlockId = 130;
                                oldBlockData = 2;
                            }

                            Block b = blockDataTransformer.replaceBlock(oldBlockId, oldBlockData);
                            worldBlocks.add(new WorldBlock(x + (posX - startX), posY, z + (posZ - startZ), b.getBlockId(), b.getBlockData()));
                        }
                    }
                }
            }
        }

        return worldBlocks;
    }

    private List<List<WorldBlock>> getSlicedData(List<WorldBlock> blockArray) {
        List<List<WorldBlock>> slicedList = new ArrayList<>();

        AtomicInteger i = new AtomicInteger();

        for (WorldBlock worldBlock : blockArray) {
            if (i.getAndIncrement() % BLOCK_CHANGE_CHUNK_SIZE == 0) {
                slicedList.add(new ArrayList<>());
            }

            slicedList.get(slicedList.size() - 1).add(worldBlock);
        }

        return slicedList;
    }

    @Getter
    @AllArgsConstructor
    private static class WorldBlock {
        private final int x;
        private final int y;
        private final int z;
        private final int blockId;
        private final int blockData;
    }
}
