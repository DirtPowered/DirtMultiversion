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

package com.github.dirtpowered.dirtmv.viaversion.providers;

import com.github.dirtpowered.dirtmv.api.DirtServer;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory.QuickBarTracker;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;

public class ViaHandItemProvider extends HandItemProvider {
    private final DirtServer api;

    public ViaHandItemProvider(DirtServer server) {
        this.api = server;
    }

    @Override
    public Item getHandItem(UserConnection userConnection) {
        if (userConnection.getProtocolInfo() == null) {
            return super.getHandItem(userConnection);
        }

        UserData userData = api.getUserDataFromUsername(userConnection.getProtocolInfo().getUsername());

        if (userData != null) {
            ProtocolStorage storage = userData.getProtocolStorage();
            if (storage.hasObject(QuickBarTracker.class)) {
                QuickBarTracker cache = storage.get(QuickBarTracker.class);

                ItemStack item = cache.getItemInHand();
                return new DataItem(item.getItemId(), (byte) item.getAmount(), (short) item.getData(), null);
            }
        }
        return super.getHandItem(userConnection);
    }
}
