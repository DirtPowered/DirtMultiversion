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
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
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

        // 0xFF SC 0x40 (kick disconnect)
        addTranslator(0xFF, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                String legacyDisconnect = data.read(Type.STRING, 0);

                return PacketUtil.createPacket(0x40, new TypeHolder[]{
                        set(Type.V1_7_STRING, ChatUtils.legacyToJsonString(legacyDisconnect))
                });
            }
        });

        // 0x35 SC 0x23 (block change)
        addTranslator(0x35, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x23, new TypeHolder[] {
                        data.read(0), // x
                        set(Type.UNSIGNED_BYTE, data.read(Type.BYTE, 1)), // y
                        data.read(2), // z
                        set(Type.VAR_INT, data.read(Type.SHORT, 3)), // type
                        set(Type.UNSIGNED_BYTE, data.read(Type.BYTE, 4)) // data
                });
            }
        });

        // 0x18 SC 0x0F (spawn mob)
        addTranslator(0x18, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x0F, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)), // entity id
                        data.read(1), // type
                        data.read(2), // x
                        data.read(3), // y
                        data.read(4), // z
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        data.read(8),
                        data.read(9),
                        data.read(10),
                        set(Type.V1_7R_METADATA, data.read(Type.V1_4R_METADATA, 11))
                });
            }
        });

        // 0xC9 SC 0x38 (player tab entry)
        addTranslator(0xC9, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x38, new TypeHolder[]{
                        set(Type.V1_7_STRING, data.read(Type.STRING, 0)),
                        data.read(1),
                        data.read(2)
                });
            }
        });

        // 0x09 SC 0x07 (respawn)
        addTranslator(0x09, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x07, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        set(Type.V1_7_STRING, data.read(Type.STRING, 3))
                });
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

        // 0x47 SC 0x2C (spawn global entity)
        addTranslator(0x47, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x2C, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                });
            }
        });

        // 0x17 SC 0x0E (spawn vehicle -> spawn object)
        addTranslator(0x17, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x0E, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                });
            }
        });

        // 0x36 SC 0x24 (play note block -> block action)
        addTranslator(0x36, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x24, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.VAR_INT, data.read(Type.SHORT, 5)),
                });
            }
        });

        // 0x23 SC 0x19 (entity head look)
        addTranslator(0x23, 0x19, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x21 SC 0x17 (entity relative move look)
        addTranslator(0x21, 0x17, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x1E SC 0x14 (entity ground state)
        addTranslator(0x1E, 0x14, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0xC8 SC 0x37 (statistics) // TODO: translate
        addTranslator(0xC8, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x05 SC 0x04 (entity equipment)
        addTranslator(0x05, 0x04, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x08 SC 0x06 (health update)
        addTranslator(0x08, 0x06, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x04 SC 0x03 (update time)
        addTranslator(0x04, 0x03, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x06 SC 0x05 (spawn position)
        addTranslator(0x06, 0x05, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x33 SC 0x21 (chunk data)
        addTranslator(0x33, 0x21, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x34 SC 0x22 (multi block change)
        addTranslator(0x34, 0x22, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x1F SC 0x15 (entity relative move)
        addTranslator(0x1F, 0x15, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x1C SC 0x12 (entity velocity)
        addTranslator(0x1C, 0x12, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x1D SC 0x13 (entity destroy)
        addTranslator(0x1D, 0x13, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x20 SC 0x16 (entity look)
        addTranslator(0x20, 0x16, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x22 SC 0x18 (entity teleport)
        addTranslator(0x22, 0x18, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x01 CS 0x03 (chat)
        addTranslator(0x01, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x03, new TypeHolder[] {
                        set(Type.STRING, data.read(Type.V1_7_STRING, 0))
                });
            }
        });

        // 0x16 CS 0xCD (client status)
        addTranslator(0x16, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                if (data.read(Type.BYTE, 0) == 0x00) {
                    return PacketUtil.createPacket(0xCD, data.getObjects());
                } else {
                    return new PacketData(-1);
                }
            }
        });

        // 0x16 SC 0x0D (item collect)
        addTranslator(0x16, 0x0D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x67 SC 0x2F (inventory set slot)
        addTranslator(0x67, 0x2F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x68 SC 0x30 (inventory window items)
        addTranslator(0x68, 0x30, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x09 CS 0x10 (held slot change)
        addTranslator(0x09, 0x10, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x03 CS 0x0A (player ground state)
        addTranslator(0x03, 0x0A, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x04 CS 0x0B (player position)
        addTranslator(0x04, 0x0B, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x05 CS 0x0C (player look)
        addTranslator(0x05, 0x0C, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x06 CS 0x0D (player position look)
        addTranslator(0x06, 0x0D, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x07 CS 0x0E (block digging)
        addTranslator(0x07, 0x0E, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x17 CS 0xFA (custom payload)
        addTranslator(0x17, 0xFA, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x15 CS 0xCC (player settings)
        addTranslator(0x15, 0xCC, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x0A CS 0x12 (player animation)
        addTranslator(0x0A, 0x12, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x0D CS 0x65 (window close)
        addTranslator(0x0D, 0x65, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x08 CS 0x0F (block placement)
        addTranslator(0x08, 0x0F, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x0E CS 0x66 (click window)
        addTranslator(0x0E, 0x66, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);
    }
}
