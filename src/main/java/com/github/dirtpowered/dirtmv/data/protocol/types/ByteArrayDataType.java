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

package com.github.dirtpowered.dirtmv.data.protocol.types;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.TypeObject;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.google.common.base.Preconditions;

import java.io.IOException;

public class ByteArrayDataType extends DataType<byte[]> {

    public ByteArrayDataType(TypeObject readDataType) {
        super(readDataType);
    }

    @Override
    public byte[] read(PacketInput packetInput) {
        byte[] bytes = new byte[0];

        if (getType() == Type.BYTE_BYTE_ARRAY) {
            int size = packetInput.readByte() & 255;

            bytes = packetInput.readBytes(size);
        } else if (getType() == Type.SHORT_BYTE_ARRAY) {
            int size = packetInput.readShort();

            Preconditions.checkArgument(size < 32767, "Payload too big");

            bytes = packetInput.readBytes(size);
        } else if (getType() == Type.UNSIGNED_SHORT_BYTE_ARRAY) {
            int size = packetInput.readUnsignedShort();

            bytes = packetInput.readBytes(size);
        } else if (getType() == Type.INT_BYTE_ARRAY) {
            int size = packetInput.readInt();

            bytes = packetInput.readBytes(size);
        } else if (getType() == Type.VAR_INT_BYTE_ARRAY) {
            int size = packetInput.readVarInt();

            bytes = packetInput.readBytes(size);
        }

        return bytes;
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput buffer) throws IOException {
        byte[] byteArray = (byte[]) typeHolder.getObject();

        if (getType() == Type.BYTE_BYTE_ARRAY) {

            buffer.writeByte(byteArray.length);
            buffer.writeBytes(byteArray);
        } else if (getType() == Type.SHORT_BYTE_ARRAY || getType() == Type.UNSIGNED_SHORT_BYTE_ARRAY) {

            buffer.writeShort(byteArray.length);
            buffer.writeBytes(byteArray);
        } else if (getType() == Type.INT_BYTE_ARRAY) {

            buffer.writeInt(byteArray.length);
            buffer.writeBytes(byteArray);
        } else if (getType() == Type.VAR_INT_BYTE_ARRAY) {

            buffer.writeVarInt(byteArray.length);
            buffer.writeBytes(byteArray);
        }
    }
}
