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

package com.github.dirtpowered.dirtmv.network.versions.Release73To61;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.entity.EntityType;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_1EntityAttributes;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.data.sound.SoundRemapper;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.entity.EntityTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.metadata.V1_5RTo1_6RMetadataTransformer;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.ping.ServerMotd;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ProtocolRelease73To61 extends ServerProtocol {

    private SoundRemapper soundRemapper;
    private V1_5RTo1_6RMetadataTransformer metadataTransformer;

    public ProtocolRelease73To61() {
        super(MinecraftVersion.R1_6_1, MinecraftVersion.R1_5_2);

        soundRemapper = new SoundRemapper("1_5To1_6SoundMappings");
        metadataTransformer = new V1_5RTo1_6RMetadataTransformer();
    }

    private PacketData getDefaultAttributes(int entityId) {
        Map<String, Double> map = new HashMap<>();
        map.put("generic.movementSpeed", 0.1D);

        V1_6_1EntityAttributes attrObj = new V1_6_1EntityAttributes(entityId, map);

        return PacketUtil.createPacket(0x2C, new TypeHolder[] {
                set(Type.V1_6_1_ENTITY_ATTRIBUTES, attrObj)
        });
    }

    @Override
    public void registerTranslators() {
        // login
        addTranslator(0x01, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                int entityId = data.read(Type.INT, 0);
                session.getUserData().setEntityId(entityId);
                EntityTracker tracker = new EntityTracker();

                tracker.addEntity(entityId, EntityType.HUMAN);
                session.getUserData().getProtocolStorage().set(EntityTracker.class, tracker);

                // send entity attributes (fixes fast movement)
                session.sendPacket(data, PacketDirection.SERVER_TO_CLIENT, getFrom());
                session.sendPacket(getDefaultAttributes(entityId), PacketDirection.SERVER_TO_CLIENT, getFrom());

                return new PacketData(-1);
            }
        });

        // respawn
        addTranslator(0x09, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                int entityId = session.getUserData().getEntityId();

                session.sendPacket(data, PacketDirection.SERVER_TO_CLIENT, getFrom());
                session.sendPacket(getDefaultAttributes(entityId), PacketDirection.SERVER_TO_CLIENT, getFrom());

                return new PacketData(-1);
            }
        });

        // handshake
        addTranslator(0x02, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                if (data.getObjects().length < 3) {
                    return new PacketData(-1);
                }

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.BYTE, 61),
                        data.read(1),
                        data.read(2),
                        data.read(3)
                });
            }
        });

        // kick disconnect
        addTranslator(0xFF, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                if (session.getUserData().getPreNettyProtocolState() != PreNettyProtocolState.STATUS)
                    return data;

                String reason = data.read(Type.STRING, 0);

                ServerMotd pingMessage = ServerMotd.deserialize(reason);

                pingMessage.setVersionName("1.6.1");
                pingMessage.setProtocol(session.getUserData().getClientVersion().getRegistryId());

                return PacketUtil.createPacket(0xFF, new TypeHolder[] {
                        set(Type.STRING, ServerMotd.serialize(pingMessage))
                });
            }
        });

        // chat
        addTranslator(0x03, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String message = data.read(Type.STRING, 0);

                return PacketUtil.createPacket(0x03, new TypeHolder[]{
                        set(Type.STRING, ChatUtils.legacyToJsonString(message))
                });
            }
        });

        // update health
        addTranslator(0x08, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x08, new TypeHolder[]{
                        set(Type.FLOAT, data.read(Type.SHORT, 0).floatValue()),
                        data.read(1),
                        data.read(2)
                });
            }
        });

        // mob spawn
        addTranslator(0x18, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                EntityType entityType = EntityType.fromEntityTypeId(data.read(Type.BYTE, 1));
                int entityId = data.read(Type.INT, 0);

                EntityTracker tracker = session.getUserData().getProtocolStorage().get(EntityTracker.class);
                if (tracker != null) {
                    tracker.addEntity(entityId, entityType);
                }

                WatchableObject[] oldMeta = data.read(Type.V1_4R_METADATA, 11);
                WatchableObject[] newMeta = metadataTransformer.transformMetadata(entityType, oldMeta);

                return PacketUtil.createPacket(0x18, new TypeHolder[] {
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
                        set(Type.V1_4R_METADATA, newMeta),
                });
            }
        });

        // entity destroy
        addTranslator(0x1D, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int[] entities = data.read(Type.BYTE_INT_ARRAY, 0);

                EntityTracker tracker = session.getUserData().getProtocolStorage().get(EntityTracker.class);
                for (int entityId : entities) {
                    if (tracker != null) {
                        tracker.removeEntity(entityId);
                    }
                }

                return data;
            }
        });

        // entity metadata
        addTranslator(0x28, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);

                EntityType entityType = null;
                EntityTracker tracker = session.getUserData().getProtocolStorage().get(EntityTracker.class);
                if (tracker != null) {
                    entityType = tracker.getEntityById(entityId);
                }

                if (entityType == null) {
                    log.warn("[{}] skipping translating metadata for {}. Entity is not tracked", session.getLogTag(), entityId);
                    return new PacketData(-1);
                }

                WatchableObject[] oldMeta = data.read(Type.V1_4R_METADATA, 1);
                WatchableObject[] newMeta = metadataTransformer.transformMetadata(entityType, oldMeta);

                return PacketUtil.createPacket(0x28, new TypeHolder[] {
                        data.read(0),
                        set(Type.V1_4R_METADATA, newMeta)
                });
            }
        });

        // name entity spawn
        addTranslator(0x14, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int entityId = data.read(Type.INT, 0);

                EntityTracker tracker = session.getUserData().getProtocolStorage().get(EntityTracker.class);
                if (tracker != null) {
                    tracker.addEntity(entityId, EntityType.HUMAN);
                }

                WatchableObject[] oldMeta = data.read(Type.V1_4R_METADATA, 8);
                WatchableObject[] newMeta = metadataTransformer.transformMetadata(EntityType.HUMAN, oldMeta);

                return PacketUtil.createPacket(0x14, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                        set(Type.V1_4R_METADATA, newMeta)
                });
            }
        });

        // vehicle spawn
        addTranslator(0x17, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                if (data.read(Type.BYTE, 1) == 0x02 /* item */) {
                    int entityId = data.read(Type.INT, 0);

                    EntityTracker tracker = session.getUserData().getProtocolStorage().get(EntityTracker.class);
                    if (tracker != null) {
                        tracker.addEntity(entityId, EntityType.ITEM);
                    }
                }

                return data;
            }
        });

        // statistics
        addTranslator(0xC8, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0xC8, new TypeHolder[] {
                        data.read(0),
                        set(Type.INT, data.read(Type.BYTE, 1).intValue())
                });
            }
        });

        // level sound
        addTranslator(0x3E, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String soundName = data.read(Type.STRING, 0);
                String newSoundName = soundRemapper.getNewSoundName(soundName);

                if (newSoundName.isEmpty())
                    return new PacketData(-1);

                return PacketUtil.createPacket(0x3E, new TypeHolder[]{
                        set(Type.STRING, newSoundName),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                });
            }
        });

        // player abilities
        addTranslator(0xCA, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0xCA, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, (byte) (data.read(Type.FLOAT, 1) * 255F)),
                        set(Type.BYTE, (byte) (data.read(Type.FLOAT, 2) * 255F))
                });
            }
        });

        // player abilities
        addTranslator(0xCA, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0xCA, new TypeHolder[]{
                        data.read(0),
                        set(Type.FLOAT, (data.read(Type.BYTE, 1) / 255F)),
                        set(Type.FLOAT, (data.read(Type.BYTE, 2) / 255F))
                });
            }
        });

        // entity action
        addTranslator(0x13, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                // client -> server
                return PacketUtil.createPacket(0x13, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                });
            }
        });

        // entity attach
        addTranslator(0x27, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                int vehicleEntityId = data.read(Type.INT, 1);
                session.getUserData().setVehicleEntityId(vehicleEntityId);

                return PacketUtil.createPacket(0x27, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        set(Type.BOOLEAN, false) // leash
                });
            }
        });

        // steer vehicle
        addTranslator(0x1B, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                boolean dismount = data.read(Type.BOOLEAN, 3);

                if (dismount) {
                    return PacketUtil.createPacket(0x07, new TypeHolder[] {
                        set(Type.INT, session.getUserData().getEntityId()),
                        set(Type.INT, session.getUserData().getVehicleEntityId()),
                        set(Type.BYTE, (byte) 0),
                    });
                }

                return new PacketData(-1); // packet doesn't exist in 1.5
            }
        });
    }
}
