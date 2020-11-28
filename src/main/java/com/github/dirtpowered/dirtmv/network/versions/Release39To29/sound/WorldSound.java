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

public enum WorldSound {
    RANDOM_FUSE("random.fuse"),
    RANDOM_EXPLODE("random.explode"),
    RANDOM_POP("random.pop"),
    RANDOM_BOW("random.bow"),
    MOB_ZOMBIE("mob.zombie"),
    MOB_HUMAN_HURT("damage.hurtflesh"),
    MOB_ZOMBIE_HURT("mob.zombiehurt"),
    MOB_ZOMBIE_DEATH("mob.zombiedeath"),
    MOB_PIG_ZOMBIE("mob.zombiepig.zpig"),
    MOB_PIG_ZOMBIE_HURT("mob.zombiepig.zpighurt"),
    MOB_PIG_ZOMBIE_DEATH("mob.zombiepig.zpigdeath"),
    MOB_SPIDER("mob.spider"),
    MOB_SPIDER_DEATH("mob.spiderdeath"),
    MOB_CREEPER("mob.creeper"),
    MOB_CREEPER_DEATH("mob.creeperdeath"),
    MOB_SKELETON("mob.skeleton"),
    MOB_SKELETON_HURT("mob.skeletonhurt"),
    MOB_SKELETON_DEATH("mob.skeletondeath"),
    MOB_ENDERMEN("mob.endermen.idle"),
    MOB_ENDERMEN_HURT("mob.endermen.hit"),
    MOB_ENDERMEN_DEATH("mob.endermen.death"),
    MOB_BLAZE("mob.blaze.breathe"),
    MOB_BLAZE_HURT("mob.blaze.hit"),
    MOB_BLAZE_DEATH("mob.blaze.death"),
    MOB_GHAST_DEATH("mob.ghast.death"),
    MOB_SILVERFISH("mob.silverfish.say"),
    MOB_SILVERFISH_HURT("mob.silverfish.hit"),
    MOB_SILVERFISH_DEATH("mob.silverfish.kill"),
    MOB_CAT("mob.cat.meow"),
    MOB_CAT_HURT("mob.cat.hitt"),
    MOB_WOLF("mob.wolf.bark"),
    MOB_WOLF_HURT("mob.wolf.hurt"),
    MOB_WOLF_DEATH("mob.wolf.death"),
    MOB_SHEEP("mob.sheep"),
    MOB_PIG("mob.pig"),
    MOB_PIG_DEATH("mob.pigdeath"),
    MOB_COW("mob.cow"),
    MOB_CHICKEN("mob.chicken"),
    MOB_CHICKEN_HURT("mob.chickenhurt"),
    MOB_SLIME("mob.slime"),
    NO_SOUND("");

    private final String soundName;

    WorldSound(String soundName) {
        this.soundName = soundName;
    }

    public String getSoundName() {
        return soundName;
    }
}
