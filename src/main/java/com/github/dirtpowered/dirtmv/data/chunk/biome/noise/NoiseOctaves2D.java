package com.github.dirtpowered.dirtmv.data.chunk.biome.noise;

import java.util.Arrays;
import java.util.Random;

public class NoiseOctaves2D {
    private final NoiseGenerator2D[] generators;
    private final int size;

    public NoiseOctaves2D(Random seededRandom, int size) {
        this.size = size;
        this.generators = new NoiseGenerator2D[size];

        for (int i = 0; i < size; ++i) {
            this.generators[i] = new NoiseGenerator2D(seededRandom);
        }
    }

    public double[] generateNoise(double[] array, double x, double z, int xSize, int zSize, double gridX, double gridZ, double noiseFrequency) {
        gridX /= 1.5D;
        gridZ /= 1.5D;

        if (array != null && array.length >= xSize * zSize) {
            Arrays.fill(array, 0.0D);
        } else {
            array = new double[xSize * zSize];
        }

        double amplitude = 1.0D;
        double frequency = 1.0D;

        for (int j = 0; j < this.size; ++j) {
            this.generators[j].generateNoise(array, x, z, xSize, zSize, gridX * frequency, gridZ * frequency, 0.55D / amplitude);
            frequency *= noiseFrequency;
            amplitude *= 0.5D;
        }

        return array;
    }
}