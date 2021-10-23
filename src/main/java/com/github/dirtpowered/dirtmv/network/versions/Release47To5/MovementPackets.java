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
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.entity.OnGroundTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.entity.PlayerMovementTracker;

public class MovementPackets extends ServerProtocol {

    MovementPackets() {
        super(MinecraftVersion.R1_8, MinecraftVersion.R1_7_6);
    }

    @Override
    public void registerTranslators() {
        // entity
        addTranslator(0x14, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {
            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x14, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0))
                });
            }
        });

        // entity relative move
        addTranslator(0x15, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                OnGroundTracker groundTracker = session.getUserData().getProtocolStorage().get(OnGroundTracker.class);
                int entityId = data.read(Type.INT, 0);

                double y = data.read(Type.BYTE, 2) / 32.0D;
                boolean onGround = !(y < 0.0D);

                groundTracker.setGroundStateFor(entityId, onGround);
                return PacketUtil.createPacket(0x15, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.BOOLEAN, onGround)
                });
            }
        });

        // entity look
        addTranslator(0x16, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                OnGroundTracker groundTracker = session.getUserData().getProtocolStorage().get(OnGroundTracker.class);
                int entityId = data.read(Type.INT, 0);

                boolean onGround = groundTracker.isOnGround(entityId);
                return PacketUtil.createPacket(0x16, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        set(Type.BOOLEAN, onGround)
                });
            }
        });

        // entity look move
        addTranslator(0x17, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                OnGroundTracker groundTracker = session.getUserData().getProtocolStorage().get(OnGroundTracker.class);
                int entityId = data.read(Type.INT, 0);

                double y = data.read(Type.BYTE, 2) / 32.0D;
                boolean onGround = !(y < 0.0D);

                groundTracker.setGroundStateFor(entityId, onGround);
                return PacketUtil.createPacket(0x17, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        set(Type.BOOLEAN, onGround)
                });
            }
        });

        // entity teleport
        addTranslator(0x18, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                OnGroundTracker groundTracker = storage.get(OnGroundTracker.class);
                int entityId = data.read(Type.INT, 0);
                boolean onGround = groundTracker.isOnGround(entityId);

                return PacketUtil.createPacket(0x18, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        set(Type.BOOLEAN, onGround)
                });
            }
        });

        // entity head look
        addTranslator(0x19, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x19, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        data.read(1),
                });
            }
        });

        // position and look
        addTranslator(0x08, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x08, new TypeHolder[]{
                        data.read(0),
                        set(Type.DOUBLE, data.read(Type.DOUBLE, 1) - 1.62D),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.BYTE, (byte) 0),
                });
            }
        });

        // player
        addTranslator(0x03, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage s = session.getStorage();

                if (s.hasObject(PlayerMovementTracker.class)) {
                    s.get(PlayerMovementTracker.class).setLastLocationUpdate(System.currentTimeMillis());
                }
                return PacketUtil.createPacket(0x03, new TypeHolder[]{
                        set(Type.BYTE, data.read(Type.UNSIGNED_BYTE, 0).byteValue())
                });
            }
        });

        // player position
        addTranslator(0x04, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage s = session.getStorage();

                if (s.hasObject(PlayerMovementTracker.class)) {
                    s.get(PlayerMovementTracker.class).setLastLocationUpdate(System.currentTimeMillis());
                }
                return PacketUtil.createPacket(0x04, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.DOUBLE, data.read(Type.DOUBLE, 1) + 1.62D), // stance
                        data.read(2),
                        set(Type.BYTE, data.read(Type.UNSIGNED_BYTE, 3).byteValue())
                });
            }
        });

        // player look
        addTranslator(0x05, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage s = session.getStorage();

                if (s.hasObject(PlayerMovementTracker.class)) {
                    s.get(PlayerMovementTracker.class).setLastLocationUpdate(System.currentTimeMillis());
                }
                return PacketUtil.createPacket(0x05, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.BYTE, data.read(Type.UNSIGNED_BYTE, 2).byteValue())
                });
            }
        });

        // player pos look
        addTranslator(0x06, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage s = session.getStorage();

                if (s.hasObject(PlayerMovementTracker.class)) {
                    s.get(PlayerMovementTracker.class).setLastLocationUpdate(System.currentTimeMillis());
                }
                return PacketUtil.createPacket(0x06, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.DOUBLE, data.read(Type.DOUBLE, 1) + 1.62D),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.BYTE, data.read(Type.UNSIGNED_BYTE, 5).byteValue())
                });
            }
        });
    }
}
