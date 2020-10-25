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

package com.github.dirtpowered.dirtmv.network.server.codec;

import com.github.dirtpowered.dirtmv.data.Constants;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyInputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ReplayingDecoder<PacketData> {

    private PacketDirection packetDirection;
    private UserData userData;

    PacketDecoder(PacketDirection packetDirection, UserData userData) {
        this.packetDirection = packetDirection;
        this.userData = userData;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> list) throws IOException {
        boolean flag = packetDirection == PacketDirection.SERVER_TO_CLIENT;

        setUserProtocol(flag, buffer);
        PacketInput inputBuffer = new NettyInputWrapper(buffer);

        PacketData packet = PacketUtil.readPacket(flag ? Constants.REMOTE_SERVER_VERSION : userData.getClientVersion(), inputBuffer);

        setProtocolState(packet);
        list.add(packet);
    }

    private void setProtocolState(PacketData data) {
        if (userData.getProtocolState() == ProtocolState.PLAY)
            return;

        switch (data.getOpCode()) {
            case 0xFE:
                userData.setProtocolState(ProtocolState.PING);
                break;
            case 0x02:
                userData.setProtocolState(ProtocolState.HANDSHAKE);
                break;
            case 0x01:
                userData.setProtocolState(ProtocolState.LOGIN);
                break;
            case 0x06 /* spawn position */:
                userData.setProtocolState(ProtocolState.PLAY);
                break;
        }
    }

    private void setUserProtocol(boolean flag, ByteBuf buffer) {
        if (!flag && !userData.isProtocolDetected()) {
            buffer.markReaderIndex();

            MinecraftVersion clientVersion = userData.getClientVersion();
            int packetId = buffer.readUnsignedByte();

            if (packetId == 0x01 /* login */) {
                clientVersion = MinecraftVersion.fromProtocolVersion(buffer.readInt());

                userData.setProtocolDetected(true);
            } else if (packetId == 0x02 /* handshake */) {

                // 1.3+ client is sending protocol version in handshake packet
                int protocol = buffer.readByte();

                if (protocol != 0) {
                    clientVersion = MinecraftVersion.fromProtocolVersion(protocol);
                    userData.setProtocolDetected(true);
                }
            }

            buffer.resetReaderIndex();

            userData.setClientVersion(clientVersion);
        }
    }
}
