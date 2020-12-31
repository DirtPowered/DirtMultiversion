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

package com.github.dirtpowered.dirtmv.network.versions.Release4To78.item;

import com.github.dirtpowered.dirtmv.data.transformers.block.Block;
import com.github.dirtpowered.dirtmv.data.transformers.block.ItemBlockDataTransformer;

public class ItemRemapper extends ItemBlockDataTransformer {

    @Override
    public void registerReplacements() {
        addItemReplacement(26, 0, new Block(355, 0, "Bed block"));
        addItemReplacement(34, 0, new Block(33, 0, "Piston extension"));
        addItemReplacement(36, 0, new Block(33, 0, "Piston moving piece"));
        addItemReplacement(55, 0, new Block(331, 0, "Redstone wire"));
        addItemReplacement(59, 0, new Block(295, 0, "Wheat crops"));
        addItemReplacement(63, 0, new Block(323, 0, "Standing sign"));
        addItemReplacement(64, 0, new Block(324, 0, "Oak door block"));
        addItemReplacement(68, 0, new Block(323, 0, "Wall sign"));
        addItemReplacement(71, 0, new Block(330, 0, "Iron door block"));
        addItemReplacement(74, 0, new Block(73, 0, "Glowing redstone ore"));
        addItemReplacement(75, 0, new Block(76, 0, "Restone torch off"));
        addItemReplacement(83, 0, new Block(338, 0, "Sugar cane block"));
        addItemReplacement(92, 0, new Block(354, 0, "Cake block"));
        addItemReplacement(93, 0, new Block(356, 0, "Diode block off"));
        addItemReplacement(94, 0, new Block(356, 0, "Diode block on"));
        addItemReplacement(95, 0, new Block(146, 0, "Locked Chest"));
        addItemReplacement(104, 0, new Block(361, 0, "Pumpkin stem"));
        addItemReplacement(105, 0, new Block(362, 0, "Melon stem"));
        addItemReplacement(115, 0, new Block(372, 0, "Nether warts"));
        addItemReplacement(117, 0, new Block(379, 0, "Brewing stand"));
        addItemReplacement(118, 0, new Block(380, 0, "Cauldron"));
        addItemReplacement(124, 0, new Block(123, 0, "Restone lamp on"));
        addItemReplacement(132, 0, new Block(287, 0, "Tripwire"));
        addItemReplacement(140, 0, new Block(390, 0, "Flower pot"));
        addItemReplacement(144, 0, new Block(397, 0, "Skull"));
        addItemReplacement(149, 0, new Block(404, 0, "Redstone comparator off"));
        addItemReplacement(150, 0, new Block(404, 0, "Redstone comparator on"));
    }
}
