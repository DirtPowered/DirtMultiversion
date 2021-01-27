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

package com.github.dirtpowered.dirtmv.network.versions.Beta17To14;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.entity.EntityType;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockLocation;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BChunk;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.block.RotationUtil;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.block.SolidBlockList;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.other.KeepAliveTask;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage.BlockStorage;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.other.HardnessTable;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProtocolBeta17to14 extends ServerProtocol {

    public ProtocolBeta17to14() {
        super(MinecraftVersion.B1_8_1, MinecraftVersion.B1_7_3);
    }

    private boolean isConnectedThroughProxy(DirtMultiVersion main, String username) {
        return main.getServer().getUserDataFromUsername(username) != null;
    }

    @Override
    public void onConnect(ServerSession session) {
        ProtocolStorage storage = session.getStorage();

        storage.set(BlockStorage.class, new BlockStorage());
        storage.set(PlayerTabListCache.class, new PlayerTabListCache());
        storage.set(KeepAliveTask.class, new KeepAliveTask(session));

        session.broadcastPacket(createTabEntryPacket(session.getUserData().getUsername(), true), getFrom());
    }

    @Override
    public void onDisconnect(ServerSession session) {
        session.broadcastPacket(createTabEntryPacket(session.getUserData().getUsername(), false), getFrom());
    }

    @Override
    public void registerTranslators() {
        // keep-alive
        addTranslator(0x00, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                return PacketUtil.createPacket(0x00, new TypeHolder[0]);
            }
        });

        // ping request
        addTranslator(0xFE, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String message = session.getMain().getConfiguration().preReleaseMOTD();
                message = ChatUtils.stripColor(message);

                int max = session.getMain().getConfiguration().getMaxOnline();
                int online = session.getConnectionCount();

                PacketData packetData = PacketUtil.createPacket(0xFF, new TypeHolder[]{
                        set(Type.STRING, message + "§" + online + "§" + max)
                });

                // Dear Mojang Devs!
                // I wanna know who broke server latency calculation in release 1.8, really
                if (session.getUserData().getClientVersion() == MinecraftVersion.R1_8) {
                    new Timer().schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    session.sendPacket(packetData, PacketDirection.TO_CLIENT, getFrom());
                                }
                            },
                            session.getMain().getSharedRandom().nextInt(70)
                    );
                } else {
                    session.sendPacket(packetData, PacketDirection.TO_CLIENT, getFrom());
                }

                return cancel(); // cancel sending
            }
        });

        // login
        addTranslator(0x01, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        set(Type.INT, 14), // INT
                        data.read(1), // STRING
                        data.read(2), // LONG
                        data.read(4) // BYTE
                });
            }
        });

        // login
        addTranslator(0x01, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                session.getMain().getSessionRegistry().getSessions().forEach((uuid, multiSession) -> {
                    String s = multiSession.getServerSession().getUserData().getUsername();
                    session.queuePacket(createTabEntryPacket(s, true), PacketDirection.TO_CLIENT, getFrom());
                });

                int max = session.getMain().getConfiguration().getMaxOnline();
                if (max > 100) max = 100; // b1.8 client is rendering tablist grid wrong when above 100

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        data.read(0), // INT - entityId
                        data.read(1), // STRING - empty
                        data.read(2), // LONG - world seed
                        set(Type.INT, 0), // INT - gameMode
                        data.read(3), // BYTE - dimension
                        set(Type.BYTE, 1), // BYTE - difficulty
                        set(Type.BYTE, -128), // BYTE - world height
                        set(Type.BYTE, (byte) max), // BYTE - maxPlayers
                });
            }
        });

        // update health
        addTranslator(0x08, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x08, new TypeHolder[]{
                        data.read(0),
                        set(Type.SHORT, (short) 6),
                        set(Type.FLOAT, 0.0F),
                });
            }
        });

        // respawn
        addTranslator(0x09, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                return PacketUtil.createPacket(0x09, new TypeHolder[]{
                        data.read(0),
                });
            }
        });

        // respawn
        addTranslator(0x09, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                return PacketUtil.createPacket(0x09, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, 1),
                        set(Type.BYTE, 0),
                        set(Type.SHORT, 128),
                        set(Type.LONG, 0),
                });
            }
        });

        // open window
        addTranslator(0x64, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x64, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.STRING, data.read(Type.STRING, 2)),
                        data.read(3)
                });
            }
        });

        // game state
        addTranslator(0x46, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x46, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, (byte) 0)
                });
            }
        });

        // entity action
        addTranslator(0x13, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                byte state = data.read(Type.BYTE, 1);

                if (state == 5 || state == 4) { // sprinting (stop/start)
                    return cancel(); // cancel sending
                }

                return data;
            }
        });

        // named entity spawn
        addTranslator(0x14, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);
                String username = data.read(Type.STRING, 1);

                if (!isConnectedThroughProxy(session.getMain(), username)) {
                    PlayerTabListCache cache = session.getStorage().get(PlayerTabListCache.class);
                    if (cache != null) {
                        session.sendPacket(createTabEntryPacket(username, true), PacketDirection.TO_CLIENT, getFrom());
                        cache.getTabPlayers().put(entityId, username);
                    }
                }
                return data;
            }
        });

        // entity destroy
        addTranslator(0x1D, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);
                PlayerTabListCache cache = session.getStorage().get(PlayerTabListCache.class);

                if (cache != null && cache.getTabPlayers().containsKey(entityId)) {
                    String username = cache.getTabPlayers().get(entityId);

                    session.sendPacket(createTabEntryPacket(username, false), PacketDirection.TO_CLIENT, getFrom());
                    cache.getTabPlayers().remove(entityId);
                }
                return data;
            }
        });

        // mob spawn
        addTranslator(0x18, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                byte entityType = data.read(Type.BYTE, 1);

                if (entityType == EntityType.HUMAN_MOB.getEntityTypeId()) {
                    PlayerTabListCache cache = session.getStorage().get(PlayerTabListCache.class);

                    // cache empty name, so the tab entry will be removed after killing human mob
                    cache.getTabPlayers().put(data.read(Type.INT, 0), StringUtil.EMPTY_STRING);

                    return PacketUtil.createPacket(0x14, new TypeHolder[]{
                            data.read(0),
                            set(Type.STRING, StringUtil.EMPTY_STRING),
                            data.read(2),
                            data.read(3),
                            data.read(4),
                            data.read(5),
                            data.read(6),
                            set(Type.SHORT, (short) 0)
                    });
                }
                return data;
            }
        });

        // block change
        addTranslator(0x35, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                byte y = data.read(Type.BYTE, 1);
                int z = data.read(Type.INT, 2);
                byte blockId = data.read(Type.BYTE, 3);
                byte blockData = data.read(Type.BYTE, 4);

                BlockStorage blockStorage = session.getStorage().get(BlockStorage.class);

                if (blockStorage != null) {
                    blockStorage.setBlockAt(x >> 4, z >> 4, x, y, z, blockId);

                    if (blockId == 54) {
                        blockData = RotationUtil.fixBlockRotation(session, x, y, z);
                    }
                }

                return PacketUtil.createPacket(0x35, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.BYTE, blockData),
                });
            }
        });

        // unload chunk
        addTranslator(0x32, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                BlockStorage blockStorage = session.getStorage().get(BlockStorage.class);
                if (blockStorage != null) {
                    byte mode = data.read(Type.BYTE, 2);

                    if (mode == 0) {
                        int chunkX = data.read(Type.INT, 0);
                        int chunkZ = data.read(Type.INT, 1);

                        blockStorage.removeChunk(chunkX, chunkZ);
                    }
                }

                return data;
            }
        });

        // chunk data
        addTranslator(0x33, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                V1_3BChunk chunk = data.read(Type.V1_3B_CHUNK, 0);
                int chunkX = chunk.getX() >> 4;
                int chunkZ = chunk.getZ() >> 4;

                // skip non-full chunk updates
                if (chunk.getXSize() * chunk.getYSize() * chunk.getZSize() != 32768) {
                    return data;
                }

                BlockStorage blockStorage = session.getStorage().get(BlockStorage.class);
                boolean reduceBlockStorageMemory = session.getMain().getConfiguration().reduceBlockStorageMemory();

                if (blockStorage != null) {
                    List<BlockLocation> locationList = new ArrayList<>();
                    try {
                        byte[] chunkData = chunk.getChunk();

                        for (int x = 0; x < 16; x++) {
                            for (int y = reduceBlockStorageMemory ? 20 : 0; y < 128; y++) {
                                for (int z = 0; z < 16; z++) {
                                    int blockId = chunkData[getBlockIndexAt(x, y, z)];

                                    if (SolidBlockList.isSolid(blockId) || HardnessTable.needsToBeCached(session, blockId)
                                            || blockId == 85 || blockId == 29 || blockId == 33) {
                                        if (blockId == 54) {
                                            locationList.add(new BlockLocation(x, y, z));
                                        }

                                        blockStorage.setBlockAt(chunkX, chunkZ, chunk.getX() + x, chunk.getY() + y, chunk.getZ() + z, blockId);
                                    }
                                }
                            }
                        }

                        for (BlockLocation location : locationList) {
                            int x = location.getX();
                            int y = location.getY();
                            int z = location.getZ();

                            byte rotation = RotationUtil.fixBlockRotation(session, chunk.getX() + x, chunk.getY() + y, chunk.getZ() + z);
                            int blockLightOffset = 65536;

                            setNibble(chunkData, x, y, z, (byte) 15, blockLightOffset);
                            sendDelayedBlockUpdate(session, chunk.getX() + x, chunk.getY() + y, chunk.getZ() + z, rotation);
                        }

                        chunk.setChunk(chunkData);
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }

                return PacketUtil.createPacket(0x33, new TypeHolder[]{
                        set(Type.V1_3B_CHUNK, chunk)
                });
            }
        });
    }

    private void sendDelayedBlockUpdate(ServerSession session, int x, int y, int z, byte data) {
        PacketData blockUpdate = PacketUtil.createPacket(0x35, new TypeHolder[]{
                new TypeHolder(Type.INT, x),
                new TypeHolder(Type.BYTE, (byte) y),
                new TypeHolder(Type.INT, z),
                new TypeHolder(Type.BYTE, (byte) 54),
                new TypeHolder(Type.BYTE, data)
        });

        session.queuePacket(blockUpdate, PacketDirection.TO_CLIENT, getFrom());
    }

    private void setNibble(byte[] data, int x, int y, int z, byte value, int offset) {
        int nibbleIndex = (x << 11 | z << 7 | y) >> 1;

        if ((nibbleIndex & 1) == 0) {
            data[nibbleIndex + offset] = (byte) (data[nibbleIndex + offset] & 240 | value & 15);
        } else {
            data[nibbleIndex + offset] = (byte) (data[nibbleIndex + offset] & 15 | (value & 15) << 4);
        }
    }

    private int getBlockIndexAt(int x, int y, int z) {
        return x << 11 | z << 7 | y;
    }

    private PacketData createTabEntryPacket(String username, boolean online) {

        return PacketUtil.createPacket(0xC9, new TypeHolder[]{
                set(Type.STRING, username),
                set(Type.BYTE, (byte) (online ? 1 : 0)),
                set(Type.SHORT, (short) 0)
        });
    }
}
