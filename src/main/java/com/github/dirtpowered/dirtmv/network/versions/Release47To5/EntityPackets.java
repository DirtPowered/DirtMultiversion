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
import com.github.dirtpowered.dirtmv.data.entity.ObjectType;
import com.github.dirtpowered.dirtmv.data.entity.SpawnableObject;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.MetadataType;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Motion;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.PlayerListEntry;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.TabListAction;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.TabListEntry;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.entity.V1_7EntityTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.metadata.V1_7RTo1_8RMetadataTransformer;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.other.GameProfileFetcher;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.entity.EntityTracker;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

public class EntityPackets extends ServerProtocol {
    private final V1_7RTo1_8RMetadataTransformer metadataTransformer;

    EntityPackets() {
        super(MinecraftVersion.R1_8, MinecraftVersion.R1_7_6);
        metadataTransformer = new V1_7RTo1_8RMetadataTransformer();
    }

    private void refreshPlayerProfile(ServerSession session, GameProfile gameProfile, PacketData originalSpawn, PacketData translatedSpawn) {
        UUID uniqueId = UUID.fromString(originalSpawn.read(Type.V1_7_STRING, 1));
        int entityId = originalSpawn.read(Type.VAR_INT, 0);

        GameProfile filledProfile = new GameProfile(uniqueId, gameProfile.getName());
        filledProfile.getProperties().putAll(gameProfile.getProperties());

        Property[] propertyArray = gameProfile.getProperties().values().toArray(new Property[0]);

        TabListEntry tabRemoveListEntry = new TabListEntry(TabListAction.REMOVE_PLAYER, new PlayerListEntry[]{
                new PlayerListEntry(new GameProfile(uniqueId, gameProfile.getName()), new Property[0], 0, 0, null)
        });

        PacketData removeTab = PacketUtil.createPacket(0x38, new TypeHolder[]{
                set(Type.TAB_LIST_ENTRY, tabRemoveListEntry)
        });

        PacketData destroyPlayer = PacketUtil.createPacket(0x13, new TypeHolder[]{
                set(Type.VAR_INT_ARRAY, new int[]{
                        entityId
                })
        });

        TabListEntry tabAddListEntry = new TabListEntry(TabListAction.ADD_PLAYER, new PlayerListEntry[]{
                new PlayerListEntry(filledProfile, propertyArray, 0, 0, null)
        });

        PacketData addTab = PacketUtil.createPacket(0x38, new TypeHolder[]{
                set(Type.TAB_LIST_ENTRY, tabAddListEntry)
        });

        session.sendPacket(removeTab, PacketDirection.TO_CLIENT, getFrom());
        session.sendPacket(destroyPlayer, PacketDirection.TO_CLIENT, getFrom());
        session.sendPacket(addTab, PacketDirection.TO_CLIENT, getFrom());
        session.sendPacket(translatedSpawn, PacketDirection.TO_CLIENT, getFrom());
    }

