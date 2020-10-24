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

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LegacyPingVersionHandler extends ChannelInboundHandlerAdapter {

    private UserData userData;

    LegacyPingVersionHandler(UserData data) {
        this.userData = data;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) {
        ByteBuf buffer = (ByteBuf) object;

        if (buffer.readUnsignedByte() == 254) {
            int i = buffer.readableBytes();

            if (i == 0) {
                userData.setClientVersion(MinecraftVersion.B1_8_1);

                close(ctx, buffer);
            } else if (i == 1) {
                if (buffer.readUnsignedByte() != 1) {
                    return;
                }

                userData.setClientVersion(MinecraftVersion.R1_4_6);

                close(ctx, buffer);
            } else {
                if (buffer.readUnsignedByte() != 0x01 || buffer.readUnsignedByte() != 0xFA) {
                    return;
                }

                // TODO: get protocol version
                userData.setClientVersion(MinecraftVersion.R1_6_1);

                close(ctx, buffer);
            }
        } else {
            close(ctx, buffer);
        }
    }

    private void close(ChannelHandlerContext ctx, ByteBuf object) {
        object.resetReaderIndex();

        ctx.channel().pipeline().remove(this);
        ctx.fireChannelRead(object);
    }
}