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

package com.github.dirtpowered.dirtmv.network.versions.handler;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import org.pmw.tinylog.Logger;

public class GlobalProtocolHandler extends ServerProtocol {

    public GlobalProtocolHandler(MinecraftVersion from, MinecraftVersion to) {
        super(from, to);
    }

    @Override
    public void registerTranslators() {
        // chat (pre-netty)
        addTranslator(0x03, ProtocolState.PRE_NETTY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int clientVersion = session.getUserData().getClientVersion().getRegistryId();
                String message = clientVersion <= 10 ? data.read(Type.UTF8_STRING, 0) : data.read(Type.STRING, 0);

                onChat(session, message);
                return data;
            }
        });

        // chat (post-netty)
        addTranslator(0x01, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String message = data.read(Type.V1_7_STRING, 0);

                onChat(session, message);
                return data;
            }
        });

        // login (pre-netty)
        addTranslator(0x01, ProtocolState.PRE_NETTY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int clientVersion = session.getUserData().getClientVersion().getRegistryId();
                String username = clientVersion <= 10 ? data.read(Type.UTF8_STRING, 1) : data.read(Type.STRING, 1);

                session.getUserData().setUsername(username);
                return data;
            }
        });

        // login (post-netty)
        addTranslator(0x00, ProtocolState.LOGIN, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                session.getUserData().setUsername(data.read(Type.V1_7_STRING, 0));
                return data;
            }
        });
    }

    private void onChat(ServerSession session, String message) {
        Logger.info("{}: {}", session.getUserData().getUsername(), message);
    }
}
