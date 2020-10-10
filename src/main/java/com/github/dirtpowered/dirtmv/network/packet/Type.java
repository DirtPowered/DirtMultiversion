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

package com.github.dirtpowered.dirtmv.network.packet;

public enum Type {
    BYTE,
    DOUBLE,
    FLOAT,
    INT,
    LONG,
    SHORT,
    STRING,
    BYTE_BYTE_ARRAY,
    SHORT_BYTE_ARRAY,
    UTF8_STRING,
    V1_7B_CHUNK,
    V1_7B_ITEM,
    V1_7B_ITEM_ARRAY,
    V1_7B_METADATA,
    V1_8B_ITEM,
    POSITION_ARRAY,
    MOTION,
    V1_7MULTIBLOCK_ARRAY,
    V1_0R_ITEM,
    V1_0R_ITEM_ARRAY,
    V1_2MULTIBLOCK_ARRAY,
    V1_2_CHUNK,
    V1_3R_ITEM,
    V1_3R_ITEM_ARRAY,
    V1_3_CHUNK,
    BYTE_INT_ARRAY
}