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

package com.github.dirtpowered.dirtmv.network.versions.Release39To29;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.entity.EntityType;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Location;
import com.github.dirtpowered.dirtmv.data.protocol.objects.MetadataType;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Motion;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.EncryptionUtils;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage.BlockStorage;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.Entity;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.EntityIdToTypeString;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.EntityTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.HumanEntity;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.WorldEntityEvent;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.model.AbstractEntity;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.item.CreativeItemList;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.OpenChestTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.UpdateTask;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.WorldSound;
import lombok.SneakyThrows;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ProtocolRelease39To29 extends ServerProtocol {

    public ProtocolRelease39To29() {
        super(MinecraftVersion.R1_3_1, MinecraftVersion.R1_2_4);
        addGroup(new WorldPackets());
    }

    @Override
    public void onConnect(ServerSession session) {
        ProtocolStorage storage = session.getStorage();

        storage.set(UpdateTask.class, new UpdateTask(session));
        storage.set(OpenChestTracker.class, new OpenChestTracker());

        if (!storage.hasObject(BlockStorage.class)) {
            storage.set(BlockStorage.class, new BlockStorage(MinecraftVersion.R1_2_4));
        }
    }

    private AbstractEntity getNearestEntity(EntityTracker tracker, Location location, double range) {
        AbstractEntity nearbyEntity = new Entity(-1, new Location(0, 0, 0), EntityType.PIG);

        for (AbstractEntity entity : tracker.getTrackedEntities().values()) {
            if (entity.getLocation().distanceTo(location) < range && entity.getLocation().distanceTo(location) != 0.0D) {
                nearbyEntity = entity;
            }
        }

        return nearbyEntity;
    }

    private void updateEntityLocation(ServerSession session, int entityId, int x, int y, int z, boolean relative) {
        EntityTracker tracker = session.getStorage().get(EntityTracker.class);
        if (tracker != null) {
            AbstractEntity e = tracker.getEntity(entityId);
            if (e != null) {
                Location oldLoc = e.getLocation();

                double xPos = x / 32.0D;
                double yPos = y / 32.0D;
                double zPos = z / 32.0D;

                Location newLoc;
                if (relative) {
                    newLoc = new Location(oldLoc.getX() + xPos, oldLoc.getY() + yPos, oldLoc.getZ() + zPos);
                } else {
                    newLoc = new Location(xPos, yPos, zPos);
                }

                e.setLocation(newLoc);
            }
        }
    }

    @Override
    public void registerTranslators() {
        // handshake
        addTranslator(0x02, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return cancel(); // since 1.3 handshake is one-way (client -> server)
            }
        });

        // handshake
        addTranslator(0x02, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String username = data.read(Type.STRING, 1);
                session.getUserData().setUsername(username);

                PacketData encryptRequest = EncryptionUtils.createEncryptionRequest(session);

                // server -> client
                session.sendPacket(encryptRequest, PacketDirection.TO_CLIENT, getFrom());
                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.STRING, username)
                });
            }
        });

        // client shared key
        addTranslator(0xFC, PacketDirection.TO_SERVER, new PacketTranslator() {

            @SneakyThrows
            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                PacketData request = session.getUserData().getProxyRequest();
                SecretKey sharedKey = EncryptionUtils.getSecret(data, request);

                // server -> client
                EncryptionUtils.sendEmptyEncryptionResponse(session, getFrom());

                // enable encryption
                EncryptionUtils.setEncryption(session.getChannel(), sharedKey);
                return cancel(); // cancel packet
            }
        });

        // client command
        addTranslator(0xCD, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                byte command = data.read(Type.BYTE, 0);

                if (command == 0x00) {
                    String username = session.getUserData().getUsername();

                    return PacketUtil.createPacket(0x01, new TypeHolder[]{
                            set(Type.INT, 29), // protocol version
                            set(Type.STRING, username),
                            set(Type.STRING, "default"),
                            set(Type.INT, 0),
                            set(Type.INT, 0),
                            set(Type.BYTE, 0),
                            set(Type.BYTE, 0),
                            set(Type.BYTE, 0),
                    });
                } else {

                    return PacketUtil.createPacket(0x09, new TypeHolder[]{
                            set(Type.INT, 0),
                            set(Type.BYTE, 0),
                            set(Type.BYTE, 0),
                            set(Type.SHORT, 0),
                            set(Type.STRING, "default"),
                    });
                }
            }
        });

        // login
        addTranslator(0x01, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                session.getUserData().setEntityId(data.read(Type.INT, 0));

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        data.read(0),
                        data.read(2),
                        set(Type.BYTE, data.read(Type.INT, 3).byteValue()),
                        set(Type.BYTE, data.read(Type.INT, 4).byteValue()),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                });
            }
        });

        // tab command complete
        addTranslator(0xCB, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return cancel();
            }
        });

        // client settings
        addTranslator(0xCC, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return cancel();
            }
        });

        // spawn position
        addTranslator(0x06, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                EntityTracker tracker = new EntityTracker();

                int x = data.read(Type.INT, 0);
                int y = data.read(Type.INT, 1);
                int z = data.read(Type.INT, 2);

                Location loc = new Location(x, y, z);
                tracker.addEntity(-999, new HumanEntity(-999, loc));

                session.getStorage().set(EntityTracker.class, tracker);
                return data;
            }
        });

        // window click
        addTranslator(0x66, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                ItemStack newItem = data.read(Type.V1_3R_ITEM, 5);

                return PacketUtil.createPacket(0x66, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.V1_0R_ITEM, newItem)
                });
            }
        });

        // set slot
        addTranslator(0x67, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                ItemStack oldItem = data.read(Type.V1_0R_ITEM, 2);

                return PacketUtil.createPacket(0x67, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_3R_ITEM, oldItem)
                });
            }
        });

        // window items
        addTranslator(0x68, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                ItemStack[] items = data.read(Type.V1_0R_ITEM_ARRAY, 1);

                for (ItemStack item : items) {
                    if (item != null && item.getCompoundTag() == null) {
                        // since 1.3 all items contains NBT data
                        item.setCompoundTag(CompoundBinaryTag.empty());
                    }
                }

                return PacketUtil.createPacket(0x68, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_3R_ITEM_ARRAY, items)
                });
            }
        });

        // entity destroy
        addTranslator(0x1D, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);

                EntityTracker tracker = session.getStorage().get(EntityTracker.class);
                if (tracker != null) {
                    tracker.removeEntity(entityId);
                }

                return PacketUtil.createPacket(0x1D, new TypeHolder[]{
                        set(Type.BYTE_INT_ARRAY, new int[]{entityId})
                });
            }
        });

        // mob spawn
        addTranslator(0x18, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);
                EntityType entityType = EntityType.fromEntityTypeId(data.read(Type.BYTE, 1));

                double x = data.read(Type.INT, 2) / 32.0D;
                double y = data.read(Type.INT, 3) / 32.0D;
                double z = data.read(Type.INT, 4) / 32.0D;

                Location location = new Location(x, y, z);
                Entity entity = new Entity(entityId, location, entityType);

                EntityTracker tracker = session.getStorage().get(EntityTracker.class);
                if (tracker != null) {
                    tracker.addEntity(entityId, entity);
                }

                return PacketUtil.createPacket(0x18, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        set(Type.SHORT, 0),
                        set(Type.SHORT, 0),
                        set(Type.SHORT, 0),
                        data.read(8)
                });
            }
        });

        // entity status
        addTranslator(0x26, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);
                byte status = data.read(Type.BYTE, 1);

                if (status == 2) { // hurt
                    WorldEntityEvent.onDamage(session, entityId);
                }

                if (status == 3) { // death
                    WorldEntityEvent.onDeath(session, entityId);
                }

                return data;
            }
        });

        // entity teleport
        addTranslator(0x22, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);
                int x = data.read(Type.INT, 1);
                int y = data.read(Type.INT, 2);
                int z = data.read(Type.INT, 3);

                updateEntityLocation(session, entityId, x, y, z, false);
                return data;
            }
        });

        // entity relative move
        addTranslator(0x1F, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);
                int x = data.read(Type.BYTE, 1);
                int y = data.read(Type.BYTE, 2);
                int z = data.read(Type.BYTE, 3);

                updateEntityLocation(session, entityId, x, y, z, true);
                return data;
            }
        });

        // entity relative look move
        addTranslator(0x21, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);
                int x = data.read(Type.BYTE, 1);
                int y = data.read(Type.BYTE, 2);
                int z = data.read(Type.BYTE, 3);

                updateEntityLocation(session, entityId, x, y, z, true);
                return data;
            }
        });

        // vehicle spawn
        addTranslator(0x17, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                Motion motion = data.read(Type.MOTION, 5);

                int throwerId = motion.getThrowerId();
                byte type = data.read(Type.BYTE, 1);

                // sound emulation, entity mount fixes
                EntityTracker tracker = session.getStorage().get(EntityTracker.class);
                if (tracker != null) {
                    int entityId = data.read(Type.INT, 0);
                    double x = data.read(Type.INT, 2) / 32.0D;
                    double y = data.read(Type.INT, 3) / 32.0D;
                    double z = data.read(Type.INT, 4) / 32.0D;

                    float pitch;

                    Location loc = new Location(x, y, z);

                    switch (type) {
                        case 1:
                            // cache boats
                            Entity boat = new Entity(entityId, loc, EntityType.BOAT);
                            tracker.addEntity(entityId, boat);
                            break;
                        case 10:
                        case 11:
                        case 12:
                            // cache minecarts
                            Entity minecart = new Entity(entityId, loc, EntityType.MINECART);
                            tracker.addEntity(entityId, minecart);
                            break;
                        case 50:
                            // cache primed tnt entity
                            Entity primedTNT = new Entity(entityId, loc, EntityType.PRIMED_TNT);
                            tracker.addEntity(entityId, primedTNT);

                            WorldEntityEvent.onCustomAction(session, entityId);
                            break;
                        case 60:
                            // bow sound
                            pitch = 1.0F / (session.getMain().getSharedRandom().nextFloat() * 0.4F + 1.2F) + 0.5F;
                            WorldEntityEvent.playSoundAt(session, loc, WorldSound.RANDOM_BOW, 0.2F, pitch);
                            break;
                        case 61: // snowball
                        case 62: // egg
                        case 90: // fishing rod
                        case 65: // ender pearl
                        case 72: // ender eye
                        case 73: // throwable potion
                        case 75: // exp bottle
                            pitch = 0.4F / (session.getMain().getSharedRandom().nextFloat() * 0.4F + 0.8F);
                            WorldEntityEvent.playSoundAt(session, loc, WorldSound.RANDOM_BOW, 0.5F, pitch);

                            if (type == 90) {
                                Location hookLocation = new Location(x, y, z);
                                AbstractEntity nearest = getNearestEntity(tracker, hookLocation, 2.0D);

                                throwerId = nearest.getEntityId() != -1 ? nearest.getEntityId() : session.getUserData().getEntityId();
                            }
                            break;
                    }
                }

                switch (type) {
                    case 70:
                        throwerId = 12;
                        break;
                    case 71:
                        throwerId = 13;
                        type = 70;
                        break;
                    case 74:
                        throwerId = 122;
                        type = 70;
                        break;
                }

                motion.setThrowerId(throwerId);

                return PacketUtil.createPacket(0x17, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, type),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.MOTION, motion)
                });
            }
        });

        // block place
        addTranslator(0x0F, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                ItemStack newItem = data.read(Type.V1_3R_ITEM, 4);

                return PacketUtil.createPacket(0x0F, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.V1_0R_ITEM, newItem)
                });
            }
        });

        // creative set slot
        addTranslator(0x6B, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack newItem = data.read(Type.V1_3R_ITEM, 1);

                boolean notNull = newItem != null;

                if (notNull && !CreativeItemList.exists(newItem.getItemId())) {
                    // replace all unknown items to stone
                    newItem.setItemId(1);
                    newItem.setData(0);
                }

                return PacketUtil.createPacket(0x6B, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_0R_ITEM, newItem)
                });
            }
        });

        // named entity spawn
        addTranslator(0x14, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                EntityTracker tracker = session.getStorage().get(EntityTracker.class);
                if (tracker != null) {
                    int entityId = data.read(Type.INT, 0);
                    double x = data.read(Type.INT, 2) / 32.0D;
                    double y = data.read(Type.INT, 3) / 32.0D;
                    double z = data.read(Type.INT, 4) / 32.0D;

                    Location loc = new Location(x, y, z);

                    HumanEntity human = new HumanEntity(entityId, loc);
                    tracker.addEntity(entityId, human);
                }

                /* default 1.3.x metadata */
                List<WatchableObject> watchableObjects = Arrays.asList(
                        new WatchableObject(MetadataType.BYTE, 0, 0),
                        new WatchableObject(MetadataType.BYTE, 16, 0),
                        new WatchableObject(MetadataType.SHORT, 1, 300),
                        new WatchableObject(MetadataType.BYTE, 17, 0),
                        new WatchableObject(MetadataType.INT, 8, 0)
                );

                return PacketUtil.createPacket(0x14, new TypeHolder[]{
                        data.read(0), // entityId
                        data.read(1), // playerName
                        data.read(2), // x
                        data.read(3), // y
                        data.read(4), // z
                        data.read(5), // yaw
                        data.read(6), // pitch
                        data.read(7), // item
                        set(Type.V1_3B_METADATA, watchableObjects.toArray(new WatchableObject[0])) // default metadata
                });
            }
        });

        // entity metadata
        addTranslator(0x28, PacketDirection.TO_CLIENT, new PacketTranslator() {
            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);

                WatchableObject[] watchableObjects = data.read(Type.V1_3B_METADATA, 1);
                EntityTracker tracker = session.getStorage().get(EntityTracker.class);

                if (tracker != null) {
                    for (WatchableObject watchableObject : watchableObjects) {
                        int index = watchableObject.getIndex();
                        MetadataType type = watchableObject.getType();
                        Object value = watchableObject.getValue();

                        if (type == MetadataType.BYTE && index == 0 && tracker.isEntityTracked(entityId)) {
                            if (((Byte) value).intValue() == 4) { //entity mount
                                if (tracker.getEntity(entityId).getEntityType() == EntityType.HUMAN) {
                                    HumanEntity humanEntity = (HumanEntity) tracker.getEntity(entityId);

                                    AbstractEntity nearbyEntity = getNearestEntity(tracker, humanEntity.getLocation(), 1.5D);
                                    EntityType eType = nearbyEntity.getEntityType();

                                    if (nearbyEntity.getEntityId() != -1) {
                                        if (eType == EntityType.MINECART || eType == EntityType.PIG || eType == EntityType.BOAT) {
                                            PacketData entityAttach = PacketUtil.createPacket(0x27, new TypeHolder[]{
                                                    set(Type.INT, entityId),
                                                    set(Type.INT, nearbyEntity.getEntityId()),

                                            });

                                            humanEntity.setRidingEntity(true);
                                            session.sendPacket(entityAttach, PacketDirection.TO_CLIENT, getFrom());
                                        }
                                    }
                                }
                            } else if (((Byte) value).intValue() == 0) { // un-mount
                                if (tracker.isEntityTracked(entityId) && tracker.getEntity(entityId) instanceof HumanEntity) {
                                    HumanEntity humanEntity = (HumanEntity) tracker.getEntity(entityId);
                                    if (humanEntity.isRidingEntity()) {
                                        PacketData entityAttach = PacketUtil.createPacket(0x27, new TypeHolder[]{
                                                set(Type.INT, entityId),
                                                set(Type.INT, -1),
                                        });

                                        humanEntity.setRidingEntity(false);
                                        session.sendPacket(entityAttach, PacketDirection.TO_CLIENT, getFrom());
                                    }
                                }
                            }
                        }
                    }
                }

                return data;
            }
        });

        // explosion
        addTranslator(0x3C, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                double x = data.read(Type.DOUBLE, 0);
                double y = data.read(Type.DOUBLE, 1);
                double z = data.read(Type.DOUBLE, 2);

                Location loc = new Location(x, y, z);
                WorldEntityEvent.playSoundAt(session, loc, WorldSound.RANDOM_EXPLODE);

                return PacketUtil.createPacket(0x3C, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.FLOAT, 0.0F),
                        set(Type.FLOAT, 0.0F),
                        set(Type.FLOAT, 0.0F)
                });
            }
        });

        // item collect
        addTranslator(0x16, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                EntityTracker tracker = session.getStorage().get(EntityTracker.class);
                if (tracker != null) {
                    int entityId = data.read(Type.INT, 0);

                    AbstractEntity itemPickup = tracker.getEntity(entityId);
                    if (itemPickup != null) {

                        Random shared = session.getMain().getSharedRandom();

                        float pitch = ((shared.nextFloat() - shared.nextFloat()) * 0.7F + 1.0F) * 2.0F;
                        WorldEntityEvent.playSoundAt(session, itemPickup.getLocation(), WorldSound.RANDOM_POP, 0.2F, pitch);
                    }
                }

                return data;
            }
        });

        // pickup spawn
        addTranslator(0x15, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                EntityTracker tracker = session.getStorage().get(EntityTracker.class);
                if (tracker != null) {
                    int entityId = data.read(Type.INT, 0);
                    double x = data.read(Type.INT, 4) / 32.0D;
                    double y = data.read(Type.INT, 5) / 32.0D;
                    double z = data.read(Type.INT, 6) / 32.0D;

                    Location b = new Location(x, y, z);

                    Entity itemPickup = new Entity(entityId, b, EntityType.ITEM);
                    tracker.addEntity(entityId, itemPickup);
                }

                return data;
            }
        });

        // player abilities
        addTranslator(0xCA, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                byte mask = data.read(Type.BYTE, 0);

                return PacketUtil.createPacket(0xCA, new TypeHolder[]{
                        set(Type.BOOLEAN, ((mask & 1) > 0)),
                        set(Type.BOOLEAN, ((mask & 2) > 0)),
                        set(Type.BOOLEAN, ((mask & 4) > 0)),
                        set(Type.BOOLEAN, ((mask & 8) > 0)),
                });
            }
        });

        // player abilities
        addTranslator(0xCA, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                boolean invulnerable = data.read(Type.BOOLEAN, 0);
                boolean flying = data.read(Type.BOOLEAN, 1);
                boolean allowFlying = data.read(Type.BOOLEAN, 2);
                boolean instantBreak = data.read(Type.BOOLEAN, 3);

                byte mask = 0;

                if (invulnerable) {
                    mask = (byte) (mask | 1);
                }

                if (flying) {
                    mask = (byte) (mask | 2);
                }

                if (allowFlying) {
                    mask = (byte) (mask | 4);
                }

                if (instantBreak) {
                    mask = (byte) (mask | 8);
                }

                return PacketUtil.createPacket(0xCA, new TypeHolder[]{
                        set(Type.BYTE, mask),
                        set(Type.BYTE, (byte) (0.05f * 255)),
                        set(Type.BYTE, (byte) (0.1f * 255)),
                });
            }
        });

        // player look move
        addTranslator(0x0D, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                double x = data.read(Type.DOUBLE, 0);
                double y = data.read(Type.DOUBLE, 1);
                double z = data.read(Type.DOUBLE, 3);
                EntityTracker tracker = session.getStorage().get(EntityTracker.class);

                AbstractEntity e = tracker.getEntity(-999);
                if (e != null) {
                    e.setLocation(new Location(x, y, z));
                }
                return data;
            }
        });

        // update tile entity
        addTranslator(0x84, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                short y = data.read(Type.SHORT, 1);
                int z = data.read(Type.INT, 2);

                int entityTypeId = data.read(Type.INT, 4);

                CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();

                builder.putString("EntityId", EntityIdToTypeString.getEntityTypeString(entityTypeId));
                builder.putShort("Delay", (short) 20);
                builder.putInt("x", x);
                builder.putInt("y", y);
                builder.putInt("z", z);

                CompoundBinaryTag tileTag = builder.build();

                return PacketUtil.createPacket(0x84, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.COMPOUND_TAG, tileTag)
                });
            }
        });

        // entity equipment
        addTranslator(0x05, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                short itemId = data.read(Type.SHORT, 2);
                short itemData = data.read(Type.SHORT, 3);

                ItemStack itemStack;

                if (itemId == -1) {
                    itemStack = null;
                } else {
                    itemStack = new ItemStack(itemId, 0, itemData, null);
                }

                return PacketUtil.createPacket(0x05, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_3R_ITEM, itemStack)
                });
            }
        });
    }
}
