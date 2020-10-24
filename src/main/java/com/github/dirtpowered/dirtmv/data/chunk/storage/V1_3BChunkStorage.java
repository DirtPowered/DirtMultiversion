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

package com.github.dirtpowered.dirtmv.data.chunk.storage;

import com.github.dirtpowered.dirtmv.data.chunk.Chunk;
import com.github.dirtpowered.dirtmv.data.chunk.NibbleArray;
import lombok.Getter;

public class V1_3BChunkStorage implements Chunk {

    private final static byte[] EMPTY_CHUNK = new byte[16 * 16 * 128];

    private byte[] blockArray;
    private NibbleArray blockDataArray;
    private NibbleArray blockLightArray;
    private NibbleArray skyLightArray;

    @Getter
    private int chunkX;

    @Getter
    private int chunkZ;

    public V1_3BChunkStorage(int chunkX, int chunkZ) {
        this.blockArray = EMPTY_CHUNK;
        this.blockDataArray = new NibbleArray(EMPTY_CHUNK.length);
        this.blockLightArray = new NibbleArray(EMPTY_CHUNK.length);
        this.skyLightArray = new NibbleArray(EMPTY_CHUNK.length);

        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return this.blockArray[x << 11 | z << 7 | y] & 255;
    }

    @Override
    public void setBlockId(int x, int y, int z, int value) {
        this.blockArray[x << 11 | z << 7 | y] = (byte) (value & 255);
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return this.blockDataArray.getNibble(x, y, z);
    }

    @Override
    public void setBlockMetadata(int x, int y, int z, int value) {
        this.blockDataArray.setNibble(x, y, z, value);
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.blockLightArray.getNibble(x, y, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int value) {
        this.blockLightArray.setNibble(x, y, z, value);
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return this.skyLightArray.getNibble(x, y, z);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int value) {
        this.skyLightArray.setNibble(x, y, z, value);
    }

    public int setChunkData(byte[] data, int x, int y, int z, int xSize, int ySize, int zSize, int totalSize) {
        for (int i = x; i < xSize; ++i) {
            for (int j = z; j < zSize; ++j) {
                int index = i << 11 | j << 7 | y;
                int size = ySize - y;
                System.arraycopy(data, totalSize, this.blockArray, index, size);
                totalSize += size;
            }
        }

        for (int i = x; i < xSize; ++i) {
            for (int j = z; j < zSize; ++j) {
                int index = (i << 11 | j << 7 | y) >> 1;
                int size = (ySize - y) / 2;
                System.arraycopy(data, totalSize, this.blockDataArray.getData(), index, size);
                totalSize += size;
            }
        }

        for (int i = x; i < xSize; ++i) {
            for (int j = z; j < zSize; ++j) {
                int index = (i << 11 | j << 7 | y) >> 1;
                int size = (ySize - y) / 2;
                System.arraycopy(data, totalSize, this.blockLightArray.getData(), index, size);
                totalSize += size;
            }
        }

        for (int i = x; i < xSize; ++i) {
            for (int j = z; j < zSize; ++j) {
                int index = (i << 11 | j << 7 | y) >> 1;
                int size = (ySize - y) / 2;
                System.arraycopy(data, totalSize, this.skyLightArray.getData(), index, size);
                totalSize += size;
            }
        }

        return totalSize;
    }
}
