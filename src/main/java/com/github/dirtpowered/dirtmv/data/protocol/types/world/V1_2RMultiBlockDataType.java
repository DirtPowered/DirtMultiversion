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

package com.github.dirtpowered.dirtmv.data.protocol.types.world;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2MultiBlockArray;

public class V1_2RMultiBlockDataType extends DataType<V1_2MultiBlockArray> {

    public V1_2RMultiBlockDataType() {
        super(Type.V1_2MULTIBLOCK_ARRAY);
    }

    @Override
    public V1_2MultiBlockArray read(PacketInput packetInput) {
        int recordCount = packetInput.readShort() & 65535;
        int dataSize = packetInput.readInt();
        byte[] data = packetInput.readBytes(dataSize);

        return new V1_2MultiBlockArray(recordCount, dataSize, data);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) {
        V1_2MultiBlockArray multiBlockArray = (V1_2MultiBlockArray) typeHolder.getObject();

        packetOutput.writeShort(multiBlockArray.getRecordCount());
        packetOutput.writeInt(multiBlockArray.getDataSize());
        packetOutput.writeBytes(multiBlockArray.getData());
    }
}
