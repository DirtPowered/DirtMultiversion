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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.type.arrays;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import io.netty.buffer.ByteBuf;

public class ByteArrayDataType extends DataType<byte[]> {

    public ByteArrayDataType(Type readDataType) {
        super(readDataType);
    }

    @Override
    public byte[] read(ByteBuf buffer) {
        byte[] bytes = new byte[0];

        if (getType() == Type.BYTE_BYTE_ARRAY) {
            bytes = new byte[buffer.readByte() & 255];

            buffer.readBytes(bytes);
        }

        return bytes;
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) {
        byte[] byteArray = (byte[]) typeHolder.getObject();

        if (getType() == Type.BYTE_BYTE_ARRAY) {

            buffer.writeByte(byteArray.length);
            buffer.writeBytes(byteArray);
        }
    }
}
