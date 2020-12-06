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

package com.github.dirtpowered.dirtmv.network.versions.Release22To17.movement;

import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockLocation;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Location;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage.BlockStorage;

import java.util.ArrayList;

public class MovementTranslator {
    private final static double PLAYER_MODEL_X = 0.6D;
    private final static double PLAYER_MODEL_Y = 1.8D;
    private final static double PLAYER_MODEL_Z = 0.6D;

    private final static BlockCollision FENCE_COLLISION;
    private final static BlockCollision CHEST_COLLISION;

    static {
        FENCE_COLLISION = new FenceCollision();
        CHEST_COLLISION = new ChestCollision();
    }

    public static void updateBoundingBox(ServerSession session, Location position) {
        ProtocolStorage storage = session.getUserData().getProtocolStorage();
        if (!storage.hasObject(BoundingBox.class)) {
            BoundingBox b = new BoundingBox(
                    position.getX(), position.getY(), position.getZ(),
                    PLAYER_MODEL_X, PLAYER_MODEL_Y, PLAYER_MODEL_Z
            );

            storage.set(BoundingBox.class, b);
        }

        BoundingBox boundingBox = storage.get(BoundingBox.class);

        if (boundingBox != null) {
            boundingBox.setMiddleX(position.getX());
            boundingBox.setMiddleY(position.getY() + 0.9D);
            boundingBox.setMiddleZ(position.getZ());
        }
    }

    private static boolean needsCorrection(int blockId) {
        return blockId == 54 || blockId == 85;
    }

    private static ArrayList<BlockCollision> getPossibleCollisions(Location position, ServerSession session) {
        ProtocolStorage protocolStorage = session.getUserData().getProtocolStorage();
        BoundingBox boundingBox = protocolStorage.get(BoundingBox.class);
        BlockStorage storage = protocolStorage.get(BlockStorage.class);

        // it's always not-null
        assert storage != null;
        assert boundingBox != null;

        ArrayList<BlockCollision> possibleCollisions = new ArrayList<>();

        int minCollisionX = (int) Math.floor(position.getX() - (boundingBox.getSizeX() / 2));
        int maxCollisionX = (int) Math.floor(position.getX() + (boundingBox.getSizeX() / 2));

        int minCollisionY = (int) Math.floor(position.getY() - 0.5);
        int maxCollisionY = (int) Math.floor(position.getY() + boundingBox.getSizeY());

        int minCollisionZ = (int) Math.floor(position.getZ() - (boundingBox.getSizeZ() / 2));
        int maxCollisionZ = (int) Math.floor(position.getZ() + (boundingBox.getSizeZ() / 2));

        for (int y = minCollisionY; y < maxCollisionY + 1; y++) {
            for (int x = minCollisionX; x < maxCollisionX + 1; x++) {
                for (int z = minCollisionZ; z < maxCollisionZ + 1; z++) {

                    BlockLocation loc = new BlockLocation(x, y, z);
                    int blockId = storage.getBlockAt(loc.getX(), loc.getY(), loc.getZ());

                    if (needsCorrection(blockId)) {
                        if (blockId == 85) {
                            FENCE_COLLISION.setPosition(loc.getX(), loc.getY(), loc.getZ());
                            possibleCollisions.add(FENCE_COLLISION);
                        } else {
                            CHEST_COLLISION.setPosition(loc.getX(), loc.getY(), loc.getZ());
                            possibleCollisions.add(CHEST_COLLISION);
                        }
                    }
                }
            }
        }

        return possibleCollisions;
    }

    public static Location correctPosition(ServerSession session, double x, double y, double z) {
        ProtocolStorage protocolStorage = session.getUserData().getProtocolStorage();
        BoundingBox b = protocolStorage.get(BoundingBox.class);

        Location loc = new Location(x, y, z);
        ArrayList<BlockCollision> possibleCollisions = getPossibleCollisions(loc, session);

        for (BlockCollision blockCollision : possibleCollisions) {
            if (blockCollision != null) {
                blockCollision.correctPosition(b, blockCollision instanceof ChestCollision);
            }
        }

        assert b != null;
        return new Location(b.getMiddleX(), b.getMiddleY() - 0.9, b.getMiddleZ());
    }

    private final static class ChestCollision extends BlockCollision {

        ChestCollision() {
            boundingBox = new BoundingBox(0.5, 0.5, 0.5, 1.0, 1.0, 1.0);
        }
    }

    private final static class FenceCollision extends BlockCollision {

        FenceCollision() {
            boundingBox = new BoundingBox(0.5, 0.5, 0.5, 0, 1.0, 0);
        }
    }
}
