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

package com.github.dirtpowered.dirtmv.network.versions.Release28To23.chunk;

import com.github.dirtpowered.dirtmv.data.chunk.ChunkUtils;
import com.github.dirtpowered.dirtmv.data.chunk.cache.WorldTrackerImpl;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import lombok.Getter;

@Getter
public class LegacyChunkTracker implements WorldTrackerImpl {
    private LongObjectMap<LegacyChunkPair> chunkMap;

    void initialize() {
        chunkMap = new LongObjectHashMap<>();
    }

    @Override
    public void onBlockUpdate(int x, int y, int z, int typeId, int data) {
        if (x < 0 || z < 0) {
            // TODO: Fix block updates on negative coordinates
            return;
        }

        int chunkX = x / 16;
        int chunkZ = z / 16;

        long key = ChunkUtils.getChunkLongKey(chunkX, chunkZ);

        int newX = x & 0xF;
        int newY = y & 0x7F;
        int newZ = z & 0xF;

        if (chunkMap.containsKey(key)) {
            LegacyChunkPair chunkPair = chunkMap.get(key);

            chunkPair.getNewChunk().setBlockId(newX, newY, newZ, typeId);
            chunkPair.getNewChunk().setBlockMetadata(newX, newY, newZ, data);
        }
    }

    @Override
    public void onChunkUnload(int chunkX, int chunkZ) {
        chunkMap.remove(ChunkUtils.getChunkLongKey(chunkX, chunkZ));
    }

    @Override
    public void purge() {
        if (chunkMap != null) {
            chunkMap.clear();
        }
    }
}
