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

import java.util.ArrayList;
import java.util.List;

public class LoadedChunkTracker {
    private List<Long> loadedChunks = new ArrayList<>();

    public void setChunkLoaded(int chunkX, int chunkZ) {
        loadedChunks.add(getKey(chunkX, chunkZ));
    }

    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return loadedChunks.contains(getKey(chunkX, chunkZ));
    }

    public void removeChunk(int chunkX, int chunkZ) {
        loadedChunks.remove(getKey(chunkX, chunkZ));
    }

    private long getKey(int chunkX, int chunkZ) {
        return (long) chunkX & 0xffffffffL | ((long) chunkZ & 0xffffffffL) << 32;
    }
}
