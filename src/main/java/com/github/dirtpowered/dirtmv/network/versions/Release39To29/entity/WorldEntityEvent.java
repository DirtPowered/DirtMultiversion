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

package com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Location;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.model.AbstractEntity;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.Sound;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.SoundEmulation;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.SoundType;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound.WorldSound;
import com.google.common.primitives.Shorts;

import java.util.Random;

public class WorldEntityEvent {

    public static void onDamage(ServerSession session, int entityId) {
        playSound(session, entityId, SoundType.HURT);
    }

    public static void onDeath(ServerSession session, int entityId) {
        playSound(session, entityId, SoundType.DEATH);
    }

    public static void onUpdate(ServerSession session, int entityId) {
        playSound(session, entityId, SoundType.IDLE);
    }

    public static void onCustomAction(ServerSession session, int entityId) {
        playSound(session, entityId, SoundType.CUSTOM);
    }

    private static void playSound(ServerSession session, int entityId, SoundType type) {
        ProtocolStorage storage = session.getStorage();
        if (!storage.hasObject(EntityTracker.class)) {
            return;
        }

        EntityTracker tracker = storage.get(EntityTracker.class);

        if (tracker.isEntityTracked(entityId)) {
            AbstractEntity e = tracker.getEntity(entityId);

            Sound sound = SoundEmulation.getEntitySound(type, e.getEntityType());
            if (sound.getSoundName().isEmpty())
                return;

            Location loc = e.getLocation();
            Random shared = session.getMain().getSharedRandom();

            sound.setPitch((shared.nextFloat() - shared.nextFloat()) * 0.2F + 1.0F);

            if (tracker.isEntityTracked(-999)) {
                Location l = tracker.getEntity(-999).getLocation();

                playSoundAt(session, loc, l, sound);
            }
        }
    }

    public static void playSoundAt(ServerSession session, Location loc, WorldSound sound) {
        EntityTracker tracker = session.getStorage().get(EntityTracker.class);

        if (tracker.isEntityTracked(-999)) {
            Location l = tracker.getEntity(-999).getLocation();

            playSoundAt(session, loc, l, new Sound(sound.getSoundName(), 0.75F, 1.0F));
        }
    }

    public static void playSoundAt(ServerSession session, Location loc, WorldSound sound, float vol, float pitch) {
        EntityTracker tracker = session.getStorage().get(EntityTracker.class);

        if (tracker.isEntityTracked(-999)) {
            Location l = tracker.getEntity(-999).getLocation();

            playSoundAt(session, loc, l, new Sound(sound.getSoundName(), vol, pitch));
        }
    }

    private static void playSoundAt(ServerSession session, Location loc, Location target, Sound sound) {
        short correctedPitch = Shorts.constrainToRange((short) (sound.getPitch() * 63.0F), (short) 0, (short) 255);

        float vol = sound.getVolume();
        float range = 16F;

        if (vol > 1.0F) {
            range *= vol;
        }

        // cancel if outside allowed range
        if (target.distanceTo(loc) > range) {
            return;
        }

        PacketData namedSound = PacketUtil.createPacket(0x3E, new TypeHolder[]{
                new TypeHolder<>(Type.STRING, sound.getSoundName()),
                new TypeHolder<>(Type.INT, ((int) loc.getX()) * 8),
                new TypeHolder<>(Type.INT, ((int) loc.getY()) * 8),
                new TypeHolder<>(Type.INT, ((int) loc.getZ()) * 8),
                new TypeHolder<>(Type.FLOAT, vol),
                new TypeHolder<>(Type.UNSIGNED_BYTE, correctedPitch),
        });

        session.sendPacket(namedSound, PacketDirection.TO_CLIENT, MinecraftVersion.R1_3_1);
    }
}
