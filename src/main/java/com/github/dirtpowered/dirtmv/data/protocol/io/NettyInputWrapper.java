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

package com.github.dirtpowered.dirtmv.data.protocol.io;

import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import io.netty.buffer.ByteBuf;

public class NettyInputWrapper implements PacketInput {

    private ByteBuf buf;

    public NettyInputWrapper(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public int readVarInt() {
        int numRead = 0;
        int result = 0;

        byte read;
        do {
            read = buf.readByte();
            int value = (read & 0x7F);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0x80) != 0);

        return result;
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return buf.readUnsignedShort();
    }

    @Override
    public short readUnsignedByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public char readChar() {
        return buf.readChar();
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public byte[] readBytes(int length) {
        byte[] b = new byte[length];

        buf.readBytes(b, 0, length);
        return b;
    }

    @Override
    public ByteBuf getBuffer() {
        return buf;
    }
}
