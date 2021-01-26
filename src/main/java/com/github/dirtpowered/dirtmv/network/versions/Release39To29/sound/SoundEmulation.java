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

package com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound;

import com.github.dirtpowered.dirtmv.data.entity.EntityType;

public class SoundEmulation {
    private final static WorldSound[][] ENTITY_SOUNDS = new WorldSound[256][];
    private final static float[] VOL_ADJUST = new float[256];

    static {
        // pig sounds
        ENTITY_SOUNDS[90] = new WorldSound[]{
                WorldSound.MOB_PIG, // idle
                WorldSound.MOB_PIG, // hurt
                WorldSound.MOB_PIG_DEATH, // death
                WorldSound.NO_SOUND, // custom
        };

        // sheep sounds
        ENTITY_SOUNDS[91] = new WorldSound[]{
                WorldSound.MOB_SHEEP,
                WorldSound.MOB_SHEEP,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // cow sounds
        ENTITY_SOUNDS[92] = new WorldSound[]{
                WorldSound.MOB_COW,
                WorldSound.MOB_COW_HURT,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // chicken sounds
        ENTITY_SOUNDS[93] = new WorldSound[]{
                WorldSound.MOB_CHICKEN,
                WorldSound.MOB_CHICKEN_HURT,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // wolf sounds
        ENTITY_SOUNDS[95] = new WorldSound[]{
                WorldSound.MOB_WOLF,
                WorldSound.MOB_WOLF_HURT,
                WorldSound.MOB_WOLF_DEATH,
                WorldSound.NO_SOUND,
        };

        // mushroom cow sounds
        ENTITY_SOUNDS[96] = new WorldSound[]{
                WorldSound.MOB_COW,
                WorldSound.MOB_COW_HURT,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // ozelot
        ENTITY_SOUNDS[98] = new WorldSound[]{
                WorldSound.MOB_CAT,
                WorldSound.MOB_CAT_HURT,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // iron golem
        ENTITY_SOUNDS[99] = new WorldSound[]{
                WorldSound.NO_SOUND,
                WorldSound.MOB_IRON_GOLEM_HURT,
                WorldSound.MOB_IRON_GOLEM_DEATH,
                WorldSound.NO_SOUND,
        };

        // creeper sounds
        ENTITY_SOUNDS[50] = new WorldSound[]{
                WorldSound.MOB_CREEPER,
                WorldSound.RANDOM_FUSE,
                WorldSound.MOB_CREEPER_DEATH,
                WorldSound.NO_SOUND
        };

        // skeleton sounds
        ENTITY_SOUNDS[51] = new WorldSound[]{
                WorldSound.MOB_SKELETON,
                WorldSound.MOB_SKELETON_HURT,
                WorldSound.MOB_SKELETON_DEATH,
                WorldSound.NO_SOUND,
        };

        // spider sounds
        ENTITY_SOUNDS[52] = new WorldSound[]{
                WorldSound.MOB_SPIDER,
                WorldSound.MOB_SPIDER,
                WorldSound.MOB_SPIDER_DEATH,
                WorldSound.NO_SOUND,
        };

        // giant sounds
        ENTITY_SOUNDS[53] = new WorldSound[]{
                WorldSound.MOB_ZOMBIE,
                WorldSound.MOB_ZOMBIE_HURT,
                WorldSound.MOB_ZOMBIE_DEATH,
                WorldSound.NO_SOUND,
        };

        // zombie sounds
        ENTITY_SOUNDS[54] = new WorldSound[]{
                WorldSound.MOB_ZOMBIE,
                WorldSound.MOB_ZOMBIE_HURT,
                WorldSound.MOB_ZOMBIE_DEATH,
                WorldSound.NO_SOUND,
        };

        // slime sounds
        ENTITY_SOUNDS[55] = new WorldSound[]{
                WorldSound.MOB_SLIME,
                WorldSound.MOB_SLIME,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // ghast sounds
        ENTITY_SOUNDS[56] = new WorldSound[]{
                WorldSound.MOB_GHAST,
                WorldSound.MOB_GHAST_HURT,
                WorldSound.MOB_GHAST_DEATH,
                WorldSound.NO_SOUND,
        };

        // pig zombie sounds
        ENTITY_SOUNDS[57] = new WorldSound[]{
                WorldSound.MOB_PIG_ZOMBIE,
                WorldSound.MOB_PIG_ZOMBIE_HURT,
                WorldSound.MOB_PIG_ZOMBIE_DEATH,
                WorldSound.NO_SOUND,
        };

        // enderman sounds
        ENTITY_SOUNDS[58] = new WorldSound[]{
                WorldSound.MOB_ENDERMEN,
                WorldSound.MOB_ENDERMEN_HURT,
                WorldSound.MOB_ENDERMEN_DEATH,
                WorldSound.NO_SOUND,
        };

        // cave spider sounds
        ENTITY_SOUNDS[59] = new WorldSound[]{
                WorldSound.MOB_SPIDER,
                WorldSound.MOB_SPIDER,
                WorldSound.MOB_SPIDER_DEATH,
                WorldSound.NO_SOUND,
        };

        // silverfish sounds
        ENTITY_SOUNDS[60] = new WorldSound[]{
                WorldSound.MOB_SILVERFISH,
                WorldSound.MOB_SILVERFISH_HURT,
                WorldSound.MOB_SILVERFISH_DEATH,
                WorldSound.NO_SOUND
        };

        // blaze sounds
        ENTITY_SOUNDS[61] = new WorldSound[]{
                WorldSound.MOB_BLAZE,
                WorldSound.MOB_BLAZE_HURT,
                WorldSound.MOB_BLAZE_DEATH,
                WorldSound.NO_SOUND
        };

        // primed tnt sounds
        ENTITY_SOUNDS[20] = new WorldSound[]{
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
                WorldSound.RANDOM_FUSE
        };

        // human sounds
        ENTITY_SOUNDS[48] = new WorldSound[]{
                WorldSound.NO_SOUND,
                WorldSound.MOB_HUMAN_HURT,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND
        };

        // volume adjustments
        VOL_ADJUST[92] = 0.4F;
        VOL_ADJUST[96] = 0.4F;
        VOL_ADJUST[56] = 10.0F;
        VOL_ADJUST[98] = 0.4F;
        VOL_ADJUST[55] = 0.4F;
        VOL_ADJUST[95] = 0.4F;
    }

    public static Sound getEntitySound(SoundType soundType, EntityType entityType) {
        Sound sound = new Sound("", 0.0F, 1.0F);
        int entityTypeId = entityType.getEntityTypeId();

        WorldSound[] entitySounds = ENTITY_SOUNDS[entityTypeId];
        if (entitySounds == null) {
            return sound;
        }

        switch (soundType) {
            case IDLE:
                sound = new Sound(entitySounds[0].getSoundName(), 1.0F, 1.0F);
                break;
            case HURT:
                sound = new Sound(entitySounds[1].getSoundName(), 1.0F, 1.0F);
                break;
            case DEATH:
                sound = new Sound(entitySounds[2].getSoundName(), 1.0F, 1.0F);
                break;
            case CUSTOM:
                sound = new Sound(entitySounds[3].getSoundName(), 1.0F, 1.0F);
                break;
        }

        float correctedVolume = VOL_ADJUST[entityTypeId];

        if (correctedVolume != .0f) {
            sound.setVolume(correctedVolume);
        }

        return sound;
    }
}
