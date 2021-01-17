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
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.network.server.codec.ChannelConstants;
import com.github.dirtpowered.dirtmv.network.server.codec.LegacyPingVersionHandler;
import com.github.dirtpowered.dirtmv.network.server.codec.PacketDecoder;
import com.github.dirtpowered.dirtmv.network.server.codec.PacketEncoder;
import com.github.dirtpowered.dirtmv.viaversion.handler.ViaDecodeHandler;
import com.github.dirtpowered.dirtmv.viaversion.handler.ViaEncodeHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;

public class DetectionHandler extends ChannelInboundHandlerAdapter {
    private final UserData userData;
    private final DirtMultiVersion main;

    public DetectionHandler(DirtMultiVersion main, UserData userData) {
        this.main = main;
        this.userData = userData;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) {
        ByteBuf buffer = (ByteBuf) object;
        try {
            short packetId = buffer.readUnsignedByte();

            if (packetId != 0x02 && packetId != 0xFE) {
                ctx.channel().pipeline().addAfter(
                        ChannelConstants.DETECTION_HANDLER,
                        ChannelConstants.NETTY_LENGTH_DECODER,
                        new VarIntFrameDecoder()
                ).addAfter(
                        ChannelConstants.NETTY_LENGTH_DECODER,
                        ChannelConstants.NETTY_LENGTH_ENCODER,
                        new VarIntFrameEncoder()
                ).addAfter(
                        ChannelConstants.NETTY_LENGTH_ENCODER,
                        ChannelConstants.NETTY_PACKET_DECODER,
                        new NettyPacketDecoder(main, userData, PacketDirection.TO_SERVER)
                ).addAfter(
                        ChannelConstants.NETTY_PACKET_DECODER,
                        ChannelConstants.NETTY_PACKET_ENCODER,
                        new NettyPacketEncoder()
                );

                if (main.getConfiguration().enableViaVersion()) {
                    UserConnection userConnection = new UserConnection(ctx.channel());
                    new ProtocolPipeline(userConnection);

                    ctx.channel().pipeline().addBefore(
                            ChannelConstants.NETTY_PACKET_DECODER,
                            ChannelConstants.VIA_DECODER,
                            new ViaDecodeHandler(userConnection)
                    );

                    ctx.channel().pipeline().addBefore(
                            ChannelConstants.NETTY_PACKET_DECODER,
                            ChannelConstants.VIA_ENCODER,
                            new ViaEncodeHandler(userConnection)
                    );
                }
            } else {
                ctx.channel().pipeline().addAfter(
                        ChannelConstants.DETECTION_HANDLER,
                        ChannelConstants.LEGACY_PING,
                        new LegacyPingVersionHandler(userData)
                ).addAfter(
                        ChannelConstants.LEGACY_PING,
                        ChannelConstants.LEGACY_DECODER,
                        new PacketDecoder(main, PacketDirection.TO_SERVER, userData)
                ).addAfter(
                        ChannelConstants.LEGACY_DECODER,
                        ChannelConstants.LEGACY_ENCODER,
                        new PacketEncoder()
                );
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            buffer.resetReaderIndex();

            ctx.channel().pipeline().remove(this);
            ctx.fireChannelRead(object);
        }
    }
}