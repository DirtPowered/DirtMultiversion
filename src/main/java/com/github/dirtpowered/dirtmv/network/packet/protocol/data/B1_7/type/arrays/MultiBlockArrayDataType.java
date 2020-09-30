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
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_7MultiBlockArray;
import io.netty.buffer.ByteBuf;

public class MultiBlockArrayDataType extends DataType<V1_7MultiBlockArray> {

    public MultiBlockArrayDataType() {
        super(Type.V1_7MULTIBLOCK_ARRAY);
    }

    @Override
    public V1_7MultiBlockArray read(ByteBuf buffer) {
        int size = buffer.readShort() & '\uffff';

        byte[] typeArray = new byte[size];
        byte[] metadataArray = new byte[size];
        short[] coordsArray = new short[size];

        for (int i = 0; i < size; ++i)
            coordsArray[i] = buffer.readShort();

        buffer.readBytes(typeArray);
        buffer.readBytes(metadataArray);

        return new V1_7MultiBlockArray(size, coordsArray, typeArray, metadataArray);
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) {
        V1_7MultiBlockArray multiBlockArray = (V1_7MultiBlockArray) typeHolder.getObject();

        int size = multiBlockArray.getSize();

        buffer.writeShort(size);

        for (int i = 0; i < size; ++i)
            buffer.writeShort(multiBlockArray.getCoordsArray()[i]);

        buffer.writeBytes(multiBlockArray.getTypesArray());
        buffer.writeBytes(multiBlockArray.getMetadataArray());
    }
}
