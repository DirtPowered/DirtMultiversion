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

package com.github.dirtpowered.dirtmv.data.protocol.io;

import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import io.netty.buffer.ByteBuf;

public class NettyOutputWrapper implements PacketOutput {

    private ByteBuf buf;

    public NettyOutputWrapper(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public void writeInt(int intValue) {
        buf.writeInt(intValue);
    }

    @Override
    public void writeVarInt(int intValue) {
        do {
            byte temp = (byte) (intValue & 0x7F);

            intValue >>>= 7;
            if (intValue != 0) {
                temp |= 0x80;
            }
            buf.writeByte(temp);
        } while (intValue != 0);
    }

    @Override
    public void writeByte(int byteValue) {
        buf.writeByte(byteValue);
    }

    @Override
    public void writeBoolean(boolean booleanValue) {
        buf.writeBoolean(booleanValue);
    }

    @Override
    public void writeShort(int shortValue) {
        buf.writeShort(shortValue);
    }

    @Override
    public void writeChar(int charValue) {
        buf.writeChar(charValue);
    }

    @Override
    public void writeLong(long longValue) {
        buf.writeLong(longValue);
    }

    @Override
    public void writeFloat(float floatValue) {
        buf.writeFloat(floatValue);
    }

    @Override
    public void writeDouble(double doubleValue) {
        buf.writeDouble(doubleValue);
    }

    @Override
    public void writeBytes(byte[] byteArrayValue) {
        buf.writeBytes(byteArrayValue);
    }

    @Override
    public void writeBytes(byte[] byteArray, int length) {
        buf.writeBytes(byteArray, 0, length);
    }

    @Override
    public ByteBuf getBuffer() {
        return buf;
    }

    public byte[] array() {
        return buf.array();
    }
}
