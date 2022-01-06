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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5.item;

import com.github.dirtpowered.dirtmv.data.transformers.block.Block;
import com.github.dirtpowered.dirtmv.data.transformers.block.ItemBlockDataTransformer;

public class ItemRemapper extends ItemBlockDataTransformer {

    @Override
    public void registerReplacements() {
        addItemReplacement(8, 0, new Block(326, 0, "Water"));
        addItemReplacement(9, 0, new Block(326, 0, "Stationary Water"));
        addItemReplacement(10, 0, new Block(327, 0, "Lava"));
        addItemReplacement(11, 0, new Block(327, 0, "Stationary lava"));
        addItemReplacement(43, 0, new Block(44, 0, "Double step"));
        addItemReplacement(44, 2, new Block(126, 0, "Wooden slab"));
        addItemReplacement(51, 0, new Block(385, 0, "Fire"));
        addItemReplacement(90, 0, new Block(160, 10, "Nether portal"));
        addItemReplacement(119, 0, new Block(399, 0, "End portal"));
        addItemReplacement(125, 0, new Block(126, 0, "Wood double step"));
        addItemReplacement(127, 0, new Block(351, 3, "Cocoa"));
        addItemReplacement(141, 0, new Block(391, 0, "Carrots"));
        addItemReplacement(142, 0, new Block(392, 0, "Potato"));
    }
}
