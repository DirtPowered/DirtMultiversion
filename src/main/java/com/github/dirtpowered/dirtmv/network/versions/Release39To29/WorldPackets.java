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

package com.github.dirtpowered.dirtmv.network.versions.Release39To29;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.chunk.storage.ExtendedBlockStorage;
import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_2RChunkStorage;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockLocation;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Location;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2MultiBlockArray;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage.BlockStorage;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.WorldEntityEvent;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.OpenChestTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.WorldSound;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldPackets extends ServerProtocol {

    public WorldPackets() {
        super(MinecraftVersion.R1_3_1, MinecraftVersion.R1_2_4);
    }

    private boolean shouldCache(int blockId) {
        return blockId == 54 || blockId == 29 || blockId == 33 || blockId == 25 || blockId == 0;
    }

    @Override
    public void registerTranslators() {
        // block change
        addTranslator(0x35, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                BlockStorage blockStorage = session.getStorage().get(BlockStorage.class);

                if (blockStorage.getVersion() == getTo()) {
                    int x = data.read(Type.INT, 0);
                    byte y = data.read(Type.BYTE, 1);
                    int z = data.read(Type.INT, 2);

                    int chunkX = x >> 4;
                    int chunkZ = z >> 4;

                    byte blockId = data.read(Type.BYTE, 3);

                    if (shouldCache(blockId)) {
                        blockStorage.setBlockAt(chunkX, chunkZ, x, y, z, blockId);
                    }
                }
                return PacketUtil.createPacket(0x35, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        set(Type.SHORT, data.read(Type.BYTE, 3).shortValue()),
                        data.read(4)
                });
            }
        });

        // multi block change
        addTranslator(0x34, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                BlockStorage blockStorage = session.getStorage().get(BlockStorage.class);

                if (blockStorage.getVersion() == getTo()) {
                    int chunkX = data.read(Type.INT, 0);
                    int chunkZ = data.read(Type.INT, 1);

                    V1_2MultiBlockArray blocks = data.read(Type.V1_2MULTIBLOCK_ARRAY, 2);

                    DataInput dis = new DataInputStream(new ByteArrayInputStream(blocks.getData()));

                    for (int i = 0; i < blocks.getRecordCount(); i++) {
                        try {
                            short pos = dis.readShort();

                            int x = pos >> 12 & 15;
                            int y = pos & 255;
                            int z = pos >> 8 & 15;

                            int xPos = x + (chunkX << 4);
                            int zPos = z + (chunkZ << 4);

                            int blockId = dis.readShort() >> 4 & 4095;

                            if (shouldCache(blockId)) {
                                blockStorage.setBlockAt(chunkX, chunkZ, xPos, y, zPos, blockId);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return data;
            }
        });

        // chunk data
        addTranslator(0x33, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                BlockStorage blockStorage = session.getStorage().get(BlockStorage.class);

                V1_2Chunk chunk = data.read(Type.V1_2_CHUNK, 0);

                if (blockStorage.getVersion() == getTo()) {
                    List<ExtendedBlockStorage> parts = new ArrayList<>();

                    // create chunk storage
                    V1_2RChunkStorage storage = new V1_2RChunkStorage(
                            true, true, chunk.getChunkX(), chunk.getChunkZ()
                    );

                    storage.readChunk(chunk.isGroundUp(), chunk.getPrimaryBitmap(), chunk.getUncompressedData());

                    // cache for later use (r1.8 -> r1.7)
                    chunk.setStorage(storage);

                    ExtendedBlockStorage[] columns = storage.getColumnStorage();

                    for (int i = 0; i < columns.length; ++i) {
                        ExtendedBlockStorage e = columns[i];
                        boolean f = e != null && !columns[i].isEmpty();

                        if (e != null && (chunk.getPrimaryBitmap() & 1 << i) != 0 && (!chunk.isGroundUp() || f)) {
                            parts.add(e);
                        }
                    }

                    for (int i = 0; i < parts.size(); ++i) {
                        byte[] blockArray = parts.get(i).getBlockLSBArray();

                        for (int j = 0; j < blockArray.length; ++j) {
                            int x = j & 15;
                            int y = (j >> 8) + i * 16 & 255;
                            int z = j >> 4 & 15;

                            int blockId = blockArray[j] & 255;

                            if (shouldCache(blockId)) {
                                blockStorage.setBlockAt(chunk.getChunkX(), chunk.getChunkZ(), x, y, z, blockId);
                            }
                        }
                    }
                }

                return PacketUtil.createPacket(0x33, new TypeHolder[]{
                        set(Type.V1_3_CHUNK, chunk)
                });
            }
        });

        // pre chunk
        addTranslator(0x32, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();

                byte mode = data.read(Type.BYTE, 2);

                if (mode == 1) {
                    return cancel();
                }

                int chunkX = data.read(Type.INT, 0);
                int chunkZ = data.read(Type.INT, 1);

                OpenChestTracker chestTracker = storage.get(OpenChestTracker.class);

                for (BlockLocation entry : chestTracker.getChestStates().keySet()) {
                    int x = entry.getX() >> 4;
                    int z = entry.getZ() >> 4;

                    if (chunkX == x && chunkZ == z) {
                        chestTracker.remove(entry);
                    }
                }

                BlockStorage blockStorage = storage.get(BlockStorage.class);

                if (blockStorage.getVersion() == getTo()) {
                    blockStorage.removeChunk(chunkX, chunkZ);
                }

                V1_2Chunk chunk = new V1_2Chunk(chunkX, chunkZ, true, (short) 0, (short) 0, 0, new byte[0], new byte[0], null);

                return PacketUtil.createPacket(0x33, new TypeHolder[]{
                        set(Type.V1_3_CHUNK, chunk)
                });
            }
        });

        // play noteblock
        addTranslator(0x36, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                int y = data.read(Type.SHORT, 1);
                int z = data.read(Type.INT, 2);

                byte type = data.read(Type.BYTE, 3);
                byte pitch = data.read(Type.BYTE, 4);

                ProtocolStorage storage = session.getStorage();

                short blockId = (short) storage.get(BlockStorage.class).getBlockAt(x, y, z);
                WorldSound worldSound;

                OpenChestTracker chestTracker = storage.get(OpenChestTracker.class);
                switch (type) {
                    case 0:
                        if (blockId == 33 || blockId == 29) {
                            worldSound = WorldSound.PISTON_OUT;
                        } else {
                            worldSound = WorldSound.NOTE_HARP;
                        }
                        break;
                    case 1:
                        if (blockId == 54) {
                            BlockLocation loc = new BlockLocation(x, y, z);

                            if (pitch == 1) {
                                if (!chestTracker.getState(loc)) {
                                    chestTracker.setOpen(loc, true);
                                    worldSound = WorldSound.CHEST_OPEN;
                                    pitch = 12;
                                } else {
                                    worldSound = WorldSound.NO_SOUND;
                                }
                            } else {
                                if (chestTracker.getState(loc)) {
                                    worldSound = WorldSound.CHEST_CLOSE;
                                    chestTracker.setOpen(loc, false);
                                } else {
                                    worldSound = WorldSound.NO_SOUND;
                                }
                            }
                        } else if (blockId == 33 || blockId == 29) {
                            worldSound = WorldSound.PISTON_IN;
                        } else {
                            worldSound = WorldSound.NOTE_CLICK;
                        }
                        break;
                    case 2:
                        worldSound = WorldSound.NOTE_SNARE;
                        break;
                    case 3:
                        worldSound = WorldSound.NOTE_HAT;
                        break;
                    case 4:
                        worldSound = WorldSound.NOTE_BASS_ATTACK;
                        break;
                    default:
                        worldSound = WorldSound.NOTE_HARP;
                        break;
                }

                float correctedPitch = (float) (0.5f * (Math.pow(2, pitch / 12.0f)));
                WorldEntityEvent.playSoundAt(session, new Location(x, y, z), worldSound, 3.0f, correctedPitch);

                return PacketUtil.createPacket(0x36, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.SHORT, blockId)
                });
            }
        });
    }
}