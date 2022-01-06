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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class InventoryUtils {

    private final static String[] ID_TO_STRING_TYPE = new String[12];

    static {
        ID_TO_STRING_TYPE[0] = "minecraft:container";
        ID_TO_STRING_TYPE[1] = "minecraft:crafting_table";
        ID_TO_STRING_TYPE[2] = "minecraft:furnace";
        ID_TO_STRING_TYPE[3] = "minecraft:dispenser";
        ID_TO_STRING_TYPE[4] = "minecraft:enchanting_table";
        ID_TO_STRING_TYPE[5] = "minecraft:brewing_stand";
        ID_TO_STRING_TYPE[6] = "minecraft:villager";
        ID_TO_STRING_TYPE[7] = "minecraft:beacon";
        ID_TO_STRING_TYPE[8] = "minecraft:anvil";
        ID_TO_STRING_TYPE[9] = "minecraft:hopper";
        ID_TO_STRING_TYPE[11] = "EntityHorse";
    }

    public static String getNamedTypeFromId(int typeId) {
        return ID_TO_STRING_TYPE[typeId];
    }

    public static String addTranslateComponent(String message) {
        JsonObject jsonElement = new JsonObject();
        jsonElement.add("translate", new JsonPrimitive(message));

        return jsonElement.toString();
    }

    public static boolean isNonStorageInventory(int inventoryType) {
        return inventoryType == 1 || inventoryType == 4 || inventoryType == 8;
    }
}
