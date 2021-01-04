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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5.chunk;

import com.github.dirtpowered.dirtmv.data.chunk.ChunkUtils;
import com.github.dirtpowered.dirtmv.data.chunk.storage.ExtendedBlockStorage;
import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_2RChunkStorage;

import java.util.ArrayList;
import java.util.List;

public class V1_3ToV1_8ChunkTranslator {
    private final V1_2RChunkStorage chunkStorage;
    private final int bitmapValue;
    private final boolean groundUp;
    private final boolean oldChunk;

    public V1_3ToV1_8ChunkTranslator(byte[] data, int bitmapValue, boolean skyLight, boolean groundUp) {
        V1_2RChunkStorage chunkStorage = new V1_2RChunkStorage(skyLight, false, 0, 0);
        this.bitmapValue = bitmapValue;
        this.groundUp = groundUp;
        this.oldChunk = false;

        chunkStorage.readChunk(groundUp, bitmapValue, data);
        this.chunkStorage = chunkStorage;
    }

    public V1_3ToV1_8ChunkTranslator(V1_2RChunkStorage storage, boolean groundUp, int bitmapValue) {
        this.chunkStorage = storage;
        this.groundUp = groundUp;
        this.bitmapValue = bitmapValue;
        this.oldChunk = true;
    }

    public byte[] getChunkData() {
        ExtendedBlockStorage[] columnStorage = chunkStorage.getColumnStorage();
        List<ExtendedBlockStorage> blockStorages = new ArrayList<>();

        boolean skyLight = chunkStorage.isSkylight();
        byte[] biomes = chunkStorage.getBiomeData();

        int columnBits = 0;

        for (int i = 0; i < columnStorage.length; ++i) {
            ExtendedBlockStorage extendedblockstorage = columnStorage[i];
            boolean f = extendedblockstorage != null && oldChunk != columnStorage[i].isEmpty();

            if (extendedblockstorage != null && (this.bitmapValue & 1 << i) != 0 && (!this.groundUp || f)) {
                columnBits |= 1 << i;

                blockStorages.add(extendedblockstorage);
            }
        }

        byte[] data = new byte[ChunkUtils.calculateDataSize(Integer.bitCount(columnBits), skyLight)];
        int totalSize = 0;

        for (ExtendedBlockStorage blockStorage : blockStorages) {
            byte[] blockArray = blockStorage.getBlockLSBArray();

            for (int j = 0; j < blockArray.length; ++j) {
                int x = j & 15;
                int y = j >> 8 & 15;
                int z = j >> 4 & 15;

                int blockId = blockArray[j] & 255;
                int blockData = blockStorage.getBlockMetadataArray().getNibble(x, y, z);

                blockData = DataFixers.getCorrectedDataFor(blockId, blockData);

                char c = (char) (blockId << 4 | blockData);
                data[totalSize++] = (byte) (c & 255);
                data[totalSize++] = (byte) (c >> 8 & 255);
            }
        }

        for (ExtendedBlockStorage blockStorage : blockStorages) {
            totalSize = writeData(blockStorage.getBlockLightArray().getData(), data, totalSize);
        }

        if (skyLight) {
            for (ExtendedBlockStorage blockStorage : blockStorages) {
                totalSize = writeData(blockStorage.getSkylightArray().getData(), data, totalSize);
            }
        }

        if (this.groundUp) {
            totalSize = writeData(biomes, data, totalSize);
        }

        byte[] chunk = new byte[totalSize];
        System.arraycopy(data, 0, chunk, 0, totalSize);

        return chunk;
    }

    private int writeData(byte[] bytes, byte[] original, int saveOffset) {
        System.arraycopy(bytes, 0, original, saveOffset, bytes.length);

        return saveOffset + bytes.length;
    }
}