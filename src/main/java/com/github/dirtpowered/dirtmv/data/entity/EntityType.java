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

package com.github.dirtpowered.dirtmv.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EntityType {
    CREEPER(50, true),
    SKELETON(51, true),
    SPIDER(52, true),
    GIANT(53, true),
    ZOMBIE(54, true),
    SLIME(55, true),
    GHAST(56, true),
    PIG_ZOMBIE(57, true),
    ENDER_MAN(58, true),
    CAVE_SPIDER(59, true),
    SILVERFISH(60, true),
    BLAZE(61, true),
    MAGMA_CUBE(62, true),
    ENDER_DRAGON(63, true),
    WITHER(64, true),
    BAT(65, true),
    WITCH(66, true),
    PIG(90, true),
    SHEEP(91, true),
    COW(92, true),
    CHICKEN(93, true),
    SQUID(94, true),
    WOLF(95, true),
    MUSHROOM_COW(96, true),
    SNOWMAN(97, true),
    OZELOT(98, true),
    IRON_GOLEM(99, true),
    HORSE(100, true),
    VILLAGER(120, true),
    HUMAN(-1, true),
    ITEM(-2, false);

    @Getter
    private int entityTypeId;

    @Getter
    private boolean livingEntity;

    public static EntityType fromEntityTypeId(int entityTypeId) {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.entityTypeId == entityTypeId) {
                return entityType;
            }
        }

        return null;
    }
}
