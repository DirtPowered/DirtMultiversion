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

package com.github.dirtpowered.dirtmv.data.protocol.types.item;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;

public class V1_8BItemDataType extends DataType<ItemStack> {

    public V1_8BItemDataType() {
        super(Type.V1_8B_ITEM);
    }

    @Override
    public ItemStack read(PacketInput packetInput) {
        int itemId = packetInput.readShort();

        if (itemId >= 0) {
            int amount = packetInput.readShort();
            int data = packetInput.readShort();

            return new ItemStack(itemId, amount, data, null);
        }

        return null;
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) {
        ItemStack itemStack = (ItemStack) typeHolder.getObject();

        if (itemStack == null) {
            packetOutput.writeShort(-1);
        } else {
            packetOutput.writeShort(itemStack.getItemId());
            packetOutput.writeShort(itemStack.getAmount());
            packetOutput.writeShort(itemStack.getData());
        }
    }
}
