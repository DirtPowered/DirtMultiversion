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

package com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound;

import com.github.dirtpowered.dirtmv.data.entity.EntityType;

public class SoundEmulation {
    private final static WorldSound[][] ENTITY_SOUNDS = new WorldSound[256][];

    static {
        // pig sounds
        ENTITY_SOUNDS[90] = new WorldSound[]{
                WorldSound.MOB_PIG, // idle
                WorldSound.NO_SOUND, // hurt
                WorldSound.MOB_PIG_DEATH, // death
                WorldSound.NO_SOUND, // custom
        };

        // sheep sounds
        ENTITY_SOUNDS[91] = new WorldSound[]{
                WorldSound.MOB_SHEEP,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // cow sounds
        ENTITY_SOUNDS[92] = new WorldSound[]{
                WorldSound.MOB_COW,
                WorldSound.NO_SOUND,
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

        // ozelot
        ENTITY_SOUNDS[98] = new WorldSound[]{
                WorldSound.MOB_CAT,
                WorldSound.MOB_CAT_HURT,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // creeper sounds
        ENTITY_SOUNDS[50] = new WorldSound[]{
                WorldSound.MOB_CREEPER,
                WorldSound.NO_SOUND,
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
                WorldSound.NO_SOUND,
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
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
        };

        // ghast sounds
        ENTITY_SOUNDS[56] = new WorldSound[]{
                WorldSound.NO_SOUND,
                WorldSound.NO_SOUND,
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
                WorldSound.NO_SOUND,
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
    }

    public static String getEntitySound(SoundType soundType, EntityType entityType) {
        String soundName = "";

        WorldSound[] entitySounds = ENTITY_SOUNDS[entityType.getEntityTypeId()];
        if (entitySounds == null) {
            return soundName;
        }

        switch (soundType) {
            case IDLE:
                soundName = entitySounds[0].getSoundName();
                break;
            case HURT:
                soundName = entitySounds[1].getSoundName();
                break;
            case DEATH:
                soundName = entitySounds[2].getSoundName();
                break;
            case CUSTOM:
                soundName = entitySounds[3].getSoundName();
                break;
        }

        return soundName;
    }
}
