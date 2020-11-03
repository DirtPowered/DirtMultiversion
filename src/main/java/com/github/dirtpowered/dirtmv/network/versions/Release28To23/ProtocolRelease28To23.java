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

package com.github.dirtpowered.dirtmv.network.versions.Release28To23;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2MultiBlockArray;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BMultiBlockArray;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProtocolRelease28To23 extends ServerProtocol {

    public ProtocolRelease28To23() {
        super(MinecraftVersion.R1_2_1, MinecraftVersion.R1_1);
    }

    @Override
    public void registerTranslators() {
        // login
        addTranslator(0x01, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

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
        addTranslator(0x01, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(3),
                        data.read(4),
                        set(Type.INT, data.read(Type.BYTE, 5).intValue()),
                        data.read(6),
                        data.read(7),
                        data.read(8)
                });
            }
        });

        // chunk
        addTranslator(0x33, PacketDirection.SERVER_TO_CLIENT, new BetaToV1_2ChunkTranslator());

        // multi block change
        addTranslator(0x34, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                V1_3BMultiBlockArray blockArray = (V1_3BMultiBlockArray) data.read(2).getObject();

                int totalDataSize = 4 * blockArray.getSize();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(totalDataSize);
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

                for (int i = 0; i < blockArray.getSize(); ++i) {
                    short coordinate = blockArray.getCoordsArray()[i];

                    int itemId = blockArray.getTypesArray()[i] & 255;
                    int blockData = blockArray.getMetadataArray()[i];

                    dataOutputStream.writeShort(coordinate);
                    dataOutputStream.writeShort((short) ((itemId & 4095) << 4 | blockData & 15));
                }

                byte[] b = byteArrayOutputStream.toByteArray();

                V1_2MultiBlockArray newFormat = new V1_2MultiBlockArray(blockArray.getSize(), b.length, b);

                return PacketUtil.createPacket(0x34, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_2MULTIBLOCK_ARRAY, newFormat)
                });
            }
        });

        // mob spawn
        addTranslator(0x18, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

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
                        set(Type.BYTE, 0), // head yaw
                        data.read(7),
                });
            }
        });

        // respawn
        addTranslator(0x09, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x09, new TypeHolder[]{
                        set(Type.BYTE, data.read(0).getObject()),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.LONG, 0L), // seed
                        data.read(4)
                });
            }
        });

        // respawn
        addTranslator(0x09, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x09, new TypeHolder[]{
                        set(Type.INT, data.read(Type.BYTE, 0).intValue()),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(5)
                });
            }
        });

        // entity relative move look
        addTranslator(0x21, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {

                PacketData rotationPacket = PacketUtil.createPacket(0x23, new TypeHolder[]{
                        data.read(0), // entityId
                        data.read(4), // yaw
                });

                session.sendPacket(rotationPacket, PacketDirection.SERVER_TO_CLIENT, getFrom());
                return data;
            }
        });

        // entity look
        addTranslator(0x20, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {

                PacketData rotationPacket = PacketUtil.createPacket(0x23, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                });

                session.sendPacket(rotationPacket, PacketDirection.SERVER_TO_CLIENT, getFrom());
                return data;
            }
        });
    }
}
