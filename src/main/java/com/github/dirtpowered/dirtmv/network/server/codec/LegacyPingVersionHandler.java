/*
 * Copyright (c) 2020-2022 Dirt Powered
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

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class LegacyPingVersionHandler extends ChannelInboundHandlerAdapter {

    private final static String CHANNEL_NAME = "MC|PingHost";
    private final UserData userData;

    public LegacyPingVersionHandler(UserData data) {
        this.userData = data;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) {
        ByteBuf buffer = (ByteBuf) object;

        try {
            if (buffer.readUnsignedByte() == 0xFE) {
                int i = buffer.readableBytes();

                if (i == 0) {
                    userData.setClientVersion(MinecraftVersion.B1_8_1);
                } else if (i == 1) {
                    if (buffer.readUnsignedByte() != 0x01) {
                        return;
                    }

                    userData.setClientVersion(MinecraftVersion.R1_4_6);
                } else {
                    if (buffer.readUnsignedByte() == 0x01 || buffer.readUnsignedByte() == 0xFA) {
                        int protocolVersion = -1;

                        buffer.skipBytes(Byte.BYTES);

                        int readerIndex = buffer.readerIndex();

                        String channelName = readString(buffer);
                        if (channelName != null) {
                            if (channelName.equals(CHANNEL_NAME)) {
                                buffer.skipBytes(Short.BYTES);

                                protocolVersion = buffer.readUnsignedByte();
                            }
                        }

                        buffer.readerIndex(readerIndex);

                        if (protocolVersion != -1) {
                            userData.setClientVersion(MinecraftVersion.fromRegistryId(protocolVersion));
                        } else {
                            // temp workaround
                            int[] versions = new int[]{73, 74, 78};
                            int index = new Random().nextInt(versions.length);

                            protocolVersion = versions[index];
                            userData.setClientVersion(MinecraftVersion.fromRegistryId(protocolVersion));
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            buffer.resetReaderIndex();
            ctx.channel().pipeline().remove(ChannelConstants.LEGACY_PING);
            ctx.fireChannelRead(object);
        }
    }

    private String readString(ByteBuf buffer) {
        int size = buffer.readShort() * Character.BYTES;
        if (!buffer.isReadable(size)) {
            return null;
        }

        String result = buffer.toString(buffer.readerIndex(), size, StandardCharsets.UTF_16BE);
        buffer.skipBytes(size);
        return result;
    }
}