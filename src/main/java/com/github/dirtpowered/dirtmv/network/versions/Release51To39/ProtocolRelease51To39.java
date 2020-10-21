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

package com.github.dirtpowered.dirtmv.network.versions.Release51To39;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ProtocolState;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_3_1.V1_3_1RProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.ItemStack;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.MetadataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.Motion;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_3_4ChunkBulk;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release51To39.sound.SoundMappings;
import com.mojang.nbt.CompoundTag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class ProtocolRelease51To39 extends ServerProtocol {

    public ProtocolRelease51To39() {
        super(MinecraftVersion.R1_4_6, MinecraftVersion.R1_3_1);
    }

    private String transformMotd(String oldMessage) {
        String colorChar = "\u00a7";
        String splitChar = "\00";

        String[] oldParts = oldMessage.split(colorChar);

        Map<Integer, String> versionMap = new WeakHashMap<>();

        versionMap.put(51, "1.4.7");
        versionMap.put(60, "1.5.1");
        versionMap.put(61, "1.5.2");

        Object[] keyArray = versionMap.keySet().toArray();

        Integer selectedVersion = (Integer) keyArray[new Random().nextInt(keyArray.length)];
        String versionName = versionMap.get(selectedVersion);

        return colorChar + "1"
                + splitChar + selectedVersion
                + splitChar + versionName
                + splitChar + oldParts[0]
                + splitChar + oldParts[1]
                + splitChar + oldParts[2];
    }

    @Override
    public void registerTranslators() {

        addTranslator(0x01 /* LOGIN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (dir == PacketDirection.SERVER_TO_CLIENT)
                    session.getUserData().setDimension(data.read(Type.INT, 3));

                // switch state
                session.getUserData().setProtocolState(ProtocolState.IN_GAME);
                return data;
            }
        });

        addTranslator(0x09 /* RESPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (dir == PacketDirection.SERVER_TO_CLIENT)
                    session.getUserData().setDimension(data.read(Type.INT, 0));

                return data;
            }
        });

        addTranslator(0xFE /* SERVER PING REQUEST */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                // empty
                return PacketUtil.createPacket(0xFE, new TypeHolder[0]);
            }
        });

        addTranslator(0xFF /* KICK DISCONNECT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (session.getUserData().getProtocolState() != ProtocolState.PING)
                    return data;

                String reason = data.read(Type.STRING, 0);

                // old to new format
                return PacketUtil.createPacket(0xFF, new TypeHolder[] {
                        set(Type.STRING, transformMotd(reason))
                });
            }
        });

        addTranslator(0x02 /* HANDSHAKE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (data.getObjects().length < 3) {
                    return new PacketData(-1);
                }

                return PacketUtil.createPacket(0x02, new TypeHolder[] {
                        set(Type.BYTE, 39),
                        data.read(1),
                        data.read(2),
                        data.read(3)
                });
            }
        });

        addTranslator(0xCC /* CLIENT SETTINGS */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0xCC, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                });
            }
        });

        addTranslator(0x83 /* MAP DATA */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                byte[] mapData = data.read(Type.BYTE_BYTE_ARRAY, 2);

                return PacketUtil.createPacket(0x83, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        set(Type.UNSIGNED_SHORT_BYTE_ARRAY, mapData)
                });
            }
        });

        addTranslator(0x04 /* UPDATE TIME */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x04, new TypeHolder[] {
                        data.read(0),
                        data.read(0)
                });
            }
        });

        addTranslator(0x17 /* VEHICLE SPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x17, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.BYTE, 0), // yaw
                        set(Type.BYTE, 0), // pitch
                        data.read(5)
                });
            }
        });

        addTranslator(0x15 /* PICKUP SPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {

                PacketData vehicleSpawn = PacketUtil.createPacket(0x17, new TypeHolder[] {
                        data.read(0),
                        set(Type.BYTE, (byte) 2),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        data.read(8),
                        set(Type.MOTION, new Motion(1, (short) 0, (short) 0, (short) 0))
                });

                short itemId = data.read(Type.SHORT, 1);
                byte amount = data.read(Type.BYTE, 2);
                short itemData = data.read(Type.SHORT, 3);

                ItemStack itemStack = new ItemStack(itemId, amount, itemData, new CompoundTag());

                List<WatchableObject> metadata = Collections.singletonList(new WatchableObject(
                        MetadataType.ITEM,
                        10,
                        itemStack
                ));

                PacketData itemMetadata = PacketUtil.createPacket(0x28, new TypeHolder[] {
                        data.read(0),
                        set(Type.V1_4R_METADATA, metadata)
                });

                session.sendPacket(vehicleSpawn, PacketDirection.SERVER_TO_CLIENT, ProtocolRelease51To39.class);
                session.sendPacket(itemMetadata, PacketDirection.SERVER_TO_CLIENT, ProtocolRelease51To39.class);

                return new PacketData(-1);
            }
        });

        addTranslator(24 /* MOB SPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {
                session.sendPacket(data, PacketDirection.SERVER_TO_CLIENT, ProtocolRelease51To39.class);

                byte type = data.read(Type.BYTE, 1);
                int itemId = 0;

                if (type == 51) {
                    itemId = 261; // bow
                } else if (type == 57) {
                    itemId = 283; // golden sword
                }

                ItemStack itemStack = new ItemStack(itemId, 1, 0, null);

                return PacketUtil.createPacket(0x05, new TypeHolder[] {
                        data.read(0),
                        set(Type.SHORT, (short) 0),
                        set(Type.V1_3R_ITEM, itemStack)
                });
            }
        });

        addTranslator(0xFA /* CUSTOM PAYLOAD */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {
                String channel = data.read(Type.STRING, 0);
                byte[] payload = data.read(Type.SHORT_BYTE_ARRAY, 1);

                if (channel.equals("MC|TrList")) {
                    ByteBuf buf = Unpooled.wrappedBuffer(payload);
                    ByteBuf fixedPayload = Unpooled.buffer();

                    fixedPayload.writeInt(buf.readInt());
                    short size = buf.readUnsignedByte();

                    fixedPayload.writeByte(size);

                    for (int i = 0; i < size; i++) {
                        V1_3_1RProtocol.ITEM.write(set(Type.V1_3R_ITEM, V1_3_1RProtocol.ITEM.read(buf)), fixedPayload);
                        V1_3_1RProtocol.ITEM.write(set(Type.V1_3R_ITEM, V1_3_1RProtocol.ITEM.read(buf)), fixedPayload);

                        boolean b = buf.readBoolean();
                        fixedPayload.writeBoolean(b);

                        if (b) {
                            V1_3_1RProtocol.ITEM.write(set(Type.V1_3R_ITEM, V1_3_1RProtocol.ITEM.read(buf)), fixedPayload);
                        }

                        fixedPayload.writeBoolean(false);
                    }

                    return PacketUtil.createPacket(0xFA, new TypeHolder[]{
                            data.read(0),
                            set(Type.SHORT_BYTE_ARRAY, fixedPayload.array())
                    });
                }

                return data;
            }
        });

        addTranslator(0x3E /* SOUND LEVEL */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                String soundName = data.read(Type.STRING, 0);
                String newSoundName = SoundMappings.getNewSoundName(soundName);

                if (newSoundName.isEmpty()) {

                    return new PacketData(-1);
                } else if (newSoundName.equals("-")) {

                    System.err.printf("Missing sound mapping for '%s'%n", soundName);

                    return new PacketData(-1);
                }

                return PacketUtil.createPacket(0x3E, new TypeHolder[]{
                        set(Type.STRING, newSoundName),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                });
            }
        });

        addTranslator(0x3D /* DOOR CHANGE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x3D, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.BOOLEAN, false)
                });
            }
        });

        addTranslator(0x38 /* CHUNK BULK */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                V1_3_4ChunkBulk oldChunk = data.read(Type.V1_3CHUNK_BULK, 0);

                oldChunk.setSkylight(session.getUserData().getDimension() == 0);

                return PacketUtil.createPacket(0x38, new TypeHolder[]{
                        set(Type.V1_4CHUNK_BULK, oldChunk)
                });
            }
        });
    }
}
