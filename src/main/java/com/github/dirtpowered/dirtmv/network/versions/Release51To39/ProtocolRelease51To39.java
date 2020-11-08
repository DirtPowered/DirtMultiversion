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
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3.V1_3_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyInputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyOutputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.MetadataType;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Motion;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3_4ChunkBulk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.data.sound.SoundRemapper;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.mojang.nbt.CompoundTag;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class ProtocolRelease51To39 extends ServerProtocol {

    private SoundRemapper soundRemapper;

    public ProtocolRelease51To39() {
        super(MinecraftVersion.R1_4_6, MinecraftVersion.R1_3_1);

        soundRemapper = new SoundRemapper("1_3To1_4SoundMappings");
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
        // login
        addTranslator(0x01, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                session.getUserData().setDimension(data.read(Type.BYTE, 3));
                return data;
            }
        });

        // respawn
        addTranslator(0x09, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                session.getUserData().setDimension(data.read(Type.INT, 0));
                return data;
            }
        });

        // server ping request
        addTranslator(0xFE, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                // empty
                return PacketUtil.createPacket(0xFE, new TypeHolder[0]);
            }
        });

        // kick disconnect
        addTranslator(0xFF, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                if (session.getUserData().getPreNettyProtocolState() != PreNettyProtocolState.STATUS)
                    return data;

                String reason = data.read(Type.STRING, 0);

                // old to new format
                return PacketUtil.createPacket(0xFF, new TypeHolder[] {
                        set(Type.STRING, transformMotd(reason))
                });
            }
        });

        // handshake
        addTranslator(0x02, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
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

        // client settings
        addTranslator(0xCC, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0xCC, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                });
            }
        });

        // map data
        addTranslator(0x83, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                byte[] mapData = data.read(Type.BYTE_BYTE_ARRAY, 2);

                return PacketUtil.createPacket(0x83, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        set(Type.UNSIGNED_SHORT_BYTE_ARRAY, mapData)
                });
            }
        });

        // update time
        addTranslator(0x04, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x04, new TypeHolder[] {
                        data.read(0),
                        data.read(0)
                });
            }
        });

        // vehicle spawn
        addTranslator(0x17, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

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

        // item spawn
        addTranslator(0x15, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {

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
                        set(Type.V1_4R_METADATA, metadata.toArray(new WatchableObject[0]))
                });

                session.sendPacket(vehicleSpawn, PacketDirection.SERVER_TO_CLIENT, getFrom());
                session.sendPacket(itemMetadata, PacketDirection.SERVER_TO_CLIENT, getFrom());

                return new PacketData(-1);
            }
        });

        // mob spawn
        addTranslator(0x18, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                session.sendPacket(data, PacketDirection.SERVER_TO_CLIENT, getFrom());

                byte type = data.read(Type.BYTE, 1);
                int itemId = 0;

                if (type == 51)
                    itemId = 261; // bow

                if (type == 57)
                    itemId = 283; // golden sword


                if (itemId > 0) {
                    ItemStack itemStack = new ItemStack(itemId, 1, 0, null);

                    PacketData entityEquipment = PacketUtil.createPacket(0x05, new TypeHolder[]{
                            data.read(0),
                            set(Type.SHORT, (short) 0),
                            set(Type.V1_3R_ITEM, itemStack)
                    });

                    session.sendPacket(entityEquipment, PacketDirection.SERVER_TO_CLIENT, getFrom());
                }

                return new PacketData(-1);
            }
        });

        // custom payload
        addTranslator(0xFA, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                String channel = data.read(Type.STRING, 0);
                byte[] payload = data.read(Type.SHORT_BYTE_ARRAY, 1);

                if (channel.equals("MC|TrList")) {
                    // TODO: Custom Payload reader
                    NettyInputWrapper buf = new NettyInputWrapper(Unpooled.wrappedBuffer(payload));
                    NettyOutputWrapper fixedPayload = new NettyOutputWrapper(Unpooled.buffer());

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

        // level sound
        addTranslator(0x3E, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String soundName = data.read(Type.STRING, 0);
                String newSoundName = soundRemapper.getNewSoundName(soundName);

                if (newSoundName.isEmpty())
                    return new PacketData(-1);

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

        // door change
        addTranslator(0x3D, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

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

        // chunk bulk
        addTranslator(0x38, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                V1_3_4ChunkBulk oldChunk = data.read(Type.V1_3CHUNK_BULK, 0);

                oldChunk.setSkylight(session.getUserData().getDimension() == 0);

                return PacketUtil.createPacket(0x38, new TypeHolder[]{
                        set(Type.V1_4CHUNK_BULK, oldChunk)
                });
            }
        });
    }
}
