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

package com.github.dirtpowered.dirtmv.network.versions.Release51To39;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

public class ProtocolRelease51To39 extends ServerProtocol {

    public ProtocolRelease51To39() {
        super(MinecraftVersion.R1_4_6, MinecraftVersion.R1_3_1);
    }

    private String transformMotd(String oldMessage) {
        String colorChar = "\u00a7";
        String splitChar = "\00";

        String[] oldParts = oldMessage.split(colorChar);

        return colorChar + "1"
                + splitChar + 51 // 1.4.7 protocol
                + splitChar + "1.4.7"
                + splitChar + oldParts[0]
                + splitChar + oldParts[1]
                + splitChar + oldParts[2];
    }

    @Override
    public void registerTranslators() {

        addTranslator(0xFE /* SERVER PING REQUEST */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                // empty
                return PacketUtil.createPacket(0xFE, new TypeHolder[0]);
            }
        });

        addTranslator(0xFF /* KICK DISCONNECT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                String reason = (String) data.read(0).getObject();
                if (reason.split("\u00a7").length != 3) {
                    return data;
                }

                // old to new format
                return PacketUtil.createPacket(0xFF, new TypeHolder[] {
                        set(Type.STRING, transformMotd(reason))
                });
            }
        });

        addTranslator(0x02 /* HANDSHAKE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x02, new TypeHolder[] {
                        set(Type.BYTE, 39),
                        data.read(1),
                        data.read(2),
                        data.read(3)
                });
            }
        });
    }
}
