/*
 * Copyright (c) 2020-2021 Dirt Powered
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

package com.github.dirtpowered.dirtmv.network.versions.Release28To23;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.chunk.biome.OldChunkData;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2MultiBlockArray;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BMultiBlockArray;
import com.github.dirtpowered.dirtmv.data.transformers.block.Block;
import com.github.dirtpowered.dirtmv.data.transformers.block.ItemBlockDataTransformer;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release28To23.chunk.BetaToV1_2ChunkTranslator;
import com.github.dirtpowered.dirtmv.network.versions.Release28To23.chunk.DimensionTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release28To23.chunk.LoadedChunkTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release28To23.item.CreativeItemList;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProtocolRelease28To23 extends ServerProtocol {
    private static final ItemBlockDataTransformer blockDataTransformer;

    static {
        blockDataTransformer = new BlockRemapper();
    }

    public ProtocolRelease28To23() {
        super(MinecraftVersion.R1_2_1, MinecraftVersion.R1_1);
    }

    @Override
    public void onConnect(ServerSession session) {
        ProtocolStorage storage = session.getStorage();
        storage.set(LoadedChunkTracker.class, new LoadedChunkTracker());
    }

    @Override
    public void registerTranslators() {
        // login
        addTranslator(0x01, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        set(Type.INT, 23),
                        data.read(1),
                        set(Type.LONG, 0L),
                        data.read(2),
                        data.read(3),
                        set(Type.BYTE, data.read(Type.INT, 4).byteValue()),
                        data.read(5),
                        data.read(6),
                        data.read(7)
                });
            }
        });

        // login
        addTranslator(0x01, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                storage.set(DimensionTracker.class, new DimensionTracker());

                // biome data only for < b1.7 for now
                if (session.getMain().getConfiguration().getServerVersion().getRegistryId() < 17) {
                    OldChunkData biomeData = new OldChunkData();
                    biomeData.initialize(data.read(Type.LONG, 2));

                    storage.set(OldChunkData.class, biomeData);
                }

                int dimension = data.read(Type.BYTE, 5).intValue();
                DimensionTracker dimensionTracker = storage.get(DimensionTracker.class);
                dimensionTracker.setDimension(dimension);

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(3),
                        data.read(4),
                        set(Type.INT, dimension),
                        data.read(6),
                        data.read(7),
                        data.read(8)
                });
            }
        });

        // pre chunk
        addTranslator(0x32, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                LoadedChunkTracker chunkTracker = session.getUserData().getProtocolStorage().get(LoadedChunkTracker.class);
                int chunkX = data.read(Type.INT, 0);
                int chunkZ = data.read(Type.INT, 1);

                byte mode = data.read(Type.BYTE, 2);

                if (mode == 0) {
                    chunkTracker.removeChunk(chunkX, chunkZ);
                }
                return data;
            }
        });

        // chunk
        addTranslator(0x33, PacketDirection.TO_CLIENT, new BetaToV1_2ChunkTranslator(blockDataTransformer));

        // multi block change
        addTranslator(0x34, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                LoadedChunkTracker chunkTracker = session.getUserData().getProtocolStorage().get(LoadedChunkTracker.class);
                V1_3BMultiBlockArray blockArray = (V1_3BMultiBlockArray) data.read(2).getObject();

                int chunkX = data.read(Type.INT, 0);
                int chunkZ = data.read(Type.INT, 1);

                int totalDataSize = 4 * blockArray.getSize();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(totalDataSize);
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

                V1_2MultiBlockArray newFormat = null;
                try {
                    for (int i = 0; i < blockArray.getSize(); ++i) {
                        short coordinate = blockArray.getCoordsArray()[i];

                        int blockId = blockArray.getTypesArray()[i] & 255;
                        int blockData = blockArray.getMetadataArray()[i];

                        Block replacement = blockDataTransformer.replaceBlock(blockId, blockData);

                        dataOutputStream.writeShort(coordinate);
                        dataOutputStream.writeShort((short) ((replacement.getBlockId() & 4095) << 4 | replacement.getBlockData() & 15));
                    }

                    byte[] b = byteArrayOutputStream.toByteArray();

                    newFormat = new V1_2MultiBlockArray(blockArray.getSize(), b.length, b);

                    dataOutputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!chunkTracker.isChunkLoaded(chunkX, chunkZ)) {
                    return cancel();
                }

                return PacketUtil.createPacket(0x34, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_2MULTIBLOCK_ARRAY, newFormat)
                });
            }
        });

        // block change
        addTranslator(0x35, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                LoadedChunkTracker chunkTracker = session.getUserData().getProtocolStorage().get(LoadedChunkTracker.class);
                int chunkX = data.read(Type.INT, 0) >> 4;
                int chunkZ = data.read(Type.INT, 2) >> 4;

                byte blockId = data.read(Type.BYTE, 3);
                byte blockData = data.read(Type.BYTE, 4);

                Block replacement = blockDataTransformer.replaceBlock(blockId, blockData);

                if (!chunkTracker.isChunkLoaded(chunkX, chunkZ)) {
                    return cancel();
                }

                return PacketUtil.createPacket(0x35, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        set(Type.BYTE, (byte) replacement.getBlockId()),
                        set(Type.BYTE, (byte) replacement.getBlockData())
                });
            }
        });

        // mob spawn
        addTranslator(0x18, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x18, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        set(Type.BYTE, (byte) 0), // head yaw
                        data.read(7),
                });
            }
        });

        // respawn
        addTranslator(0x09, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x09, new TypeHolder[]{
                        set(Type.INT, (int) data.read(0).getObject()),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.LONG, 0L), // seed
                        data.read(4)
                });
            }
        });

        // respawn
        addTranslator(0x09, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                int dimension = data.read(Type.BYTE, 0).intValue();

                DimensionTracker dimensionTracker = storage.get(DimensionTracker.class);
                dimensionTracker.setDimension(dimension);

                return PacketUtil.createPacket(0x09, new TypeHolder[]{
                        set(Type.INT, dimension),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(5)
                });
            }
        });

        // entity relative move look
        addTranslator(0x21, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                PacketData rotationPacket = PacketUtil.createPacket(0x23, new TypeHolder[]{
                        data.read(0), // entityId
                        data.read(4), // yaw
                });

                session.sendPacket(rotationPacket, PacketDirection.TO_CLIENT, getFrom());
                return data;
            }
        });

        // entity look
        addTranslator(0x20, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                PacketData rotationPacket = PacketUtil.createPacket(0x23, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                });

                session.sendPacket(rotationPacket, PacketDirection.TO_CLIENT, getFrom());
                return data;
            }
        });

        // door change
        addTranslator(0x3D, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int effectId = data.read(Type.INT, 0);
                int effectData = data.read(Type.INT, 4);

                if (effectId == 2001) {
                    int blockId = effectData & 255;
                    int blockData = effectData >> 8 & 255;

                    effectData = blockId + (blockData << 12);
                }

                return PacketUtil.createPacket(0x3D, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.INT, effectData)
                });
            }
        });

        // creative item get
        addTranslator(0x6B, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack item = data.read(Type.V1_0R_ITEM, 1);

                boolean notNull = item != null;

                if (notNull && !CreativeItemList.exists(item.getItemId())) {
                    // replace all unknown items to stone
                    item.setItemId(1);
                    item.setData(0);
                }

                return PacketUtil.createPacket(0x6B, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_0R_ITEM, item)
                });
            }
        });
    }

    static class BlockRemapper extends ItemBlockDataTransformer {

        @Override
        public void registerReplacements() {
            // wooden door
            addBlockReplacement(64, 9, new Block(64, 8));
            addBlockReplacement(64, 10, new Block(64, 8));
            addBlockReplacement(64, 11, new Block(64, 8));
            addBlockReplacement(64, 12, new Block(64, 8));
            addBlockReplacement(64, 13, new Block(64, 8));
            addBlockReplacement(64, 14, new Block(64, 8));
            addBlockReplacement(64, 15, new Block(64, 8));

            // iron door
            addBlockReplacement(71, 9, new Block(71, 8));
            addBlockReplacement(71, 10, new Block(71, 8));
            addBlockReplacement(71, 11, new Block(71, 8));
            addBlockReplacement(71, 12, new Block(71, 8));
            addBlockReplacement(71, 13, new Block(71, 8));
            addBlockReplacement(71, 14, new Block(71, 8));
            addBlockReplacement(71, 15, new Block(71, 8));

            // chest
            addBlockReplacement(54, 0, new Block(54, 2));
        }
    }
}
