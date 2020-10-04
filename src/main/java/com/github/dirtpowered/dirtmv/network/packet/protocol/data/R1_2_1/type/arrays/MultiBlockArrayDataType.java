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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_2_1.type.arrays;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_2MultiBlockArray;
import io.netty.buffer.ByteBuf;

public class MultiBlockArrayDataType extends DataType<V1_2MultiBlockArray> {

    public MultiBlockArrayDataType() {
        super(Type.V1_2MULTIBLOCK_ARRAY);
    }

    @Override
    public V1_2MultiBlockArray read(ByteBuf buffer) {
        int recordCount = buffer.readShort() & 0xffff;
        int dataSize = buffer.readInt();
        byte[] data = new byte[dataSize];

        buffer.readBytes(data);
        return new V1_2MultiBlockArray(recordCount, dataSize, data);
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) {
        V1_2MultiBlockArray multiBlockArray = (V1_2MultiBlockArray) typeHolder.getObject();

        buffer.writeShort(multiBlockArray.getRecordCount());
        buffer.writeInt(multiBlockArray.getDataSize());
        buffer.writeBytes(multiBlockArray.getData());
    }
}
