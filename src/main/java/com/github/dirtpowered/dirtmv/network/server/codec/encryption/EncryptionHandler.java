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

package com.github.dirtpowered.dirtmv.network.server.codec.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

class EncryptionHandler {
    private Cipher cipher;

    EncryptionHandler(Cipher cipher) {
        this.cipher = cipher;
    }

    private byte[] readData(ByteBuf byteBuf) {
        int size = byteBuf.readableBytes();
        byte[] bytes = new byte[byteBuf.readableBytes()];

        if (bytes.length < size) {
            bytes = new byte[size];
        }

        byteBuf.readBytes(bytes, 0, size);
        return bytes;
    }

    ByteBuf decrypt(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws ShortBufferException {
        int size = byteBuf.readableBytes();

        byte[] bytes = readData(byteBuf);

        ByteBuf buffer = channelHandlerContext.alloc().heapBuffer(cipher.getOutputSize(size));
        buffer.writerIndex(cipher.update(bytes, 0, size, buffer.array(), buffer.arrayOffset()));
        return buffer;
    }

    void encrypt(ByteBuf buf, ByteBuf byteBuf) throws ShortBufferException {
        byte[] data = new byte[0];
        int size = buf.readableBytes();

        byte[] bytes = readData(buf);
        int outputSize = cipher.getOutputSize(size);

        if (data.length < outputSize) {
            data = new byte[outputSize];
        }

        byteBuf.writeBytes(data, 0, cipher.update(bytes, 0, size, data));
    }
}
