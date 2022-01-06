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

package com.github.dirtpowered.dirtmv.data.chunk;

import lombok.Getter;

public class NibbleArray {

    @Getter
    private final byte[] data;
    private final boolean oldFormat;
    private int depthBits;

    public NibbleArray(int initialSize) {
        oldFormat = true;

        this.data = new byte[initialSize >> 1];
    }

    public NibbleArray(int initialSize, int depthBits) {
        oldFormat = false;

        this.data = new byte[initialSize >> 1];
        this.depthBits = depthBits;
    }

    public int getNibble(int x, int y, int z) {
        int index = oldFormat ? x << 11 | z << 7 | y : y << depthBits + 4 | z << depthBits | x;
        int value = index >> 1;

        boolean above = (index & 1) == 0;
        return above ? this.data[value] & 15 : this.data[value] >> 4 & 15;
    }

    public void setNibble(int x, int y, int z, int value) {
        int index = oldFormat ? x << 11 | z << 7 | y : y << depthBits + 4 | z << depthBits | x;

        int dividedVal = index >> 1;
        boolean above = (index & 1) == 0;

        if (above) {
            this.data[dividedVal] = (byte) (this.data[dividedVal] & 240 | value & 15);
        } else {
            this.data[dividedVal] = (byte) (this.data[dividedVal] & 15 | (value & 15) << 4);
        }
    }
}
