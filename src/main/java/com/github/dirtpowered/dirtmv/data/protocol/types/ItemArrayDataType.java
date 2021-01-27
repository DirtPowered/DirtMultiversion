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

package com.github.dirtpowered.dirtmv.data.protocol.types;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.TypeObject;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import lombok.Getter;

import java.io.IOException;

public class ItemArrayDataType extends DataType<ItemStack[]> {

    @Getter
    private final DataType childInstruction;

    public ItemArrayDataType(TypeObject type, DataType child) {
        super(type);

        this.childInstruction = child;
    }

    @Override
    public ItemStack[] read(PacketInput packetInput) throws IOException {
        short length = packetInput.readShort();

        ItemStack[] objArray = new ItemStack[length];

        for (short i = 0; i < length; i++) {
            objArray[i] = (ItemStack) childInstruction.read(packetInput);
        }
        return objArray;
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) throws IOException {
        ItemStack[] objArray = (ItemStack[]) typeHolder.getObject();

        packetOutput.writeShort(objArray.length);

        for (ItemStack item : objArray) {
            childInstruction.write(new TypeHolder(childInstruction.getType(), item), packetOutput);
        }
    }
}
