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

package com.github.dirtpowered.dirtmv.data.chunk.biome;

import lombok.Getter;

enum BiomeType {
    RAINFOREST(21 /* jungle */),
    SWAMPLAND(1 /* plains */),
    SEASONAL_FOREST(4 /* forest */),
    FOREST(4 /* forest */),
    SAVANNA(35 /* savanna */),
    SHRUBLAND(151 /* mutated jungle edge */),
    TAIGA(5 /* taiga */),
    DESERT(2 /* desert */),
    PLAINS(1 /* plains */),
    TUNDRA(5 /* taiga */),
    NETHER(8 /* nether */);

    @Getter
    private final int biomeId;

    BiomeType(int biomeId) {
        this.biomeId = biomeId;
    }
}