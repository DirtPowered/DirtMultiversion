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
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.utils.encryption.EncryptionUtils;
import lombok.SneakyThrows;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;

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

                PacketData encryptRequest = PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0xFD, new TypeHolder[]{
                        new TypeHolder(Type.STRING, "-"),
                        new TypeHolder(Type.SHORT_BYTE_ARRAY, key.getEncoded()),
                        new TypeHolder(Type.SHORT_BYTE_ARRAY, verify)
                });

                session.getUserData().setProxyRequest(encryptRequest);

                // server -> client
                session.sendPacket(encryptRequest, PacketDirection.SERVER_TO_CLIENT, ProtocolRelease39To29.class);

                return PacketUtil.createPacket(MinecraftVersion.R1_2_4, 0x02, new TypeHolder[]{
                        new TypeHolder(Type.STRING, username)
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
                PacketData response = PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0xFC, new TypeHolder[]{
                        new TypeHolder(Type.SHORT_BYTE_ARRAY, new byte[0]),
                        new TypeHolder(Type.SHORT_BYTE_ARRAY, new byte[0])
                });

                session.sendPacket(response, PacketDirection.SERVER_TO_CLIENT, ProtocolRelease39To29.class);

                // enable encryption
                EncryptionUtils.setEncryption(session.getChannel(), sharedKey);
                return new PacketData(-1); // cancel packet
            }
        });

        addTranslator(0xCD /* CLIENT COMMAND */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {
                byte command = (byte) data.read(0).getObject();

                if (command == 0x00) {
                    String username = session.getUserData().getUsername();

                    PacketData login = PacketUtil.createPacket(MinecraftVersion.R1_2_4, 0x01, new TypeHolder[]{
                            new TypeHolder(Type.INT, 29), // protocol version
                            new TypeHolder(Type.STRING, username),
                            new TypeHolder(Type.STRING, "NORMAL"),
                            new TypeHolder(Type.INT, 0),
                            new TypeHolder(Type.INT, 0),
                            new TypeHolder(Type.BYTE, 0),
                            new TypeHolder(Type.BYTE, 0),
                            new TypeHolder(Type.BYTE, 0),
                    });

                    session.sendPacket(login, PacketDirection.CLIENT_TO_SERVER, ProtocolRelease39To29.class);

                    return new PacketData(-1);
                } else {
                    //TODO: Respawn packet
                    return null;
                }
            }
        });

        addTranslator(0x01, /* LOGIN */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0x01, new TypeHolder[]{
                        data.read(0),
                        data.read(2),
                        new TypeHolder(Type.BYTE, data.read(3).getObject()),
                        new TypeHolder(Type.BYTE, data.read(4).getObject()),
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
                //TODO: Unload chunk

                return new PacketData(-1);
            }
        });

        addTranslator(0x33 /* MAP CHUNK */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                V1_2Chunk chunk = (V1_2Chunk) data.read(0).getObject();

                return PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0x33, new TypeHolder[]{
                        new TypeHolder(Type.V1_3_CHUNK, chunk)
                });
            }
        });

        addTranslator(0x35 /* BLOCK CHANGE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0x35, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        new TypeHolder(Type.SHORT, ((Byte) data.read(3).getObject()).shortValue()),
                        data.read(4)
                });
            }
        });

        addTranslator(0x67, /* SET SLOT */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(0x68, /* WINDOW ITEMS */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(0x1D, /* ENTITY DESTROY */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                int entityId = (int) data.read(0).getObject();

                return PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0x1D, new TypeHolder[]{
                        new TypeHolder(Type.BYTE_INT_ARRAY, new int[]{entityId})
                });
            }
        });
    }
}
