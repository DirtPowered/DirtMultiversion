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

package com.github.dirtpowered.dirtmv.network.versions.Release39To29;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.ItemStack;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.MetadataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.Motion;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.utils.encryption.EncryptionUtils;
import com.mojang.nbt.CompoundTag;
import lombok.SneakyThrows;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

public class ProtocolRelease39To29 extends ServerProtocol {

    public ProtocolRelease39To29() {
        super(MinecraftVersion.R1_3_1, MinecraftVersion.R1_2_4);
    }

    @Override
    public void registerTranslators() {
        addTranslator(0x02 /* HANDSHAKE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {
                if (dir == PacketDirection.SERVER_TO_CLIENT || data.getObjects().length < 3) {
                    return new PacketData(-1); // since 1.3 handshake is one-way (client -> server)
                }

                PublicKey key = EncryptionUtils.keyPair.getPublic();
                byte[] verify = new byte[4];

                String username = (String) data.read(1).getObject();
                session.getUserData().setUsername(username);

                session.getMain().getSharedRandom().nextBytes(verify);

                PacketData encryptRequest = PacketUtil.createPacket(0xFD, new TypeHolder[]{
                        set(Type.STRING, "-"),
                        set(Type.SHORT_BYTE_ARRAY, key.getEncoded()),
                        set(Type.SHORT_BYTE_ARRAY, verify)
                });

                session.getUserData().setProxyRequest(encryptRequest);

                // server -> client
                session.sendPacket(encryptRequest, PacketDirection.SERVER_TO_CLIENT, ProtocolRelease39To29.class);

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.STRING, username)
                });
            }
        });

        addTranslator(0xFC /* CLIENT SHARED KEY */, new PacketTranslator() {

            @SneakyThrows
            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                PacketData request = session.getUserData().getProxyRequest();
                SecretKey sharedKey = EncryptionUtils.getSecret(data, request);

                // server -> client
                PacketData response = PacketUtil.createPacket(0xFC, new TypeHolder[]{
                        set(Type.SHORT_BYTE_ARRAY, new byte[0]),
                        set(Type.SHORT_BYTE_ARRAY, new byte[0])
                });

                session.sendPacket(response, PacketDirection.SERVER_TO_CLIENT, ProtocolRelease39To29.class);

                // enable encryption
                EncryptionUtils.setEncryption(session.getChannel(), sharedKey);
                return new PacketData(-1); // cancel packet
            }
        });

