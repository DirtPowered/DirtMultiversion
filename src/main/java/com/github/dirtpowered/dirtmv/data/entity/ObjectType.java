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

package com.github.dirtpowered.dirtmv.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ObjectType implements SpawnableObject {
    BOAT(1),
    ITEM(2),
    MINECART(10),
    FISH_HOOK(90),
    TNT_PRIMED(50),
    ENDER_CRYSTAL(51),
    ARROW(60),
    SNOWBALL(61),
    EGG(62),
    FIREBALL(63),
    SMALL_FIREBALL(64),
    ENDER_PEARL(65),
    WITHER_SKULL(66),
    FALLING_OBJECT(70),
    ITEM_FRAME(71),
    ENDER_EYE(72),
    POTION(73),
    EXP_BOTTLE(75),
    FIREWORK_ROCKET(76),
    LEASH(77);

    @Getter
    private final int objectTypeId;

    public static ObjectType fromObjectTypeId(int objectTypeId) {
        for (ObjectType objectType : ObjectType.values()) {
            if (objectType.objectTypeId == objectTypeId) {
                return objectType;
            }
        }

        throw new IllegalArgumentException("missing mapping for object type: " + objectTypeId);
    }

    @Override
    public boolean isLivingEntity() {
        return false;
    }
}
