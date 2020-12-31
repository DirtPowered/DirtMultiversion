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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.entity.EntityType;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.MetadataType;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.metadata.V1_7RTo1_8RMetadataTransformer;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.entity.EntityTracker;

import java.util.UUID;

public class EntityPackets extends ServerProtocol {
    private V1_7RTo1_8RMetadataTransformer metadataTransformer;

    EntityPackets() {
        super(MinecraftVersion.R1_8, MinecraftVersion.R1_7_6);

        metadataTransformer = new V1_7RTo1_8RMetadataTransformer();
    }

    @Override
    public void registerTranslators() {
        // spawn mob
        addTranslator(0x0F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                EntityType entityType = EntityType.fromEntityTypeId(data.read(Type.BYTE, 1));

                WatchableObject[] oldMeta = data.read(Type.V1_7R_METADATA, 11);
                WatchableObject[] newMeta = metadataTransformer.transformMetadata(entityType, oldMeta);

                return PacketUtil.createPacket(0x0F, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        data.read(8),
                        data.read(9),
                        data.read(10),
                        set(Type.V1_8R_METADATA, newMeta)
                });
            }
        });

        // entity metadata
        addTranslator(0x1C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                // get existing entity tracker (TODO: create if not exist)
                ProtocolStorage protocolStorage = session.getUserData().getProtocolStorage();

                if (!protocolStorage.hasObject(EntityTracker.class)) {
                    return new PacketData(-1);
                }

                EntityTracker tracker = session.getUserData().getProtocolStorage().get(EntityTracker.class);
                assert tracker != null;

                EntityType entityType = tracker.getEntityById(data.read(Type.INT, 0));
                WatchableObject[] oldMeta = data.read(Type.V1_7R_METADATA, 1);

                WatchableObject[] watchableObjects = metadataTransformer.transformMetadata(entityType, oldMeta);

                for (int i = 0; i < watchableObjects.length; i++) {
                    WatchableObject watchableObject = watchableObjects[i];
                    if (watchableObject.getType() == MetadataType.ITEM) {
                        ItemStack obj = (ItemStack) watchableObject.getValue();

                        if (obj != null) {
                            obj = InventoryPackets.itemRemapper.replaceItem(obj);
                        }

                        watchableObjects[i] = new WatchableObject(MetadataType.ITEM, watchableObject.getIndex(), obj);
                    }
                }

                return PacketUtil.createPacket(0x1C, new TypeHolder[] {
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        set(Type.V1_8R_METADATA, watchableObjects)
                });
            }
        });

        // entity velocity
        addTranslator(0x12, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x12, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        data.read(3)
                });
            }
        });

        // entity destroy
        addTranslator(0x13, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x13, new TypeHolder[]{
                        set(Type.VAR_INT_ARRAY, data.read(Type.BYTE_INT_ARRAY, 0))
                });
            }
        });

        // spawn player
        addTranslator(0x0C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                UUID playerOfflineUUID = UUID.fromString(data.read(Type.V1_7_STRING, 1));
                WatchableObject[] oldMeta = data.read(Type.V1_7R_METADATA, 10);
                WatchableObject[] newMeta = metadataTransformer.transformMetadata(EntityType.HUMAN, oldMeta);

                return PacketUtil.createPacket(0x0C, new TypeHolder[]{
                        data.read(0),
                        set(Type.UUID, playerOfflineUUID),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        data.read(8),
                        data.read(9),
                        set(Type.V1_8R_METADATA, newMeta)
                });
            }
        });

        // entity equipment
        addTranslator(0x04, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x04, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        set(Type.V1_8R_ITEM, data.read(Type.V1_3R_ITEM, 2)),
                });
            }
        });

        // update tile entity
        addTranslator(0x35, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                short y = data.read(Type.SHORT, 1);
                int z = data.read(Type.INT, 2);

                return PacketUtil.createPacket(0x35, new TypeHolder[]{
                        set(Type.LONG, ProtocolRelease47To5.toBlockPosition(x, y, z)),
                        data.read(3),
                        set(Type.COMPRESSED_COMPOUND_TAG, data.read(Type.COMPOUND_TAG, 4))
                });
            }
        });

        // spawn painting
        addTranslator(0x10, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 2);
                int y = data.read(Type.INT, 3);
                int z = data.read(Type.INT, 4);

                int direction = data.read(Type.INT, 5);

                switch (direction) {
                    case 0:
                        z += 1;
                        break;
                    case 1:
                        x -= 1;
                        break;
                    case 2:
                        z -= 1;
                        break;
                    case 3:
                        x += 1;
                        break;
                }

                return PacketUtil.createPacket(0x10, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.LONG, ProtocolRelease47To5.toBlockPosition(x, y, z)),
                        set(Type.BYTE, data.read(Type.INT, 5).byteValue())
                });
            }
        });

        // entity effect
        addTranslator(0x1D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x1D, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        set(Type.VAR_INT, data.read(Type.SHORT, 3).intValue()),
                        set(Type.BOOLEAN, false)
                });
            }
        });

        // remove entity effect
        addTranslator(0x1E, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x1E, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                });
            }
        });

        // spawn object
        addTranslator(0x0E, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 2);
                int y = data.read(Type.INT, 3);
                int z = data.read(Type.INT, 4);

                byte type = data.read(Type.BYTE, 1);

                // fixes entity bouncing
                switch (type) {
                    case 2: // item
                        y = (int) ((y / 32.0D) - 0.125D) * 32;
                        break;
                    case 50: // primed tnt
                        y = (int) ((y / 32.0D) - 0.5D) * 32;
                        break;
                }

                return PacketUtil.createPacket(0x0E, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        set(Type.INT, x),
                        set(Type.INT, y),
                        set(Type.INT, z),
                        data.read(5),
                        data.read(6),
                        data.read(7)
                });
            }
        });
    }
}
