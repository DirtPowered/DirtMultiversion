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

package com.github.dirtpowered.dirtmv.network.server.codec;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyInputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ReplayingDecoder<PacketData> {
    private final PacketDirection packetDirection;
    private final UserData userData;
    private final DirtMultiVersion main;

    public PacketDecoder(DirtMultiVersion main, PacketDirection packetDirection, UserData userData) {
        this.packetDirection = packetDirection;
        this.userData = userData;
        this.main = main;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> list) throws IOException {
        boolean flag = packetDirection == PacketDirection.TO_CLIENT;

        setUserProtocol(flag, buffer);
        PacketInput inputBuffer = new NettyInputWrapper(buffer);

        PacketData packet = PacketUtil.readPacket(flag ? main.getConfiguration().getServerVersion() : userData.getClientVersion(), inputBuffer);

        setProtocolState(packet);
        list.add(packet);
    }

    private void setProtocolState(PacketData data) {
        if (userData.getPreNettyProtocolState() == PreNettyProtocolState.IN_GAME)
            return;

        if (data.getOpCode() == 255 && packetDirection == PacketDirection.TO_CLIENT) {
            if (!userData.getClientVersion().isNettyProtocol()) {
                userData.setPreNettyProtocolState(PreNettyProtocolState.STATUS);
                return;
            }
        }

        switch (data.getOpCode()) {
            case 0xFE: // ping request
            case 0xFA: // custom payload
                userData.setPreNettyProtocolState(PreNettyProtocolState.STATUS);
                break;
            case 0x06 /* spawn position */:
                userData.setPreNettyProtocolState(PreNettyProtocolState.IN_GAME);
                break;
            default:
                userData.setPreNettyProtocolState(PreNettyProtocolState.LOGIN);
                break;
        }
    }

    private void setUserProtocol(boolean flag, ByteBuf buffer) {
        if (!flag && !userData.isProtocolDetected()) {
            buffer.markReaderIndex();

            MinecraftVersion clientVersion = userData.getClientVersion();
            int packetId = buffer.readUnsignedByte();

            if (packetId == 0x01 /* login */) {
                clientVersion = MinecraftVersion.fromRegistryId(buffer.readInt());
                userData.setProtocolDetected(true);
            } else if (packetId == 0x02 /* handshake */) {

                // 1.3+ client is sending protocol version in handshake packet
                int protocol = buffer.readByte();

                if (protocol != 0) {
                    clientVersion = MinecraftVersion.fromRegistryId(protocol);
                    userData.setProtocolDetected(true);
                }
            }

            buffer.resetReaderIndex();

            userData.setClientVersion(clientVersion);
        }
    }
}
