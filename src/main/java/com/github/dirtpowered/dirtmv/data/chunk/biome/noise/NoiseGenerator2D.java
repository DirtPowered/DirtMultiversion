package com.github.dirtpowered.dirtmv.data.chunk.biome.noise;

import java.util.Random;

public class NoiseGenerator2D {
    private static final double f = 0.5D * (Math.sqrt(3.0D) - 1.0D);
    private static final double g = (3.0D - Math.sqrt(3.0D)) / 6.0D;
    private static int[][] d = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    public double a;
    public double b;
    public double c;
    private int[] premutationTable;

    NoiseGenerator2D(Random seededRandom) {
        this.premutationTable = new int[512];
        this.a = seededRandom.nextDouble() * 256.0D;
        this.b = seededRandom.nextDouble() * 256.0D;
        this.c = seededRandom.nextDouble() * 256.0D;

        int i = 0;
        while (i < 256) {
            this.premutationTable[i] = i++;
        }

        for (i = 0; i < 256; ++i) {
            int var3 = seededRandom.nextInt(256 - i) + i;
            int var4 = this.premutationTable[i];
            this.premutationTable[i] = this.premutationTable[var3];
            this.premutationTable[var3] = var4;
            this.premutationTable[i + 256] = this.premutationTable[i];
        }

    }

    private static int a(double var0) {
        return var0 > 0.0D ? (int) var0 : (int) var0 - 1;
    }

    private static double a(int[] var0, double var1, double var3) {
        return (double) var0[0] * var1 + (double) var0[1] * var3;
    }

    void generateNoise(double[] array, double x, double z, int xSize, int zSize, double gridX, double gridZ, double noiseFrequency) {
        int index = 0;

        for (int x1 = 0; x1 < xSize; ++x1) {
            double var16 = (x + (double) x1) * gridX + this.a;

            for (int z1 = 0; z1 < zSize; ++z1) {
                double var19 = (z + (double) z1) * gridZ + this.b;
                double var21 = (var16 + var19) * f;
                int var23 = a(var16 + var21);
                int var24 = a(var19 + var21);
                double var25 = (double) (var23 + var24) * g;
                double var27 = (double) var23 - var25;
                double var29 = (double) var24 - var25;
                double var31 = var16 - var27;
                double var33 = var19 - var29;
                byte var35;
                byte var36;
                if (var31 > var33) {
                    var35 = 1;
                    var36 = 0;
                } else {
                    var35 = 0;
                    var36 = 1;
                }

                double var37 = var31 - (double) var35 + g;
                double var39 = var33 - (double) var36 + g;
                double var41 = var31 - 1.0D + 2.0D * g;
                double var43 = var33 - 1.0D + 2.0D * g;
                int var45 = var23 & 255;
                int var46 = var24 & 255;
                int var47 = this.premutationTable[var45 + this.premutationTable[var46]] % 12;
                int var48 = this.premutationTable[var45 + var35 + this.premutationTable[var46 + var36]] % 12;
                int var49 = this.premutationTable[var45 + 1 + this.premutationTable[var46 + 1]] % 12;
                double var50 = 0.5D - var31 * var31 - var33 * var33;
                double var52;
                if (var50 < 0.0D) {
                    var52 = 0.0D;
                } else {
                    var50 *= var50;
                    var52 = var50 * var50 * a(d[var47], var31, var33);
                }

                double var54 = 0.5D - var37 * var37 - var39 * var39;
                double var56;
                if (var54 < 0.0D) {
                    var56 = 0.0D;
                } else {
                    var54 *= var54;
                    var56 = var54 * var54 * a(d[var48], var37, var39);
                }

                double var58 = 0.5D - var41 * var41 - var43 * var43;
                double var60;
                if (var58 < 0.0D) {
                    var60 = 0.0D;
                } else {
                    var58 *= var58;
                    var60 = var58 * var58 * a(d[var49], var41, var43);
                }

                array[index] += 70.0D * (var52 + var56 + var60) * noiseFrequency;

                index++;
            }
        }
    }
}