    @Override
    public void registerTranslators() {
        // spawn mob
        addTranslator(0x0F, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getUserData().getProtocolStorage();
                EntityType entityType = EntityType.fromEntityTypeId(data.read(Type.BYTE, 1));

                if (storage.hasObject(V1_7EntityTracker.class)) {
                    V1_7EntityTracker tracker = storage.get(V1_7EntityTracker.class);
                    int entityId = data.read(Type.VAR_INT, 0);

                    assert tracker != null;
                    tracker.addEntity(entityId, entityType);
                }

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
        addTranslator(0x1C, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getUserData().getProtocolStorage();

                SpawnableObject entityObject = null;
                int entityId = data.read(Type.INT, 0);

                if (storage.hasObject(EntityTracker.class)) {
                    EntityTracker tracker = storage.get(EntityTracker.class);

                    assert tracker != null;
                    entityObject = tracker.getEntityById(entityId);
                } else if (storage.hasObject(V1_7EntityTracker.class)) {
                    V1_7EntityTracker tracker = storage.get(V1_7EntityTracker.class);

                    assert tracker != null;
                    entityObject = tracker.getEntityById(entityId);
                }

                if (entityObject == null)
                    return cancel();

                WatchableObject[] oldMeta = data.read(Type.V1_7R_METADATA, 1);
                WatchableObject[] watchableObjects = metadataTransformer.transformMetadata(entityObject, oldMeta);

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
        addTranslator(0x12, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

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
        addTranslator(0x13, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getUserData().getProtocolStorage();
                int[] entities = data.read(Type.BYTE_INT_ARRAY, 0);

                if (storage.hasObject(V1_7EntityTracker.class)) {
                    for (int entityId : entities) {
                        V1_7EntityTracker tracker = storage.get(V1_7EntityTracker.class);

                        assert tracker != null;
                        tracker.removeEntity(entityId);
                    }
                }
                return PacketUtil.createPacket(0x13, new TypeHolder[]{
                        set(Type.VAR_INT_ARRAY, entities)
                });
            }
        });

        // spawn player
        addTranslator(0x0C, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getUserData().getProtocolStorage();
                UUID uniqueId = UUID.fromString(data.read(Type.V1_7_STRING, 1));
                String username = data.read(Type.V1_7_STRING, 2);

                if (storage.hasObject(V1_7EntityTracker.class)) {
                    V1_7EntityTracker tracker = storage.get(V1_7EntityTracker.class);
                    int entityId = data.read(Type.VAR_INT, 0);

                    assert tracker != null;
                    tracker.addEntity(entityId, EntityType.HUMAN);
                }

                WatchableObject[] oldMeta = data.read(Type.V1_7R_METADATA, 10);
                WatchableObject[] newMeta = metadataTransformer.transformMetadata(EntityType.HUMAN, oldMeta);

                PacketData playerSpawn = PacketUtil.createPacket(0x0C, new TypeHolder[]{
                        data.read(0),
                        set(Type.UUID, uniqueId),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        data.read(8),
                        data.read(9),
                        set(Type.V1_8R_METADATA, newMeta)
                });

                // create fake profile
                GameProfile profile = new GameProfile(uniqueId, username);

                TabListEntry tabAddListEntry = new TabListEntry(TabListAction.ADD_PLAYER, new PlayerListEntry[]{
                        new PlayerListEntry(profile, new Property[0], 0, 0, null)
                });

                PacketData tabEntry = PacketUtil.createPacket(0x38, new TypeHolder[]{
                        set(Type.TAB_LIST_ENTRY, tabAddListEntry)
                });

                // seems that client overwrites old tab packet after sending a new one
                session.sendPacket(tabEntry, PacketDirection.TO_CLIENT, getFrom());

                // send player spawn (right after tablist packet)
                session.sendPacket(playerSpawn, PacketDirection.TO_CLIENT, getFrom());

                // apply skin
                GameProfileFetcher.getSkinFor(username).whenComplete((gameProfile, throwable) -> {
                    refreshPlayerProfile(session, gameProfile, data, playerSpawn);
                });

                return cancel();
            }
        });

        // entity equipment
        addTranslator(0x04, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

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
        addTranslator(0x35, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

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
        addTranslator(0x10, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

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
        addTranslator(0x1D, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

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
        addTranslator(0x1E, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x1E, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                });
            }
        });

        // spawn object
        addTranslator(0x0E, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getUserData().getProtocolStorage();
                int x = data.read(Type.INT, 2);
                int y = data.read(Type.INT, 3);
                int z = data.read(Type.INT, 4);

                byte type = data.read(Type.BYTE, 1);
                byte yaw = data.read(Type.BYTE, 6);

                Motion motionData = data.read(Type.MOTION, 7);
                ObjectType objectType = ObjectType.fromObjectTypeId(type);

                if (objectType == null)
                    return cancel();

                switch (objectType) {
                    case ITEM:
                        y -= 4;
                        break;
                    case TNT_PRIMED:
                        y -= 16;
                        break;
                    case FALLING_OBJECT:
                        int itemId = motionData.getThrowerId();
                        int itemData = motionData.getThrowerId() >> 16;
                        short mX = motionData.getMotionX();
                        short mY = motionData.getMotionY();
                        short mZ = motionData.getMotionZ();

                        y -= 16;
                        motionData = new Motion(itemId | itemData << 12, mX, mY, mZ);
                        break;
                    case ITEM_FRAME:
                        int rotation = motionData.getThrowerId();
                        if (rotation == 0) {
                            z += 32;
                            yaw = 0;
                        } else if (rotation == 1) {
                            x -= 32;
                            yaw = 64;
                        } else if (rotation == 2) {
                            z -= 32;
                            yaw = -128;
                        } else if (rotation == 3) {
                            x += 32;
                            yaw = -64;
                        }
                        break;
                }

                if (storage.hasObject(V1_7EntityTracker.class)) {
                    V1_7EntityTracker tracker = storage.get(V1_7EntityTracker.class);
                    int entityId = data.read(Type.VAR_INT, 0);

                    assert tracker != null;
                    tracker.addEntity(entityId, objectType);
                }
                return PacketUtil.createPacket(0x0E, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, type),
                        set(Type.INT, x),
                        set(Type.INT, y),
                        set(Type.INT, z),
                        data.read(5),
                        set(Type.BYTE, yaw),
                        set(Type.MOTION, motionData)
                });
            }
        });
    }
}
