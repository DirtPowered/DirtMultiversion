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
import lombok.Setter;

import java.util.zip.Deflater;

public class V1_2RChunkStorage implements Chunk {

    @Getter
    private int primaryBitmap;

    @Getter
    private int compressedSize;

    @Getter
    private int chunkX;

    @Getter
    private int chunkZ;

    @Getter
    @Setter
    private byte[] biomeData;

    private ExtendedBlockStorage[] columnStorage;

    public V1_2RChunkStorage(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        this.columnStorage = new ExtendedBlockStorage[16];

        for (int i = 0; i < 16; i++) {
            this.columnStorage[i] = new ExtendedBlockStorage();
        }

        this.biomeData = new byte[256];
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return columnStorage[y >> 4].getTypeAt(x, y & 15, z);
    }

    @Override
    public void setBlockId(int x, int y, int z, int value) {
        columnStorage[y >> 4].setTypeAt(x, y & 15, z, value);
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return columnStorage[y >> 4].getBlockMetadata(x, y & 15, z);
    }

    @Override
    public void setBlockMetadata(int x, int y, int z, int value) {
        columnStorage[y >> 4].setBlockMetadata(x, y & 15, z, value);
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return columnStorage[y >> 4].getBlockLightValue(x, y, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int value) {
        columnStorage[y >> 4].setBlockLightValue(x, y & 15, z, value);
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return columnStorage[y >> 4].getSkylightValue(x, y, z);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int value) {
        columnStorage[y >> 4].setSkylightValue(x, y & 15, z, value);
    }

    public byte[] getCompressedData(boolean groundUp, int bitmapValue) {
        int totalSize = 0;
        int primaryBitmap = bitmapValue;

        byte[] data = new byte[groundUp ? 164096 /* with biome data*/ : 163840];

        if (groundUp)
            primaryBitmap = 65535;

        for (int i = 0; i < 16; i++) {
            if (columnStorage[i] == null || groundUp && columnStorage[i].isEmpty() || (primaryBitmap & 1 << i) == 0) {
                continue;
            }

            this.primaryBitmap |= 1 << i;
        }

        for (int i = 0; i < 16; i++) {
            if (columnStorage[i] != null && (!groundUp || !columnStorage[i].isEmpty()) && (primaryBitmap & 1 << i) != 0) {
                byte[] blocks = columnStorage[i].getBlockLSBArray();

                System.arraycopy(blocks, 0, data, totalSize, blocks.length);

                totalSize += blocks.length;
            }
        }

        for (int i = 0; i < 16; i++) {
            if (columnStorage[i] != null && (!groundUp || !columnStorage[i].isEmpty()) && (primaryBitmap & 1 << i) != 0) {
                NibbleArray blocksData = columnStorage[i].getBlockMetadataArray();

                System.arraycopy(blocksData.getData(), 0, data, totalSize, blocksData.getData().length);

                totalSize += blocksData.getData().length;
            }
        }

        for (int i = 0; i < 16; i++) {
            if (columnStorage[i] != null && (!groundUp || !columnStorage[i].isEmpty()) && (primaryBitmap & 1 << i) != 0) {
                NibbleArray blocksLight = columnStorage[i].getBlockLightArray();

                System.arraycopy(blocksLight.getData(), 0, data, totalSize, blocksLight.getData().length);

                totalSize += blocksLight.getData().length;
            }
        }

        for (int i = 0; i < 16; i++) {
            if (columnStorage[i] != null && (!groundUp || !columnStorage[i].isEmpty()) && (primaryBitmap & 1 << i) != 0) {
                NibbleArray skyLight = columnStorage[i].getSkylightArray();

                System.arraycopy(skyLight.getData(), 0, data, totalSize, skyLight.getData().length);

                totalSize += skyLight.getData().length;
            }
        }

        if (groundUp) {
            System.arraycopy(biomeData, 0, data, totalSize, biomeData.length);
            totalSize += 256;
        }

        Deflater deflater = new Deflater(-1);

        byte[] compressedChunk;

        try {
            deflater.setInput(data, 0, totalSize);
            deflater.finish();

            compressedChunk = new byte[totalSize];
            compressedSize = deflater.deflate(compressedChunk);
        } finally {
            deflater.end();
        }

        return compressedChunk;
    }
}
