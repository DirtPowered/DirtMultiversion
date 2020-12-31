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

package com.github.dirtpowered.dirtmv.network.versions.Beta17To14.block;

import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;

public class SolidBlockList {
    private static final Int2BooleanMap BLOCK_LIST = new Int2BooleanOpenHashMap();

    static {
        BLOCK_LIST.put(1, true);
        BLOCK_LIST.put(2, true);
        BLOCK_LIST.put(3, true);
        BLOCK_LIST.put(4, true);
        BLOCK_LIST.put(5, true);
        BLOCK_LIST.put(6, false);
        BLOCK_LIST.put(7, true);
        BLOCK_LIST.put(8, false);
        BLOCK_LIST.put(9, false);
        BLOCK_LIST.put(10, false);
        BLOCK_LIST.put(11, false);
        BLOCK_LIST.put(12, true);
        BLOCK_LIST.put(13, true);
        BLOCK_LIST.put(14, true);
        BLOCK_LIST.put(15, true);
        BLOCK_LIST.put(16, true);
        BLOCK_LIST.put(17, true);
        BLOCK_LIST.put(18, true);
        BLOCK_LIST.put(19, true);
        BLOCK_LIST.put(20, false);
        BLOCK_LIST.put(21, true);
        BLOCK_LIST.put(22, true);
        BLOCK_LIST.put(23, true);
        BLOCK_LIST.put(24, true);
        BLOCK_LIST.put(25, true);
        BLOCK_LIST.put(26, false);
        BLOCK_LIST.put(27, false);
        BLOCK_LIST.put(28, false);
        BLOCK_LIST.put(29, false);
        BLOCK_LIST.put(30, false);
        BLOCK_LIST.put(31, false);
        BLOCK_LIST.put(32, false);
        BLOCK_LIST.put(33, false);
        BLOCK_LIST.put(34, false);
        BLOCK_LIST.put(35, true);
        BLOCK_LIST.put(36, false);
        BLOCK_LIST.put(37, false);
        BLOCK_LIST.put(38, false);
        BLOCK_LIST.put(39, false);
        BLOCK_LIST.put(40, false);
        BLOCK_LIST.put(41, true);
        BLOCK_LIST.put(42, true);
        BLOCK_LIST.put(43, true);
        BLOCK_LIST.put(44, false);
        BLOCK_LIST.put(45, true);
        BLOCK_LIST.put(46, true);
        BLOCK_LIST.put(47, true);
        BLOCK_LIST.put(48, true);
        BLOCK_LIST.put(49, true);
        BLOCK_LIST.put(50, false);
        BLOCK_LIST.put(51, false);
        BLOCK_LIST.put(52, false);
        BLOCK_LIST.put(53, false);
        BLOCK_LIST.put(54, true);
        BLOCK_LIST.put(55, false);
        BLOCK_LIST.put(56, true);
        BLOCK_LIST.put(57, true);
        BLOCK_LIST.put(58, true);
        BLOCK_LIST.put(59, false);
        BLOCK_LIST.put(60, false);
        BLOCK_LIST.put(61, true);
        BLOCK_LIST.put(62, true);
        BLOCK_LIST.put(63, false);
        BLOCK_LIST.put(64, false);
        BLOCK_LIST.put(65, false);
        BLOCK_LIST.put(66, false);
        BLOCK_LIST.put(67, false);
        BLOCK_LIST.put(68, false);
        BLOCK_LIST.put(69, false);
        BLOCK_LIST.put(70, false);
        BLOCK_LIST.put(71, false);
        BLOCK_LIST.put(72, false);
        BLOCK_LIST.put(73, true);
        BLOCK_LIST.put(74, true);
        BLOCK_LIST.put(75, false);
        BLOCK_LIST.put(76, false);
        BLOCK_LIST.put(77, false);
        BLOCK_LIST.put(78, false);
        BLOCK_LIST.put(79, false);
        BLOCK_LIST.put(80, true);
        BLOCK_LIST.put(81, false);
        BLOCK_LIST.put(82, true);
        BLOCK_LIST.put(83, false);
        BLOCK_LIST.put(84, true);
        BLOCK_LIST.put(85, false);
        BLOCK_LIST.put(86, true);
        BLOCK_LIST.put(87, true);
        BLOCK_LIST.put(88, true);
        BLOCK_LIST.put(89, true);
        BLOCK_LIST.put(90, false);
        BLOCK_LIST.put(91, true);
        BLOCK_LIST.put(92, false);
        BLOCK_LIST.put(93, false);
        BLOCK_LIST.put(94, false);
        BLOCK_LIST.put(95, true);
        BLOCK_LIST.put(96, false);
    }

    public static boolean isSolid(int blockId) {
        return BLOCK_LIST.get(blockId);
    }
}
