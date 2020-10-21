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

package com.github.dirtpowered.dirtmv.network.versions.Release73To61;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.ping.ServerMotd;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ProtocolRelease73To61 extends ServerProtocol {

    public ProtocolRelease73To61() {
        super(MinecraftVersion.R1_6_1, MinecraftVersion.R1_5_2);
    }

    @Override
    public void registerTranslators() {
        addTranslator(0xFA /* CUSTOM PAYLOAD */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                String channel = data.read(Type.STRING, 0);
                byte[] payload = data.read(Type.SHORT_BYTE_ARRAY, 1);

                if (channel.equals("MC|PingHost")) {
                    ByteBuf buf = Unpooled.wrappedBuffer(payload);

                    int protocolId = buf.readUnsignedByte();
                    session.getUserData().setClientVersion(MinecraftVersion.fromProtocolVersion(protocolId));
                }
                return data;
            }
        });

        addTranslator(0xFF /* KICK DISCONNECT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                String reason = data.read(Type.STRING, 0);
                if (reason.split("\00").length != 6) {
                    return data;
                }

                ServerMotd pingMessage = ServerMotd.deserialize(reason);
                pingMessage.setProtocol(session.getUserData().getClientVersion().getProtocolId());

                return PacketUtil.createPacket(0xFF, new TypeHolder[] {
                        set(Type.STRING, ServerMotd.serialize(pingMessage))
                });
            }
        });
    }
}
