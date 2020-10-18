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

package com.github.dirtpowered.dirtmv.network.versions.Beta17To14;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

import java.io.IOException;

public class ProtocolBeta17to14 extends ServerProtocol {

    public ProtocolBeta17to14() {
        super(MinecraftVersion.B1_8_1, MinecraftVersion.B1_7_3);
    }

    @Override
    public void registerTranslators() {

        addTranslator(0xFE /* PING REQUEST */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) throws IOException {

                PacketData packetData = PacketUtil.createPacket(0xFF, new TypeHolder[]{
                        set(Type.STRING, "A Minecraft Server§0§20")
                });

                session.sendPacket(packetData, PacketDirection.SERVER_TO_CLIENT, ProtocolBeta17to14.class);
                return new PacketData(-1); // cancel sending
            }
        });

        addTranslator(0x01 /* LOGIN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (dir == PacketDirection.CLIENT_TO_SERVER) {

                    return PacketUtil.createPacket(0x01, new TypeHolder[]{
                            set(Type.INT, 14), // INT
                            data.read(1), // STRING
                            data.read(2), // LONG
                            data.read(4) // BYTE
                    });
                } else {

                    return PacketUtil.createPacket(0x01, new TypeHolder[]{
                            data.read(0), // INT - entityId
                            data.read(1), // STRING - empty
                            data.read(2), // LONG - world seed
                            set(Type.INT, 0), // INT - gameMode
                            data.read(3), // BYTE - dimension
                            set(Type.BYTE, 0), // BYTE - difficulty
                            set(Type.BYTE, -128), // BYTE - world height
                            set(Type.BYTE, 20), // BYTE - maxPlayers
                    });
                }
            }
        });

        addTranslator(0x08 /* UPDATE HEALTH */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x08, new TypeHolder[]{
                        data.read(0),
                        set(Type.SHORT, 6),
                        set(Type.FLOAT, 0.0F),

                });
            }
        });

        addTranslator(0x09 /* RESPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                PacketData packetData;

                if (dir == PacketDirection.CLIENT_TO_SERVER) {
                    packetData = PacketUtil.createPacket(0x09, new TypeHolder[]{
                            data.read(0),

                    });
                } else {
                    packetData = PacketUtil.createPacket(0x09, new TypeHolder[]{
                            data.read(0),
                            set(Type.BYTE, 0),
                            set(Type.BYTE, 0),
                            set(Type.SHORT, 128),
                            set(Type.LONG, 0),
                    });
                }

                return packetData;
            }
        });

        addTranslator(0x64 /* OPEN WINDOW */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x64, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.STRING, data.read(Type.STRING, 2)),
                        data.read(3)
                });
            }
        });

        addTranslator(0x46 /* GAME-MODE / WEATHER */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x46, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, 0)
                });
            }
        });

        addTranslator(0x13 /* ENTITY ACTION */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                byte state = data.read(Type.BYTE, 1);

                if (state == 5 || state == 4) { // sprinting (stop/start)
                    return new PacketData(-1); // cancel sending
                }

                return data;
            }
        });
    }
}
