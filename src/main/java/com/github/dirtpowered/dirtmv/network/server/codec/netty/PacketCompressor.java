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

import com.github.dirtpowered.dirtmv.data.protocol.io.NettyOutputWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.zip.Deflater;

public class PacketCompressor extends MessageToByteEncoder<ByteBuf> {
    private final int threshold;
    private final Deflater deflater;

    public PacketCompressor(int threshold) {
        this.threshold = threshold;
        this.deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf out) {
        NettyOutputWrapper outputWrapper = new NettyOutputWrapper(out);
        int packetSize = byteBuf.readableBytes();

        if (packetSize < threshold) {
            outputWrapper.writeVarInt(0);
            out.writeBytes(byteBuf);
        } else {
            byte[] bytes = new byte[packetSize];
            byteBuf.readBytes(bytes);

            outputWrapper.writeVarInt(packetSize);

            deflater.setInput(bytes);
            deflater.finish();

            byte[] compressedData = new byte[packetSize];
            int compressedSize = deflater.deflate(compressedData);

            out.writeBytes(compressedData, 0, compressedSize);
            deflater.reset();
        }
    }
}