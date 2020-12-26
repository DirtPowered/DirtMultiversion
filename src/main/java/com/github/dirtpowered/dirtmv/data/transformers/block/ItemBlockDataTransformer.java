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

package com.github.dirtpowered.dirtmv.data.transformers.block;

import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

public abstract class ItemBlockDataTransformer {

    @Getter
    private final Int2ObjectMap<Block> blockReplacementsMap = new Int2ObjectOpenHashMap<>();

    @Getter
    private final Int2ObjectMap<Block> itemReplacementsMap = new Int2ObjectOpenHashMap<>();

    protected ItemBlockDataTransformer() {
        registerReplacements();
    }

    public abstract void registerReplacements();

    public Block replaceBlock(int blockId, int blockData) {
        int key = (blockId * 16) + blockData;
        return getBlockReplacementsMap().getOrDefault(key, new Block(blockId, blockData));
    }

    public ItemStack replaceItem(ItemStack itemStack) {
        int key = (itemStack.getItemId() * 16) + itemStack.getData();
        Block b = getItemReplacementsMap().getOrDefault(key, new Block(itemStack.getItemId(), itemStack.getData()));

        return new ItemStack(b.getBlockId(), itemStack.getAmount(), b.getBlockData(), b.getNameTag(itemStack.getCompoundTag()));
    }

    protected void addBlockReplacement(int id, int data, Block to) {
        blockReplacementsMap.put((id * 16) + data, to);
    }

    protected void addItemReplacement(int id, int data, Block to) {
        itemReplacementsMap.put((id * 16) + data, to);
    }
}
