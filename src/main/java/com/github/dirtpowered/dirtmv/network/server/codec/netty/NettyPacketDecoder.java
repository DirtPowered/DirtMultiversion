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

package com.github.dirtpowered.dirtmv.network.server.codec.netty;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7.V1_7_2RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyInputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.data.utils.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;

@Log4j2
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
        if (byteBuf.readableBytes() == 0) {
            return;
        }

        PacketInput inputBuffer = new NettyInputWrapper(byteBuf);

        int i = inputBuffer.readVarInt();

        ProtocolState protocolState = userData.getProtocolState();

        PacketData packet = read1_7Packet(protocolState, inputBuffer, packetDirection, i);

        if (protocolState == ProtocolState.HANDSHAKE) {
            int protocol = packet.read(Type.VAR_INT, 0);

            userData.setClientVersion(MinecraftVersion.fromNettyProtocolId(protocol));
        }

        if (main.getConfiguration().getServerVersion().isNettyProtocol())
            handleProtocolState(protocolState, packet);

        int readableBytes = byteBuf.readableBytes();

        if (readableBytes > 0) {
            byteBuf.skipBytes(readableBytes);
            log.warn("skipping {} bytes for packet id: {}, direction: {}", readableBytes, i, packetDirection);
        } else {
            list.add(packet);
        }
    }

    private void handleProtocolState(ProtocolState protocolState, PacketData data) {
        int packetId = data.getOpCode();

        switch (protocolState) {
            case HANDSHAKE:
                ProtocolState nextState = ProtocolState.fromId(data.read(Type.VAR_INT, 3));
                userData.setProtocolState(nextState);
                break;
            case LOGIN:
                if (packetId == 0x02 && packetDirection == PacketDirection.SERVER_TO_CLIENT)
                    userData.setProtocolState(ProtocolState.PLAY);
                break;
        }
    }

    private PacketData read1_7Packet(ProtocolState protocolState, PacketInput buffer, PacketDirection packetDirection, int packetId) throws IOException {
        DataType[] parts = V1_7_2RProtocol.STATE_DEPENDED_PROTOCOL.getInstruction(packetId, protocolState, packetDirection);

        if (parts == null) {
            log.warn("Unknown packet id {} ({}), state: {}, direction: {}",
                    StringUtils.intToHexStr(packetId), packetId, protocolState, packetDirection);

            return new PacketData(0);
        }

        TypeHolder[] typeHolders = new TypeHolder[parts.length];

        int i = 0;

        while (i < parts.length) {
            DataType dataType = parts[i];

            typeHolders[i] = new TypeHolder(dataType.getType(), dataType.read(buffer));
            i++;
        }

        return new PacketData(packetId, typeHolders);
    }
}