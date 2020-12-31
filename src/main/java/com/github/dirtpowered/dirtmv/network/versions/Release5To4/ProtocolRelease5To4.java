/*
 * Copyright (c) 2020-2021 Dirt Powered
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

package com.github.dirtpowered.dirtmv.network.versions.Release5To4;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release4To78.ping.ServerPing;
import com.google.gson.Gson;

import java.util.List;

public class ProtocolRelease5To4 extends ServerProtocol {

    public ProtocolRelease5To4() {
        super(MinecraftVersion.R1_7_6, MinecraftVersion.R1_7_2);
    }

    private String dashedFromTrimmedUUID(String trim) {
        return trim.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
    }

    @Override
    public void registerTranslators() {
        // status ping
        addTranslator(0x00, ProtocolState.STATUS, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String json = data.read(Type.V1_7_STRING, 0);
                ServerPing serverPing = new Gson().fromJson(json, ServerPing.class);

                ServerPing.Version versionObj = new ServerPing.Version();
                versionObj.setName("1.7.10");
                versionObj.setProtocol(5);

                serverPing.setVersion(versionObj);

                List<ServerPing.Player> samplePlayers = serverPing.getPlayers().getSample();

                if (samplePlayers != null) {
                    for (ServerPing.Player player : samplePlayers) {
                        player.setId(dashedFromTrimmedUUID(player.getId()));
                    }
                }

                return PacketUtil.createPacket(0x00, new TypeHolder[]{
                        set(Type.V1_7_STRING, serverPing.toString())
                });
            }
        });

        // login success
        addTranslator(0x02, ProtocolState.LOGIN, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String trimmedUUID = data.read(Type.V1_7_STRING, 0);

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.V1_7_STRING, dashedFromTrimmedUUID(trimmedUUID)),
                        data.read(1)
                });
            }
        });

        // spawn player
        addTranslator(0x0C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String trimmedUUID = data.read(Type.V1_7_STRING, 1);

                return PacketUtil.createPacket(0x0C, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_7_STRING, dashedFromTrimmedUUID(trimmedUUID)),
                        data.read(2),
                        set(Type.VAR_INT, 0),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        data.read(8),
                        data.read(9),
                });
            }
        });
    }
}
