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

package com.github.dirtpowered.dirtmv.network.handler;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.handler.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.handler.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.handler.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

public class ProtocolRelease23To22 extends ServerProtocol {

    public ProtocolRelease23To22() {
        super(MinecraftVersion.R1_1, MinecraftVersion.R1_0);
    }

    @Override
    public void registerTranslators() {
        addTranslator(0x01 /* LOGIN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (dir == PacketDirection.CLIENT_TO_SERVER) {

                    return PacketUtil.createPacket(MinecraftVersion.R1_0, 0x01, new TypeHolder[]{
                            data.read(0),
                            data.read(1),
                            data.read(2),
                            data.read(4),
                            data.read(5),
                            data.read(6),
                            data.read(7),
                            data.read(8),
                    });
                } else {

                    return PacketUtil.createPacket(MinecraftVersion.R1_1, 0x01, new TypeHolder[]{
                            data.read(0),
                            data.read(1),
                            data.read(2),
                            new TypeHolder(Type.STRING, "NORMAL"), // world type
                            data.read(3),
                            data.read(4),
                            data.read(5),
                            data.read(6),
                            data.read(7),
                    });
                }
            }
        });

        addTranslator(0x09 /* RESPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                if (dir == PacketDirection.CLIENT_TO_SERVER) {

                    return PacketUtil.createPacket(MinecraftVersion.R1_0, 0x09, new TypeHolder[]{
                            data.read(0),
                            data.read(1),
                            data.read(2),
                            data.read(3),
                            data.read(4),
                    });
                } else {

                    return PacketUtil.createPacket(MinecraftVersion.R1_1, 0x09, new TypeHolder[]{
                            data.read(0),
                            data.read(1),
                            data.read(2),
                            data.read(3),
                            data.read(4),
                            new TypeHolder(Type.STRING, "NORMAL"), // world type
                    });
                }
            }
        });
    }
}
