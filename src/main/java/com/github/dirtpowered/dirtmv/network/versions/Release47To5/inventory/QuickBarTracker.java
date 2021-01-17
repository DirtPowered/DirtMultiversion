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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory;

import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class QuickBarTracker {
    private final Int2ObjectMap<ItemStack> quickBarItems = new Int2ObjectOpenHashMap<>();

    @Getter
    private int currentSlot;

    /*
     * Stolen from GlowPlayerInventory
     * https://github.com/GlowstoneMC/Glowstone (28965ce7e1774df7ed7f91ddd5e2d9a885389089)
     */
    private final static int[] slotConversion = {
            36, 37, 38, 39, 40, 41, 42, 43, 44,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            5, 6, 7, 8, 1, 2, 3, 4, 0
    };

    @Setter
    @Getter
    private ItemStack itemOnCursor;

    public static int networkSlotToInventory(int slot) {
        for (int i = 0; i < 45; ++i) {
            if (slot == slotConversion[i]) return i;
        }
        return -1;
    }

    public void setItem(int slot, ItemStack itemStack) {
        quickBarItems.put(slot, itemStack);
    }

    public ItemStack getItem(int slot) {
        return quickBarItems.getOrDefault(slot, null);
    }

    public ItemStack getItemInHand() {
        if (quickBarItems.get(currentSlot) == null) {
            return new ItemStack(0, 0, 0, null);
        }
        return quickBarItems.get(currentSlot);
    }

    public void setCurrentHotBarSlot(int currentSlot) {
        this.currentSlot = 36 + currentSlot;
    }
}
