/*
 * Copyright (c) 2020 Dirt Powered
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

import com.github.dirtpowered.dirtmv.data.chunk.ChunkUtils;
import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_2RChunkStorage;
import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_3BChunkStorage;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BChunk;
import com.github.dirtpowered.dirtmv.data.transformers.block.Block;
import com.github.dirtpowered.dirtmv.data.transformers.block.ItemBlockDataTransformer;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetaToV1_2ChunkTranslator extends PacketTranslator {

    private ItemBlockDataTransformer blockDataTransformer;

    public BetaToV1_2ChunkTranslator(ItemBlockDataTransformer blockDataTransformer) {
        this.blockDataTransformer = blockDataTransformer;
    }

    @Override
    public PacketData translate(ServerSession session, PacketData data) {
        V1_3BChunk oldChunk = (V1_3BChunk) data.read(0).getObject();

        LegacyChunkTracker legacyChunkTracker = session.getUserData().getLegacyChunkTracker();

        if (legacyChunkTracker.getChunkMap() == null) {
            legacyChunkTracker.initialize();
        }

        boolean groundUp = true;

        if (oldChunk.getXSize() * oldChunk.getYSize() * oldChunk.getZSize() != 32768) {
            groundUp = false;
        }

        List<LegacyChunkPair> updatedChunks = getUpdatedChunks(legacyChunkTracker, oldChunk.getX(), oldChunk.getY(),
                oldChunk.getZ(), oldChunk.getXSize(), oldChunk.getYSize(), oldChunk.getZSize(), oldChunk.getChunk());

        if (updatedChunks.size() > 1) {
            return new PacketData(-1);
        }

        LegacyChunkPair chunkPair = updatedChunks.get(0);

        byte[] compressedData = chunkPair.getNewChunk().getCompressedData(groundUp, !groundUp ? 0xffff : 0xff);

        return PacketUtil.createPacket(0x33, new TypeHolder[]{
                new TypeHolder(Type.V1_2_CHUNK, new V1_2Chunk(
                        chunkPair.getOldChunk().getChunkX(),
                        chunkPair.getOldChunk().getChunkZ(),
                        groundUp,
                        (short) chunkPair.getNewChunk().getPrimaryBitmap(),
                        (short) 0,
                        chunkPair.getNewChunk().getCompressedSize(),
                        compressedData
                ))
        });
    }

    private List<LegacyChunkPair> getUpdatedChunks(LegacyChunkTracker chunkTracker
            , int x, int y, int z, int xSize, int ySize, int zSize, byte[] packetData) {
        List<LegacyChunkPair> updatedChunks = new ArrayList<>();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        int startPosition = x + xSize - 1 >> 4;
        int endPosition = z + zSize - 1 >> 4;

        int offset = 0;

        int startY = Math.max(y, 0);
        int endY = Math.min(y + ySize, 128);

        byte[] biomes = new byte[256];

        for (int i = chunkX; i <= startPosition; ++i) {
            int startX = Math.max(x - i * 16, 0);
            int newXSize = Math.min(x + xSize - i * 16, 16);

            for (int j = chunkZ; j <= endPosition; ++j) {
                int startZ = Math.max(z - j * 16, 0);
                int newZSize = Math.min(z + zSize - j * 16, 16);

                LegacyChunkPair chunkPair = chunkTracker.getChunkMap().get(ChunkUtils.getChunkLongKey(i, j));

                if (chunkPair == null) {
                    V1_3BChunkStorage oldChunk = new V1_3BChunkStorage(i, j);
                    V1_2RChunkStorage newChunk = new V1_2RChunkStorage(i, j);

                    chunkPair = new LegacyChunkPair(oldChunk, newChunk);
                    chunkTracker.getChunkMap().put(ChunkUtils.getChunkLongKey(i, j), chunkPair);
                }

                offset = chunkPair.getOldChunk().setChunkData(packetData, startX, startY, startZ, newXSize, endY, newZSize, offset);

                for (int posX = startX; posX < newXSize; posX++) {
                    for (int posY = startY; posY < endY; posY++) {
                        for (int posZ = startZ; posZ < newZSize; posZ++) {
                            int oldBlockId = chunkPair.getOldChunk().getBlockId(posX, posY, posZ);
                            int oldBlockData = chunkPair.getOldChunk().getBlockData(posX, posY, posZ);

                            Block replacement = blockDataTransformer.replaceBlock(oldBlockId, oldBlockData);

                            chunkPair.getNewChunk().setBlockId(posX, posY, posZ, replacement.getBlockId());
                            chunkPair.getNewChunk().setBlockMetadata(posX, posY, posZ, replacement.getBlockData());
                            chunkPair.getNewChunk().setBlockLight(posX, posY, posZ, chunkPair.getOldChunk().getBlockLight(posX, posY, posZ));
                            chunkPair.getNewChunk().setSkyLight(posX, posY, posZ, chunkPair.getOldChunk().getSkyLight(posX, posY, posZ));
                        }
                    }
                }

                Arrays.fill(biomes, (byte) 0x04 /* Forest */);
                chunkPair.getNewChunk().setBiomeData(biomes);

                updatedChunks.add(chunkPair);
            }
        }

        return updatedChunks;
    }
}
