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

package com.github.dirtpowered.dirtmv.network.versions.Release51To39.sound;

import com.github.dirtpowered.dirtmv.data.sound.SoundRemapper;

public class SoundMappings extends SoundRemapper {

    @Override
    public void initialize() {
        remap("damage.hurtflesh", "damage.hit");
        remap("mob.chicken", "mob.chicken.say");
        remap("mob.chickenhurt", "mob.chicken.hurt");
        remap("mob.chickenplop", "mob.chicken.plop");
        remap("mob.cow", "mob.cow.say");
        remap("mob.cowhurt", "mob.cow.hurt");
        remap("mob.creeper", "mob.creeper.say");
        remap("mob.creeperdeath", "mob.creeper.death");
        remap("mob.ghast.affectionate scream", "mob.ghast.affectionate_scream");
        remap("mob.pigdeath", "mob.pig.death");
        remap("mob.sheep", "mob.sheep.say");
        remap("mob.skeleton", "mob.skeleton.say");
        remap("mob.skeletondeath", "mob.skeleton.death");
        remap("mob.skeletonhurt", "mob.skeleton.hurt");
        remap("mob.slime", "mob.slime.small");
        remap("mob.slimeattack", "mob.slime.attack");
        remap("mob.spider", "mob.spider.say");
        remap("mob.spiderdeath", "mob.spider.death");
        remap("mob.zombie", "mob.zombie.say");
        remap("mob.zombie.wood", "mob.zombie.wood");
        remap("mob.zombiedeath", "mob.zombie.death");
        remap("mob.zombiehurt", "mob.zombie.hurt");
        remap("random.chestclosed", "random.chestclosed");
        remap("random.chestopen", "random.chestopen");
        remap("random.click", "random.click");
        remap("random.door_close", "random.door_close");
        remap("random.door_open", "random.door_open");
        remap("random.drr", "");
        remap("random.hurt", "damage.hit");
        remap("random.old_explode", "random.explode");
    }
}
