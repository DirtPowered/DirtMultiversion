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

package com.github.dirtpowered.dirtmv.data.chunk.biome;

import com.github.dirtpowered.dirtmv.data.chunk.biome.noise.NoiseOctaves2D;

import java.util.Arrays;
import java.util.Random;

public class OldChunkData {
    private final static int BIOME_ARRAY_LENGTH = 256;
    private final static double TEMP_NOISE_FREQ = 0.25D;
    private final static double HUMID_NOISE_FREQ = 0.3333333333333333D;
    private final static double TEMP_GRID = 0.02500000037252903D;
    private final static double HUMID_GRID = 0.05000000074505806D;
    private double[] temperature;
    private double[] humidity;
    private NoiseOctaves2D noise1;
    private NoiseOctaves2D noise2;

    public void initialize(long worldSeed) {
        noise1 = new NoiseOctaves2D(new Random(worldSeed * 9871L), 4);
        noise2 = new NoiseOctaves2D(new Random(worldSeed * 39811L), 4);
    }

    private BiomeType getBiomeType(double temperature, double humidity) {
        humidity = humidity * temperature;

        if (temperature < 0.1F) {
            return BiomeType.TUNDRA;
        } else if (humidity < 0.2F) {
            return temperature < 0.5F ? BiomeType.TUNDRA : temperature < 0.95F ? BiomeType.SAVANNA : BiomeType.DESERT;
        } else if (humidity > 0.5F && temperature < 0.7F) {
            return BiomeType.SWAMPLAND;
        } else if (temperature < 0.5F) {
            return BiomeType.TAIGA;
        } else if (temperature < 0.97F) {
            return humidity < 0.35F ? BiomeType.SHRUBLAND : BiomeType.FOREST;
        } else if (humidity < 0.45F) {
            return BiomeType.PLAINS;
        } else {
            return humidity < 0.9F ? BiomeType.SEASONAL_FOREST : BiomeType.RAINFOREST;
        }
    }

    public byte[] getBiomeDataAt(int chunkX, int chunkZ, boolean customDimension) {
        byte[] oldData = new byte[256];

        if (!customDimension) {
            oldData = getBiomeData(chunkX * 16, chunkZ * 16);
        } else {
            // nether
            Arrays.fill(oldData, (byte) BiomeType.NETHER.getBiomeId());
        }

        byte[] newData = new byte[256];
        int i = 0;

        // The array is indexed by z * 16 | x. ~wiki.vg
        for (int x1 = 0; x1 < 16; x1++) {
            for (int z1 = 0; z1 < 16; z1++) {
                newData[z1 * 16 | x1] = oldData[i++];
            }
        }
        return newData;
    }

    private byte[] getBiomeData(int x, int z) {
        byte[] biomeArray = new byte[256];

        temperature = noise1.generateNoise(temperature, x, z, 16, 16, TEMP_GRID, TEMP_GRID, TEMP_NOISE_FREQ);
        humidity = noise2.generateNoise(humidity, x, z, 16, 16, HUMID_GRID, HUMID_GRID, HUMID_NOISE_FREQ);

        for (int i = 0; i < BIOME_ARRAY_LENGTH; ++i) {
            double rawBiomeTemperature = (temperature[i] * 0.15D + 0.7D) * 0.99D + 0.0105;
            double biomeTemperature = 1.0D - (1.0D - rawBiomeTemperature) * (1.0D - rawBiomeTemperature);

            double biomeHumidity = (humidity[i] * 0.15D + 0.5D) * 0.99D + 0.0021;

            biomeTemperature = Math.min(Math.max(biomeTemperature, 0.0D), 1.0D);
            biomeHumidity = Math.min(Math.max(biomeHumidity, 0.0D), 1.0D);

            BiomeType biome = getBiomeType(biomeTemperature, biomeHumidity);
            biomeArray[i] = (byte) biome.getBiomeId();
        }

        return biomeArray;
    }
}
