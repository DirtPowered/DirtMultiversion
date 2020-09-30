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
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.V1_7BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.ItemStack;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class ItemArrayDataType extends DataType<ItemStack[]> {

    public ItemArrayDataType() {
        super(Type.V1_7B_ITEM_ARRAY);
    }

    @Override
    public ItemStack[] read(ByteBuf buffer) throws IOException {
        short length = buffer.readShort();

        ItemStack[] objArray = new ItemStack[length];

        for (short i = 0; i < length; i++) {
            objArray[i] = (ItemStack) V1_7BProtocol.ITEM.read(buffer);
        }
        return objArray;
    }


    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) throws IOException {
        ItemStack[] objArray = (ItemStack[]) typeHolder.getObject();

        buffer.writeShort(objArray.length);

        for (ItemStack item : objArray) {
            V1_7BProtocol.ITEM.write(new TypeHolder(Type.V1_7B_ITEM, item), buffer);
        }
    }
}
