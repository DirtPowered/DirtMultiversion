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

package com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityIdToTypeString {
    private final static Map<Integer, String> ENTITY_LIST = new HashMap<>();

    static {
        ENTITY_LIST.put(1, "Item");
        ENTITY_LIST.put(2, "XPOrb");
        ENTITY_LIST.put(200, "EnderCrystal");
        ENTITY_LIST.put(9, "Painting");
        ENTITY_LIST.put(10, "Arrow");
        ENTITY_LIST.put(11, "Snowball");
        ENTITY_LIST.put(12, "Fireball");
        ENTITY_LIST.put(13, "SmallFireball");
        ENTITY_LIST.put(14, "ThrownEnderpearl");
        ENTITY_LIST.put(15, "EyeOfEnderSignal");
        ENTITY_LIST.put(16, "ThrownPotion");
        ENTITY_LIST.put(17, "ThrownExpBottle");
        ENTITY_LIST.put(20, "PrimedTnt");
        ENTITY_LIST.put(21, "FallingSand");
        ENTITY_LIST.put(90, "Pig");
        ENTITY_LIST.put(91, "Sheep");
        ENTITY_LIST.put(92, "Cow");
        ENTITY_LIST.put(93, "Chicken");
        ENTITY_LIST.put(94, "Squid");
        ENTITY_LIST.put(95, "Wolf");
        ENTITY_LIST.put(96, "MushroomCow");
        ENTITY_LIST.put(97, "SnowMan");
        ENTITY_LIST.put(98, "Ozelot");
        ENTITY_LIST.put(99, "VillagerGolem");
        ENTITY_LIST.put(40, "Minecart");
        ENTITY_LIST.put(41, "Boat");
        ENTITY_LIST.put(48, "Mob");
        ENTITY_LIST.put(49, "Monster");
        ENTITY_LIST.put(50, "Creeper");
        ENTITY_LIST.put(51, "Skeleton");
        ENTITY_LIST.put(52, "Spider");
        ENTITY_LIST.put(53, "Giant");
        ENTITY_LIST.put(54, "Zombie");
        ENTITY_LIST.put(55, "Slime");
        ENTITY_LIST.put(56, "Ghast");
        ENTITY_LIST.put(120, "Villager");
        ENTITY_LIST.put(57, "PigZombie");
        ENTITY_LIST.put(58, "Enderman");
        ENTITY_LIST.put(59, "CaveSpider");
        ENTITY_LIST.put(60, "Silverfish");
        ENTITY_LIST.put(61, "Blaze");
        ENTITY_LIST.put(62, "LavaSlime");
        ENTITY_LIST.put(63, "EnderDragon");
    }

    public static String getEntityTypeString(int entityTypeId) {
        return ENTITY_LIST.getOrDefault(entityTypeId, "Pig");
    }
}
