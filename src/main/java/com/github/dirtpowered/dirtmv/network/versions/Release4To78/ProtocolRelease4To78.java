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
import com.github.dirtpowered.dirtmv.data.sound.SoundRemapper;
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

    private SoundRemapper soundRemapper;

    public ProtocolRelease4To78() {
        super(MinecraftVersion.R1_7_2, MinecraftVersion.R1_6_4);

        soundRemapper = new SoundRemapper("1_6To1_7SoundMappings");
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

                String address = "localhost";
                int port = 25565;

                return PacketUtil.createPacket(0xFE, new TypeHolder[] {
                        set(Type.UNSIGNED_BYTE, 1),
                        set(Type.UNSIGNED_BYTE, 0xFA),
                        set(Type.STRING, "MC|PingHost"),
                        set(Type.SHORT, 3 + 2 * address.length() + 4),
                        set(Type.BYTE, (byte) 78),
                        set(Type.STRING, address),
                        set(Type.INT, port)
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
                String message = ChatUtils.fixTranslationComponent(data.read(Type.STRING, 0));

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.V1_7_STRING, message)
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
                        set(Type.UNSIGNED_BYTE, (int) data.read(Type.BYTE, 1)), // y
                        data.read(2), // z
                        set(Type.VAR_INT, (int) data.read(Type.SHORT, 3)), // type
                        set(Type.UNSIGNED_BYTE, (int) data.read(Type.BYTE, 4)) // data
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
                        set(Type.V1_7_STRING, data.read(Type.STRING, 4))
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

        // 0x28 SC 0x1C (entity metadata)
        addTranslator(0x28, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x1C, new TypeHolder[] {
                        data.read(0),
                        set(Type.V1_7R_METADATA, data.read(Type.V1_4R_METADATA, 1))
                });
            }
        });

        // 0x46 SC 0x2B (game event)
        addTranslator(0x46, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int reason = data.read(Type.BYTE, 0);

                if (reason == 1) {
                    reason = 2;
                } else if (reason == 2) {
                    reason = 1;
                }

                return PacketUtil.createPacket(0x2B, new TypeHolder[] {
                        set(Type.UNSIGNED_BYTE, reason),
                        set(Type.FLOAT, data.read(Type.BYTE, 1).floatValue())
                });
            }
        });

        // 0x64 SC 0x2D (open window) // TODO: optional horse data
        addTranslator(0x64, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x2D, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        set(Type.V1_7_STRING, data.read(Type.STRING, 2)),
                        data.read(3),
                        data.read(4)
                });
            }
        });

        // 0x3C SC 0x27 (explosion)
        addTranslator(0x3C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x27, new TypeHolder[] {
                        set(Type.FLOAT, data.read(Type.DOUBLE, 0).floatValue()),
                        set(Type.FLOAT, data.read(Type.DOUBLE, 1).floatValue()),
                        set(Type.FLOAT, data.read(Type.DOUBLE, 2).floatValue()),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                });
            }
        });

        // 0x3E SC 0x29 (level sound)
        addTranslator(0x3E, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String soundName = data.read(Type.STRING, 0);
                String newSoundName = soundRemapper.getNewSoundName(soundName);

                if (newSoundName.isEmpty())
                    return new PacketData(-1);

                return PacketUtil.createPacket(0x29, new TypeHolder[] {
                        set(Type.V1_7_STRING, soundName),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                });
            }
        });

        // 0x14 CS 0x0C (named entity spawn)
        addTranslator(0x14, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String username = data.read(Type.STRING, 1);

                return PacketUtil.createPacket(0x0C, new TypeHolder[] {
                        set(Type.VAR_INT, data.read(0)), // entity id
                        set(Type.V1_7_STRING, "bananas"), // player UUID,
                        set(Type.V1_7_STRING, username), // player name
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        set(Type.V1_7R_METADATA, data.read(Type.V1_4R_METADATA, 8))
                });
            }
        });

        // 0x82 CS 0x33 (update sign)
        addTranslator(0x82, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x33, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        set(Type.V1_7_STRING, data.read(Type.STRING, 3)),
                        set(Type.V1_7_STRING, data.read(Type.STRING, 4)),
                        set(Type.V1_7_STRING, data.read(Type.STRING, 5)),
                        set(Type.V1_7_STRING, data.read(Type.STRING, 6)),
                });
            }
        });

        // 0x1A SC 0x11 (spawn experience orb)
        addTranslator(0x1A, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x11, new TypeHolder[] {
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                });
            }
        });

        // 0x19 SC 0x10 (spawn painting)
        addTranslator(0x19, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x10, new TypeHolder[] {
                      set(Type.VAR_INT, data.read(Type.INT, 0)),
                      set(Type.V1_7_STRING, data.read(Type.STRING, 1)),
                      data.read(2),
                      data.read(3),
                      data.read(4),
                      data.read(5),
                });
            }
        });

        // 0x10 SC 0x09 (held slot change)
        addTranslator(0x10, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x09, new TypeHolder[] {
                        set(Type.BYTE, data.read(Type.SHORT, 0))
                });
            }
        });

        // 0xCB SC 0x3A (tab complete)
        addTranslator(0xCB, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String arr = data.read(Type.STRING,0);
                String[] commands = arr.split( "\0" );

                TypeHolder[] types = new TypeHolder[commands.length + 1];
                types[0] = set(Type.VAR_INT, commands.length);

                for (int i = 0; i < commands.length; i++) {
                    String command = commands[i];
                    types[i + 1] = set(Type.V1_7_STRING, command);
                }

                return PacketUtil.createPacket(0x3A, types);
            }
        });

        // 0x37 SC 0x25 (block break animation)
        addTranslator(0x37, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x25, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                });
            }
        });

        // 0x69 SC 0x31 (update progress bar -> window property)
        addTranslator(0x69, 0x31, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x65 SC 0x2E (window close)
        addTranslator(0x65, 0x2E, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x29 SC 0x1D (entity effect)
        addTranslator(0x29, 0x1D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x2A SC 0x1E (clear entity effect)
        addTranslator(0x2A, 0x1E, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x84 SC 0x35 (update tile entity)
        addTranslator(0x84, 0x35, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x6A SC 0x32 (inventory transaction)
        addTranslator(0x6A, 0x32, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0xCA SC 0x39 (player abilities)
        addTranslator(0xCA, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x2B SC 0x1F (set experience)
        addTranslator(0x2B, 0x1F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x38 SC 0x26 (chunk bulk)
        addTranslator(0x38, 0x26, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x83 SC 0x34 (map data) // TODO: translate
        addTranslator(0x83, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x26 SC 0x1A (entity status)
        addTranslator(0x26, 0x1A, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

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

        // 0x16 SC 0x0D (item collect)
        addTranslator(0x16, 0x0D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x67 SC 0x2F (inventory set slot)
        addTranslator(0x67, 0x2F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x68 SC 0x30 (inventory window items)
        addTranslator(0x68, 0x30, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x11 CS 0x0A (use bed)
        addTranslator(0x11, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x27 CS 0x1B (entity attach)
        addTranslator(0x27, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // 0x3D CS 0x28 (door change)
        addTranslator(0x3D, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

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
                int status = data.read(Type.BYTE, 0);

                if (status == 0) {
                    return PacketUtil.createPacket(0xCD, new TypeHolder[]{
                            set(Type.BYTE, 1) // perform respawn
                    });
                } else {
                    return new PacketData(-1);
                }
            }
        });

        // 0x02 SC 0x07 (use entity)
        addTranslator(0x02, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x07, new TypeHolder[] {
                        set(Type.INT, 0),
                        data.read(0),
                        data.read(1)
                });
            }
        });

        // 0x14 CS 0xCB (tab complete)
        addTranslator(0x14, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0xCB, new TypeHolder[] {
                        set(Type.STRING, data.read(Type.V1_7_STRING, 0))
                });
            }
        });

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

        // 0x0A CS 0x12 (player animation)
        addTranslator(0x0A, 0x12, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x0D CS 0x65 (window close)
        addTranslator(0x0D, 0x65, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x08 CS 0x0F (block placement)
        addTranslator(0x08, 0x0F, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x0E CS 0x66 (click window)
        addTranslator(0x0E, 0x66, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x15 CS 0xCC (player settings)
        addTranslator(0x15, -1, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x17 CS 0xFA (custom payload)
        addTranslator(0x17, -1, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);

        // 0x0B CS 0x13 (entity action)
        addTranslator(0x0B, -1, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER);
    }
}
