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

package com.github.dirtpowered.dirtmv.network.versions.Release4To78;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release4To78.ping.ServerPing;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.ping.ServerMotd;
import com.google.common.base.Charsets;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.UUID;

@Log4j2
public class ProtocolRelease4To78 extends ServerProtocol {

    public ProtocolRelease4To78() {
        super(MinecraftVersion.R1_7_2, MinecraftVersion.R1_6_4);
    }

    @Override
    public void registerTranslators() {

        // handshake
        addTranslator(0x00, ProtocolState.HANDSHAKE, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                UserData userData = session.getUserData();

                userData.setAddress(data.read(Type.V1_7_STRING, 1));
                userData.setPort(data.read(Type.UNSIGNED_SHORT, 2));

                userData.setProtocolState(ProtocolState.fromId(data.read(Type.VAR_INT, 3)));

                return new PacketData(-1);
            }
        });

        // server info request
        addTranslator(0x00, ProtocolState.STATUS, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0xFE, new TypeHolder[]{
                        set(Type.BYTE, 1)
                });
            }
        });

        // ping
        addTranslator(0x01, ProtocolState.STATUS, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                PacketData response = PacketUtil.createPacket(0x01, new TypeHolder[]{
                        data.read(0)
                });

                session.sendPacket(response, PacketDirection.SERVER_TO_CLIENT, getFrom());
                return new PacketData(-1);
            }
        });

        // kick disconnect
        addTranslator(0xFF, ProtocolState.STATUS, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ServerMotd motd = ServerMotd.deserialize(data.read(Type.STRING, 0));

                ServerPing serverPing = new ServerPing();
                ServerPing.Version version = new ServerPing.Version();
                ServerPing.Players players = new ServerPing.Players();

                serverPing.setDescription(motd.getMotd());
                version.setName("1.7.2");
                version.setProtocol(4);
                players.setMax(motd.getMax());
                players.setOnline(motd.getOnline());

                serverPing.setVersion(version);
                serverPing.setPlayers(players);

                return PacketUtil.createPacket(0x00, new TypeHolder[]{
                        set(Type.V1_7_STRING, serverPing.toString())
                });
            }
        });

        // login start
        addTranslator(0x00, ProtocolState.LOGIN, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                UserData userData = session.getUserData();
                String username = data.read(Type.V1_7_STRING, 0);

                // handshake
                PacketData handshake = PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.BYTE, 78), // protocol version
                        set(Type.STRING, username),
                        set(Type.STRING, userData.getAddress()),
                        set(Type.INT, userData.getPort())
                });

                PacketData clientCommand = PacketUtil.createPacket(0xCD, new TypeHolder[]{
                        set(Type.BYTE, (byte) 0)
                });

                userData.setUsername(username);
                session.sendPacket(handshake, PacketDirection.CLIENT_TO_SERVER, getFrom());

                // client command
                session.sendPacket(clientCommand, PacketDirection.CLIENT_TO_SERVER, getFrom());

                return new PacketData(-1);
            }
        });

        // encryption
        addTranslator(0xFD, ProtocolState.LOGIN, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                UserData userData = session.getUserData();
                String username = userData.getUsername();

                String uuidStr = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8)).toString();

                PacketData loginSuccess = PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.V1_7_STRING, uuidStr),
                        set(Type.V1_7_STRING, username)
                });

                session.sendPacket(loginSuccess, PacketDirection.SERVER_TO_CLIENT, getFrom());
                userData.setProtocolState(ProtocolState.PLAY);

                return new PacketData(-1);
            }
        });

        // pre-netty login
        addTranslator(0x01, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        data.read(0), // entity id
                        set(Type.UNSIGNED_BYTE, 0),
                        set(Type.BYTE, (byte) 0),
                        set(Type.UNSIGNED_BYTE, 0),
                        set(Type.UNSIGNED_BYTE, 20),
                        set(Type.V1_7_STRING, data.read(Type.STRING, 1))
                });
            }
        });

        // 0x03 SC 0x02 (chat)
        addTranslator(0x03, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.V1_7_STRING, data.read(Type.STRING, 0))
                });
            }
        });

        // 0x04 SC 0x03 (update time)
        addTranslator(0x04, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x03, data.getObjects());
            }
        });

        // 0x06 SC 0x05 (spawn position)
        addTranslator(0x06, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x05, data.getObjects());
            }
        });

        // 0x33 SC 0x21 (chunk data)
        addTranslator(0x33, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x21, data.getObjects());
            }
        });

        // 0x0D SC 0x08 (player pos look)
        addTranslator(0x0D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x08, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6)
                });
            }
        });

        // TODO: translate packets
        addTranslator(21, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(4, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(5, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(3, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(6, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return new PacketData(-1);
            }
        });

        addTranslator(23, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return new PacketData(-1);
            }
        });
    }
}
