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

import com.github.dirtpowered.dirtmv.data.protocol.io.NettyInputWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.pmw.tinylog.Logger;

import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PacketDecompressor extends ByteToMessageDecoder {
    private final Inflater inflater = new Inflater();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() == 0)
            return;

        NettyInputWrapper inputWrapper = new NettyInputWrapper(byteBuf);
        int size = inputWrapper.readVarInt();

        if (size == 0) {
            list.add(byteBuf.readBytes(byteBuf.readableBytes()));
        } else {
            byte[] compressedData = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(compressedData);
            inflater.setInput(compressedData);

            byte[] data = new byte[size];
            try {
                inflater.inflate(data);
            } catch (DataFormatException e) {
                Logger.warn("Couldn't decompress packet: {}", e.getMessage());
            }

            list.add(Unpooled.wrappedBuffer(data));
            inflater.reset();
        }
    }
}