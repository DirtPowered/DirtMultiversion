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


import com.github.dirtpowered.dirtmv.data.protocol.io.NettyInputWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class VarIntFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        if (!buffer.isReadable()) {
            return;
        }

        NettyInputWrapper inputWrapper = new NettyInputWrapper(buffer);

        int index = buffer.readerIndex();
        for (int i = 0; i < 3; i++) {
            if (!buffer.isReadable()) {
                buffer.readerIndex(index);
                return;
            }

            byte read = buffer.readByte();
            if (read >= 0) {
                buffer.readerIndex(index);
                int packetLength = inputWrapper.readVarInt();
                if (packetLength == 0) {
                    return;
                }

                if (buffer.readableBytes() < packetLength) {
                    buffer.readerIndex(index);
                    return;
                }

                out.add(buffer.readRetainedSlice(packetLength));
                return;
            }
        }

        throw new CorruptedFrameException("VarInt too big");
    }
}
