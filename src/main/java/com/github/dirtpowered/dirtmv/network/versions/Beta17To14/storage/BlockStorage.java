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

package com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;

import java.util.Map;

public class BlockStorage {
    private final Map<Long, ChunkPart> blockStorage = new Long2ObjectOpenHashMap<>();

    @Getter
    private final MinecraftVersion version;

    public BlockStorage(MinecraftVersion version) {
        this.version = version;
    }

    private long getKey(int chunkX, int chunkZ) {
        return (long) chunkX & 0xffffffffL | ((long) chunkZ & 0xffffffffL) << 32;
    }

    public void removeChunk(int chunkX, int chunkZ) {
        blockStorage.remove(getKey(chunkX, chunkZ));
    }

    public int getBlockAt(int x, int y, int z) {
        ChunkPart part = blockStorage.get(getKey(x >> 4, z >> 4));
        if (part == null) {
            return 0;
        }

        return part.getBlock(x & 15, y & 127, z & 15);
    }

    public void setBlockAt(int chunkX, int chunkZ, int x, int y, int z, int blockId) {
        long key = getKey(chunkX, chunkZ);

        if (blockStorage.get(key) == null) {
            blockStorage.put(key, new ChunkPart());
        }

        blockStorage.get(key).setBlock(x & 15, y & 127, z & 15, blockId);
    }

    private static class ChunkPart {
        private static final int SIZE_X = 16;
        private static final int SIZE_Y = 128;
        private static final int SIZE_Z = 16;

        private final byte[] blocks = new byte[SIZE_X * SIZE_Y * SIZE_Z];

        int getBlock(int posX, int posY, int posZ) {
            if (posX >= 0 && posY >= 0 && posZ >= 0 && posX < SIZE_X && posY < SIZE_Y && posZ < SIZE_Z) {
                return this.blocks[(posY * SIZE_Z + posZ) * SIZE_X + posX];
            } else {
                return 0;
            }
        }

        void setBlock(int posX, int posY, int posZ, int block) {
            if (posX >= 0 && posY >= 0 && posZ >= 0 && posX < SIZE_X && posY < SIZE_Y && posZ < SIZE_Z) {
                this.blocks[(posY * SIZE_Z + posZ) * SIZE_X + posX] = (byte) block;
            }
        }
    }
}
