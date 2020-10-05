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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_2_1.chunk;

import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.chunk.model.Chunk;

public class V1_2ChunkStorage implements Chunk {

    @Override
    public int getBlockId(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockId(int x, int y, int z, int value) {

    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockMetadata(int x, int y, int z, int value) {

    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int value) {

    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setSkyLight(int x, int y, int z, int value) {

    }
}
