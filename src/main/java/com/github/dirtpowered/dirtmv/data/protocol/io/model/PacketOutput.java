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

package com.github.dirtpowered.dirtmv.data.protocol.io.model;

import io.netty.buffer.ByteBuf;

public interface PacketOutput {

    void writeInt(int intValue);

    void writeVarInt(int intValue);

    void writeByte(int byteValue);

    void writeBoolean(boolean booleanValue);

    void writeShort(int shortValue);

    void writeChar(int charValue);

    void writeLong(long longValue);

    void writeFloat(float floatValue);

    void writeDouble(double doubleValue);

    void writeBytes(byte[] byteArrayValue);

    void writeBytes(byte[] byteArray, int length);

    ByteBuf getBuffer();
}
