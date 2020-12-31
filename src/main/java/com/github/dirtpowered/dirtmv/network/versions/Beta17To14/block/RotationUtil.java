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

package com.github.dirtpowered.dirtmv.network.versions.Beta17To14.block;

import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage.BlockStorage;

public class RotationUtil {
    private final static int CHEST_BLOCK = 54;

    public static byte fixBlockRotation(ServerSession session, int startX, int startY, int startZ) {
        BlockStorage blockStorage = session.getUserData().getProtocolStorage().get(BlockStorage.class);
        byte blockData = 3;

        if (blockStorage == null) {
            return blockData;
        }

        int rot1 = blockStorage.getBlockAt(startX, startY, startZ - 1);
        int rot2 = blockStorage.getBlockAt(startX, startY, startZ + 1);
        int rot3 = blockStorage.getBlockAt(startX - 1, startY, startZ);
        int rot4 = blockStorage.getBlockAt(startX + 1, startY, startZ);

        int rot5;
        if (rot3 == CHEST_BLOCK) {
            rot5 = blockStorage.getBlockAt(startX - 1, startY, startZ - 1);
        } else {
            rot5 = blockStorage.getBlockAt(startX + 1, startY, startZ - 1);
        }

        int rot6;
        if (rot3 == CHEST_BLOCK) {
            rot6 = blockStorage.getBlockAt(startX - 1, startY, startZ + 1);
        } else {
            rot6 = blockStorage.getBlockAt(startX + 1, startY, startZ + 1);
        }

        if (rot1 != CHEST_BLOCK && rot2 != CHEST_BLOCK) {
            if (rot3 != CHEST_BLOCK && rot4 != CHEST_BLOCK) {
                if (SolidBlockList.isSolid(rot2) && !SolidBlockList.isSolid(rot1)) {
                    blockData = 2;
                }

                if (SolidBlockList.isSolid(rot3) && !SolidBlockList.isSolid(rot4)) {
                    blockData = 5;
                }

                if (SolidBlockList.isSolid(rot4) && !SolidBlockList.isSolid(rot3)) {
                    blockData = 4;
                }
            } else {
                if (SolidBlockList.isSolid(rot2) || SolidBlockList.isSolid(rot6)) {
                    if (!SolidBlockList.isSolid(rot1) && !SolidBlockList.isSolid(rot5)) {
                        blockData = 2;
                    }
                }
            }
        } else {
            if (rot1 == CHEST_BLOCK) {
                rot5 = blockStorage.getBlockAt(startX - 1, startY, startZ - 1);
            } else {
                rot5 = blockStorage.getBlockAt(startX - 1, startY, startZ + 1);
            }

            if (rot1 == CHEST_BLOCK) {
                rot6 = blockStorage.getBlockAt(startX + 1, startY, startZ - 1);
            } else {
                rot6 = blockStorage.getBlockAt(startX + 1, startY, startZ + 1);
            }

            if (SolidBlockList.isSolid(rot3) || SolidBlockList.isSolid(rot5)) {
                if (!SolidBlockList.isSolid(rot4) && !SolidBlockList.isSolid(rot6)) {
                    blockData = 5;
                }
            }

            if (SolidBlockList.isSolid(rot4) || SolidBlockList.isSolid(rot6)) {
                if (!SolidBlockList.isSolid(rot3) && !SolidBlockList.isSolid(rot5)) {
                    blockData = 4;
                }
            }
        }

        return blockData;
    }
}