        addTranslator(0xCD /* CLIENT COMMAND */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                byte command = (byte) data.read(0).getObject();

                if (command == 0x00) {
                    String username = session.getUserData().getUsername();

                    return PacketUtil.createPacket(0x01, new TypeHolder[]{
                            set(Type.INT, 29), // protocol version
                            set(Type.STRING, username),
                            set(Type.STRING, "NORMAL"),
                            set(Type.INT, 0),
                            set(Type.INT, 0),
                            set(Type.BYTE, 0),
                            set(Type.BYTE, 0),
                            set(Type.BYTE, 0),
                    });
                } else {

                    return PacketUtil.createPacket(0x09, new TypeHolder[]{
                            set(Type.INT, 0),
                            set(Type.BYTE, 0),
                            set(Type.BYTE, 0),
                            set(Type.SHORT, 0),
                            set(Type.STRING, "NORMAL"),
                    });
                }
            }
        });

        addTranslator(0x01, /* LOGIN */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        data.read(0),
                        data.read(2),
                        set(Type.BYTE, data.read(3).getObject()),
                        set(Type.BYTE, data.read(4).getObject()),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                });
            }
        });

        addTranslator(0xCB, /* TAB AUTOCOMPLETE */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(0xCC, /* CLIENT SETTINGS */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(0x32, /* PRE CHUNK */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x33, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        set(Type.BYTE, 1),
                        set(Type.SHORT, 0),
                        set(Type.SHORT, 0),
                        set(Type.INT_BYTE_ARRAY, new byte[0])
                });
            }
        });

        addTranslator(0x33 /* MAP CHUNK */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                V1_2Chunk chunk = (V1_2Chunk) data.read(0).getObject();

                return PacketUtil.createPacket(0x33, new TypeHolder[]{
                        set(Type.V1_3_CHUNK, chunk)
                });
            }
        });

        addTranslator(0x35 /* BLOCK CHANGE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x35, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        set(Type.SHORT, ((Byte) data.read(3).getObject()).shortValue()),
                        data.read(4)
                });
            }
        });

        addTranslator(0x66 /* WINDOW CLICK */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                ItemStack newItem = (ItemStack) data.read(5).getObject();

                return PacketUtil.createPacket(0x66, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.V1_0R_ITEM, newItem)
                });
            }
        });

        addTranslator(0x67, /* SET SLOT */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                ItemStack oldItem = (ItemStack) data.read(2).getObject();

                return PacketUtil.createPacket(0x67, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_3R_ITEM, oldItem)
                });
            }
        });

        addTranslator(0x68, /* WINDOW ITEMS */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                ItemStack[] items = (ItemStack[]) data.read(1).getObject();

                for (ItemStack item : items) {
                    if (item != null && item.getCompoundTag() == null) {
                        // since 1.3 all items contains NBT data
                        item.setCompoundTag(new CompoundTag("tag"));
                    }
                }

                return PacketUtil.createPacket(0x68, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_3R_ITEM_ARRAY, items)
                });
            }
        });

        addTranslator(0x1D, /* ENTITY DESTROY */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                int entityId = (int) data.read(0).getObject();

                return PacketUtil.createPacket(0x1D, new TypeHolder[]{
                        set(Type.BYTE_INT_ARRAY, new int[]{entityId})
                });
            }
        });

        addTranslator(0x18, /* MOB SPAWN */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x18, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        set(Type.SHORT, 0),
                        set(Type.SHORT, 0),
                        set(Type.SHORT, 0),
                        data.read(8)
                });
            }
        });

        addTranslator(0x17, /* VEHICLE SPAWN */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                Motion motion = (Motion) data.read(5).getObject();

                int throwerId = motion.getThrowerId();
                byte type = (byte) data.read(1).getObject();

                switch (type) {
                    case 70:
                        throwerId = 12;
                        break;
                    case 71:
                        throwerId = 13;
                        type = 70;
                        break;
                    case 74:
                        throwerId = 122;
                        break;
                }

                motion.setThrowerId(throwerId);

                return PacketUtil.createPacket(0x17, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, type),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.MOTION, motion)
                });
            }
        });

        addTranslator(0x0F /* BLOCK PLACE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                ItemStack newItem = (ItemStack) data.read(4).getObject();

                return PacketUtil.createPacket(0x0F, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.V1_0R_ITEM, newItem)
                });
            }
        });

        addTranslator(0x6B /* CREATIVE SET SLOT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                ItemStack newItem = (ItemStack) data.read(1).getObject();

                return PacketUtil.createPacket(0x6B, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_0R_ITEM, newItem)
                });
            }
        });

        addTranslator(0x14, /* NAMED ENTITY SPAWN */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                /* default 1.3.x metadata */
                List<WatchableObject> watchableObjects = Arrays.asList(
                        new WatchableObject(MetadataType.BYTE, 0, 0),
                        new WatchableObject(MetadataType.BYTE,16, 0),
                        new WatchableObject(MetadataType.SHORT, 1, 300),
                        new WatchableObject(MetadataType.BYTE,17, 0),
                        new WatchableObject(MetadataType.INT,8, 0)
                );

                return PacketUtil.createPacket(0x14, new TypeHolder[]{
                        data.read(0), // entityId
                        data.read(1), // playerName
                        data.read(2), // x
                        data.read(3), // y
                        data.read(4), // z
                        data.read(5), // yaw
                        data.read(6), // pitch
                        data.read(7), // item
                        set(Type.V1_3B_METADATA, watchableObjects) // default metadata
                });
            }
        });

        addTranslator(0x3C, /* EXPLOSION */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x3C, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.FLOAT, 0.0F),
                        set(Type.FLOAT, 0.0F),
                        set(Type.FLOAT, 0.0F)
                });
            }
        });

        addTranslator(0xCA, /* PLAYER ABILITIES */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                if (dir == PacketDirection.SERVER_TO_CLIENT) {

                    boolean invulnerable = ((Byte) data.read(0).getObject()) == 1;
                    boolean flying = ((Byte) data.read(1).getObject()) == 1;
                    boolean allowFlying = ((Byte) data.read(2).getObject()) == 1;
                    boolean instantBreak = ((Byte) data.read(3).getObject()) == 1;

                    byte mask = 0;

                    if (invulnerable) {
                        mask = (byte) (mask | 1);
                    }

                    if (flying) {
                        mask = (byte) (mask | 2);
                    }

                    if (allowFlying) {
                        mask = (byte) (mask | 4);
                    }

                    if (instantBreak) {
                        mask = (byte) (mask | 8);
                    }

                    return PacketUtil.createPacket(0xCA, new TypeHolder[]{
                            set(Type.BYTE, mask),
                            set(Type.BYTE, (byte) (0.05f * 255)),
                            set(Type.BYTE, (byte) (0.1f * 255)),
                    });
                } else {
                    byte mask = (byte) data.read(0).getObject();

                    return PacketUtil.createPacket(0xCA, new TypeHolder[]{
                            set(Type.BYTE, ((mask & 1) > 0)),
                            set(Type.BYTE, ((mask & 2) > 0)),
                            set(Type.BYTE, ((mask & 4) > 0)),
                            set(Type.BYTE, ((mask & 8) > 0)),
                    });
                }
            }
        });

        addTranslator(0x84 /* UPDATE TILE ENTITY */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x84, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.COMPOUND_TAG, new CompoundTag())
                });
            }
        });

        addTranslator(0x05 /* ENTITY EQUIPMENT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                short itemId = (short) data.read(2).getObject();
                short itemData = (short) data.read(3).getObject();

                ItemStack itemStack;

                if (itemId == -1) {
                    itemStack = null;
                } else {
                    itemStack = new ItemStack(itemId, 0, itemData, null);
                }

                return PacketUtil.createPacket(0x05, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        set(Type.V1_3R_ITEM, itemStack)
                });
            }
        });

        addTranslator(0x36 /* PLAY NOTEBLOCK */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                // 0 - x (int)
                // 1 - y (short)
                // 2 - z (int)
                // 3 - instrument (byte)
                // 4 - pitch (byte)

                // TODO: Chunk cache
                return new PacketData(-1);
            }
        });
    }
}
