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

package com.github.dirtpowered.dirtmv.network.server.codec.netty;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyInputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.pmw.tinylog.Logger;

import java.util.List;

import static com.github.dirtpowered.dirtmv.data.utils.PacketUtil.readModernPacket;

public class NettyPacketDecoder extends ByteToMessageDecoder {

    private PacketDirection packetDirection;
    private UserData userData;
    private DirtMultiVersion main;

    public NettyPacketDecoder(DirtMultiVersion main, UserData userData, PacketDirection direction) {
        this.main = main;
        this.userData = userData;
        this.packetDirection = direction;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() == 0)
            return;

        PacketInput inputBuffer = new NettyInputWrapper(byteBuf);

        int i = inputBuffer.readVarInt();
        boolean flag = packetDirection == PacketDirection.TO_CLIENT;

        ProtocolState protocolState = userData.getProtocolState();

        if (protocolState == ProtocolState.HANDSHAKE) {
            int readerIndex = byteBuf.readerIndex();
            int protocol = inputBuffer.readVarInt();

            byteBuf.readerIndex(readerIndex);

            userData.setClientVersion(MinecraftVersion.fromNettyProtocolId(protocol));
        }

        PacketData packet;

        if (flag) {
            packet = readModernPacket(main.getConfiguration().getServerVersion(), protocolState, inputBuffer, packetDirection, i);
        } else {
            packet = readModernPacket(userData.getClientVersion(), protocolState, inputBuffer, packetDirection, i);
        }

        int readableBytes = byteBuf.readableBytes();

        if (readableBytes > 0) {
            byteBuf.skipBytes(readableBytes);
            Logger.warn("skipping {} bytes for packet id: {}, direction: {}", readableBytes, i, packetDirection);
        } else {
            list.add(packet);
        }
    }
}