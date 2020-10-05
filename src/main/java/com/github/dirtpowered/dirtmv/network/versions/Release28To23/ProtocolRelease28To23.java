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
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_2MultiBlockArray;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_7MultiBlockArray;
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
        addTranslator(0x01 /* LOGIN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (dir == PacketDirection.CLIENT_TO_SERVER) {

                    return PacketUtil.createPacket(MinecraftVersion.R1_1, 0x01, new TypeHolder[]{
                            new TypeHolder(Type.INT, 23),
                            data.read(1),
                            new TypeHolder(Type.LONG, 0L),
                            data.read(2),
                            data.read(3),
                            new TypeHolder(Type.BYTE, ((Integer) data.read(4).getObject()).byteValue()),
                            data.read(5),
                            data.read(6),
                            data.read(7)
                    });
                } else {

                    return PacketUtil.createPacket(MinecraftVersion.R1_2_1, 0x01, new TypeHolder[]{
                            data.read(0),
                            data.read(1),
                            data.read(3),
                            data.read(4),
                            new TypeHolder(Type.INT, ((Byte) data.read(5).getObject()).intValue()),
                            data.read(6),
                            data.read(7),
                            data.read(8)
                    });
                }
            }
        });

        addTranslator(0x33 /* CHUNK */, new BetaToV1_2ChunkTranslator());

        addTranslator(0x34 /* MULTI BLOCK CHANGE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {
                V1_7MultiBlockArray blockArray = (V1_7MultiBlockArray) data.read(2).getObject();

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

                return PacketUtil.createPacket(MinecraftVersion.R1_2_1, 0x34, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        new TypeHolder(Type.V1_2MULTIBLOCK_ARRAY, newFormat)
                });
            }
        });

        addTranslator(0x18 /* MOB SPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(MinecraftVersion.R1_2_1, 0x18, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        new TypeHolder(Type.BYTE, 0), // head yaw
                        data.read(7),
                });
            }
        });

        addTranslator(0x09 /* RESPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (dir == PacketDirection.CLIENT_TO_SERVER) {

                    return PacketUtil.createPacket(MinecraftVersion.R1_1, 0x09, new TypeHolder[]{
                            new TypeHolder(Type.BYTE, data.read(0).getObject()),
                            data.read(1),
                            data.read(2),
                            data.read(3),
                            new TypeHolder(Type.LONG, 0L), // seed
                            data.read(4),
                    });
                } else {

                    return PacketUtil.createPacket(MinecraftVersion.R1_2_1, 0x09, new TypeHolder[]{
                            new TypeHolder(Type.INT, ((Byte) data.read(0).getObject()).intValue()),
                            data.read(1),
                            data.read(2),
                            data.read(3),
                            data.read(5)
                    });
                }
            }
        });
    }
}
