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
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release4To78.ping.ServerPing;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.ping.ServerMotd;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class ProtocolRelease4To78 extends ServerProtocol {

    public ProtocolRelease4To78() {
        super(MinecraftVersion.R1_7_2, MinecraftVersion.R1_6_4);
    }

    @Override
    public void registerTranslators() {
        addTranslator(0x00, /* HANDSHAKE */new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {
                if (session.getUserData().getProtocolState() == ProtocolState.PING) {
                    PacketData pingPacket = PacketUtil.createPacket(0xFE, new TypeHolder[]{
                            set(Type.BYTE, 1)
                    });

                    session.sendPacket(pingPacket, PacketDirection.CLIENT_TO_SERVER, getFrom());
                }

                return new PacketData(-1);
            }
        });

        addTranslator(0x01 /* PING REQUEST / PING RESPONSE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {
                if (dir == PacketDirection.CLIENT_TO_SERVER) {
                    PacketData response = PacketUtil.createPacket(0x01, new TypeHolder[] {
                            data.read(0)
                    });

                    session.sendPacket(response, PacketDirection.SERVER_TO_CLIENT, getFrom());
                }

                return new PacketData(-1);
            }
        });

        addTranslator(0xFF /* KICK DISCONNECT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (session.getUserData().getPreNettyProtocolState() != PreNettyProtocolState.STATUS) {
                    //TODO: kick disconnect message
                    return new PacketData(-1);
                }

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
    }
}
