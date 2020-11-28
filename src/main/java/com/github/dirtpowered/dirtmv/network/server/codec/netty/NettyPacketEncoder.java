package com.github.dirtpowered.dirtmv.network.server.codec.netty;

import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyOutputWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class NettyPacketEncoder extends MessageToMessageEncoder<PacketData> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, PacketData packetData, List<Object> list) throws Exception {
        ByteBuf packetId = Unpooled.buffer();

        NettyOutputWrapper outputWrapper = new NettyOutputWrapper(packetId);
        outputWrapper.writeVarInt(packetData.getOpCode());

        list.add(Unpooled.wrappedBuffer(packetId, packetData.toMessage().getBuffer()));
    }
}
