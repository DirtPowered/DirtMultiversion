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
        remap("ambient.cave.cave", "ambient.cave.cave");
        remap("ambient.weather.rain", "ambient.weather.rain");
        remap("ambient.weather.thunder", "ambient.weather.thunder");
        remap("damage.fallbig", "damage.fallbig");
        remap("damage.fallsmall", "damage.fallsmall");
        remap("damage.hurtflesh", "damage.hit");
        remap("fire.fire", "fire.fire");
        remap("fire.ignite", "fire.ignite");
        remap("liquid.lava", "liquid.lava");
        remap("liquid.lavapop", "liquid.lavapop");
        remap("liquid.splash", "liquid.splash");
        remap("liquid.water", "liquid.water");
        remap("mob.blaze.breathe", "mob.blaze.breathe");
        remap("mob.blaze.death", "mob.blaze.death");
        remap("mob.blaze.hit", "mob.blaze.hit");
        remap("mob.cat.hiss", "mob.cat.hiss");
        remap("mob.cat.hitt", "mob.cat.hitt");
        remap("mob.cat.meow", "mob.cat.meow");
        remap("mob.cat.purr", "mob.cat.purr");
        remap("mob.cat.purreow", "mob.cat.purreow");
        remap("mob.chicken", "mob.chicken.say");
        remap("mob.chickenhurt", "mob.chicken.hurt");
        remap("mob.chickenplop", "mob.chicken.plop");
        remap("mob.cow", "mob.cow.say");
        remap("mob.cowhurt", "mob.cow.hurt");
        remap("mob.creeper", "mob.creeper.say");
        remap("mob.creeperdeath", "mob.creeper.death");
        remap("mob.endermen.death", "mob.endermen.death");
        remap("mob.endermen.hit", "mob.endermen.hit");
        remap("mob.endermen.idle", "mob.endermen.idle");
        remap("mob.endermen.portal", "mob.endermen.portal");
        remap("mob.endermen.scream", "mob.endermen.scream");
        remap("mob.endermen.stare", "mob.endermen.stare");
        remap("mob.ghast.affectionate scream", "mob.ghast.affectionate_scream");
        remap("mob.ghast.charge", "mob.ghast.charge");
        remap("mob.ghast.death", "mob.ghast.death");
        remap("mob.ghast.fireball", "mob.ghast.fireball");
        remap("mob.ghast.moan", "mob.ghast.moan");
        remap("mob.ghast.scream", "mob.ghast.scream");
        remap("mob.irongolem.death", "mob.irongolem.death");
        remap("mob.irongolem.hit", "mob.irongolem.hit");
        remap("mob.irongolem.throw", "mob.irongolem.throw");
        remap("mob.irongolem.walk", "mob.irongolem.walk");
        remap("mob.magmacube.big", "mob.magmacube.big");
        remap("mob.magmacube.jump", "mob.magmacube.jump");
        remap("mob.magmacube.small", "mob.magmacube.small");
        remap("mob.pig", "mob.pig.say");
        remap("mob.pigdeath", "mob.pig.death");
        remap("mob.sheep", "mob.sheep.say");
        remap("mob.silverfish.hit", "mob.silverfish.hit");
        remap("mob.silverfish.kill", "mob.silverfish.kill");
        remap("mob.silverfish.say", "mob.silverfish.say");
        remap("mob.silverfish.step", "mob.silverfish.step");
        remap("mob.skeleton", "mob.skeleton.say");
        remap("mob.skeletondeath", "mob.skeleton.death");
        remap("mob.skeletonhurt", "mob.skeleton.hurt");
        remap("mob.slime", "mob.slime.small");
        remap("mob.slimeattack", "mob.slime.attack");
        remap("mob.spider", "mob.spider.say");
        remap("mob.spiderdeath", "mob.spider.death");
        remap("mob.wolf.bark", "mob.wolf.bark");
        remap("mob.wolf.death", "mob.wolf.death");
        remap("mob.wolf.growl", "mob.wolf.growl");
        remap("mob.wolf.howl", "mob.wolf.howl");
        remap("mob.wolf.hurt", "mob.wolf.hurt");
        remap("mob.wolf.panting", "mob.wolf.panting");
        remap("mob.wolf.shake", "mob.wolf.shake");
        remap("mob.wolf.whine", "mob.wolf.whine");
        remap("mob.zombie.metal", "mob.zombie.metal");
        remap("mob.zombie", "mob.zombie.say");
        remap("mob.zombie.wood", "mob.zombie.wood");
        remap("mob.zombie.woodbreak", "mob.zombie.woodbreak");
        remap("mob.zombiedeath", "mob.zombie.death");
        remap("mob.zombiehurt", "mob.zombie.hurt");
        remap("mob.zombiepig.zpig", "mob.zombiepig.zpig");
        remap("mob.zombiepig.zpigangry", "mob.zombiepig.zpigangry");
        remap("mob.zombiepig.zpigdeath", "mob.zombiepig.zpigdeath");
        remap("mob.zombiepig.zpighurt", "mob.zombiepig.zpighurt");
        remap("mob.villager.default", "mob.villager.default");
        remap("mob.villager.defaulthurt", "mob.villager.defaulthurt");
        remap("mob.villager.defaultdeath", "mob.villager.defaultdeath");
        remap("note.bass", "note.bass");
        remap("note.bassattack", "note.bassattack");
        remap("note.bd", "note.bd");
        remap("note.harp", "note.harp");
        remap("note.hat", "note.hat");
        remap("note.pling", "note.pling");
        remap("note.snare", "note.snare");
        remap("portal.portal", "portal.portal");
        remap("portal.travel", "portal.travel");
        remap("portal.trigger", "portal.trigger");
        remap("random.bow", "random.bow");
        remap("random.bowhit", "random.bowhit");
        remap("random.break", "random.break");
        remap("random.breath", "random.breath");
        remap("random.burp", "random.burp");
        remap("random.chestclosed", "random.chestclosed");
        remap("random.chestopen", "random.chestopen");
        remap("random.click", "random.click");
        remap("random.door_close", "random.door_close");
        remap("random.door_open", "random.door_open");
        remap("random.drink", "random.drink");
        remap("random.drr", "");
        remap("random.eat", "random.eat");
        remap("random.explode", "random.explode");
        remap("random.fizz", "random.fizz");
        remap("random.fuse", "random.fuse");
        remap("random.glass", "random.glass");
        remap("random.hurt", "damage.hit");
        remap("random.levelup", "random.levelup");
        remap("random.old_explode", "random.explode");
        remap("random.orb", "random.orb");
        remap("random.pop", "random.pop");
        remap("random.splash", "random.splash");
        remap("random.wood click", "random.wood_click");
        remap("step.cloth", "step.cloth");
        remap("step.grass", "step.grass");
        remap("step.gravel", "step.gravel");
        remap("step.sand", "step.sand");
        remap("step.snow", "step.snow");
        remap("step.stone", "step.stone");
        remap("step.wood", "step.wood");
        remap("tile.piston.in", "tile.piston.in");
        remap("tile.piston.out", "tile.piston.out");
    }
}
