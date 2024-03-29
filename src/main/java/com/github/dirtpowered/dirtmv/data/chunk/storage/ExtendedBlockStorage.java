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

package com.github.dirtpowered.dirtmv.data.chunk.storage;

import com.github.dirtpowered.dirtmv.data.chunk.NibbleArray;
import lombok.Getter;

public class ExtendedBlockStorage {

    @Getter
    private final byte[] blockLSBArray;

    @Getter
    private NibbleArray blockMSBArray;

    @Getter
    private final NibbleArray blockMetadataArray;

    @Getter
    private final NibbleArray blockLightArray;

    @Getter
    private NibbleArray skylightArray;

    private final boolean old;

    public ExtendedBlockStorage(boolean skylight, boolean old) {
        this.old = old;
        this.blockLSBArray = new byte[4096];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.blockLightArray = new NibbleArray(this.blockLSBArray.length, 4);

        if (skylight) {
            this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
        }
    }

    int getTypeAt(int x, int y, int z) {
        return this.blockLSBArray[y << 8 | z << 4 | x] & 255;
    }

    void setTypeAt(int x, int y, int z, int value) {
        this.blockLSBArray[y << 8 | z << 4 | x] = (byte) (value & 255);
    }

    int getBlockMetadata(int x, int y, int z) {
        return this.blockMetadataArray.getNibble(x, y, z);
    }

    void setBlockMetadata(int x, int y, int z, int value) {
        this.blockMetadataArray.setNibble(x, y, z, value);
    }

    void setSkylightValue(int x, int y, int z, int value) {
        this.skylightArray.setNibble(x, y, z, value);
    }

    int getSkylightValue(int x, int y, int z) {
        return this.skylightArray.getNibble(x, y, z);
    }

    void setBlockLightValue(int x, int y, int z, int value) {
        this.blockLightArray.setNibble(x, y, z, value);
    }

    int getBlockLightValue(int x, int y, int z) {
        return this.blockLightArray.getNibble(x, y, z);
    }

    public boolean isEmpty() {
        return !old && this.blockMSBArray == null;
    }

    public void clearMSBArray() {
        this.blockMSBArray = null;
    }

    public NibbleArray createBlockMSBArray() {
        this.blockMSBArray = new NibbleArray(this.blockLSBArray.length);
        return this.blockMSBArray;
    }
}
