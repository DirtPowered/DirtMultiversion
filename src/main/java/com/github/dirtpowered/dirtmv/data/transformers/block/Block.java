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

package com.github.dirtpowered.dirtmv.data.transformers.block;

import lombok.Data;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.Map;

@Data
public class Block {
    private int blockId;
    private int blockData;
    private String oldItemName;

    public Block(int blockId, int blockData) {
        this.blockId = blockId;
        this.blockData = blockData;
    }

    public Block(int blockId, int blockData, String oldItemName) {
        this.blockId = blockId;
        this.blockData = blockData;
        this.oldItemName = oldItemName;
    }

    CompoundBinaryTag getNameTag(CompoundBinaryTag originalTag) {
        if (oldItemName == null || oldItemName.isEmpty()) {
            return originalTag;
        }

        CompoundBinaryTag tag = originalTag;
        if (tag == null) {
            tag = CompoundBinaryTag.empty();
        }

        CompoundBinaryTag.Builder parentBuilder = CompoundBinaryTag.builder();
        parentBuilder.putString("Name", "§r" + oldItemName);
        CompoundBinaryTag parentTag = parentBuilder.build();

        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
        builder.put("display", parentTag);

        for (Map.Entry<String, ? extends BinaryTag> stringEntry : tag) {
            builder.put(stringEntry.getKey(), stringEntry.getValue());
        }

        return builder.build();
    }
}
