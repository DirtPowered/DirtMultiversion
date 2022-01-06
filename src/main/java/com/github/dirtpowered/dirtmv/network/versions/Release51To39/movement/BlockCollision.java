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

package com.github.dirtpowered.dirtmv.network.versions.Release51To39.movement;

import lombok.Getter;

public class BlockCollision {
    private final static double COLLISION_TOLERANCE = 0.6D;

    @Getter
    protected BoundingBox boundingBox;

    protected int x;
    protected int y;
    protected int z;

    void setPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    void correctPosition(BoundingBox playerCollision) {
        double playerMinY = playerCollision.getMiddleY() - (playerCollision.getSizeY() / 2);
        double boxMinY = (boundingBox.getMiddleY() + y) - (boundingBox.getSizeY() / 2);
        double boxMaxY = (boundingBox.getMiddleY() + y) + (boundingBox.getSizeY() / 2);

        if (boundingBox.checkIntersection(x, y, z, playerCollision) && (playerMinY + 1) >= boxMinY) {
            if (boxMaxY - playerMinY <= 0.5625) {
                playerCollision.translate(0, boxMaxY - playerMinY, 0);
            }
        }

        playerCollision.setSizeX(playerCollision.getSizeX() + COLLISION_TOLERANCE * 2);
        playerCollision.setSizeZ(playerCollision.getSizeZ() + COLLISION_TOLERANCE * 2);

        if (boundingBox.checkIntersection(x, y, z, playerCollision)) {
            double northZ = boundingBox.getMiddleZ() - (boundingBox.getSizeZ() / 2);
            double southZ = boundingBox.getMiddleZ() + (boundingBox.getSizeZ() / 2);
            double eastX = boundingBox.getMiddleX() + (boundingBox.getSizeX() / 2);
            double westX = boundingBox.getMiddleX() - (boundingBox.getSizeX() / 2);

            double relativeX = playerCollision.getMiddleX() - x;
            double relativeZ = playerCollision.getMiddleZ() - z;

            double translateDistance = northZ - relativeZ - (playerCollision.getSizeZ() / 2);
            if (Math.abs(translateDistance) < COLLISION_TOLERANCE * 1.1) {
                playerCollision.translate(0, 0, translateDistance);
            }

            translateDistance = southZ - relativeZ + (playerCollision.getSizeZ() / 2);
            if (Math.abs(translateDistance) < COLLISION_TOLERANCE * 1.1) {
                playerCollision.translate(0, 0, translateDistance);
            }

            translateDistance = eastX - relativeX + (playerCollision.getSizeX() / 2);
            if (Math.abs(translateDistance) < COLLISION_TOLERANCE * 1.1) {
                playerCollision.translate(translateDistance, 0, 0);
            }

            translateDistance = westX - relativeX - (playerCollision.getSizeX() / 2);
            if (Math.abs(translateDistance) < COLLISION_TOLERANCE * 1.1) {
                playerCollision.translate(translateDistance, 0, 0);
            }
        }

        playerCollision.setSizeX(0.6D);
        playerCollision.setSizeZ(0.6D);
    }
}