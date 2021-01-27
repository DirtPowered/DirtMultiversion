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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.transformers.block.ItemBlockDataTransformer;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory.InventoryUtils;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory.QuickBarTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory.WindowTypeTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.item.CreativeItemList;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.item.ItemRemapper;

public class InventoryPackets extends ServerProtocol {
    static ItemBlockDataTransformer itemRemapper;

    InventoryPackets() {
        super(MinecraftVersion.R1_8, MinecraftVersion.R1_7_6);

        itemRemapper = new ItemRemapper();
    }

    /*
     * NOTE: quick bar slots are static, so we don't have to check inventory type
     */
    private void updateLocalInventory(QuickBarTracker cache, int slot, boolean shiftClick) {
        int slotIndex = QuickBarTracker.networkSlotToInventory(slot);
        ItemStack currentItem = cache.getItem(slotIndex);

        if (shiftClick) {
            if (slotIndex < 9) {
                for (int i = 9; i < 36; ++i) {
                    if (cache.getItem(i) == null) {
                        cache.setItem(i, currentItem);
                        cache.setItem(slotIndex, null);
                        return;
                    }
                }
            } else {
                for (int i = 0; i < 9; ++i) {
                    if (cache.getItem(i) == null) {
                        cache.setItem(i, currentItem);
                        cache.setItem(slotIndex, null);
                        return;
                    }
                }
            }
            return;
        }

        cache.setItem(slotIndex, cache.getItemOnCursor());
        cache.setItemOnCursor(currentItem);
    }

    @Override
    public void registerTranslators() {
        // set slot
        addTranslator(0x2F, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                short windowId = data.read(Type.UNSIGNED_BYTE, 0);
                short slot = data.read(Type.SHORT, 1);

                int windowType = 0;

                if (storage.hasObject(WindowTypeTracker.class)) {
                    WindowTypeTracker windowTracker = storage.get(WindowTypeTracker.class);
                    windowType = windowTracker.getWindowType(windowId);
                }

                ItemStack originalItem = data.read(Type.V1_3R_ITEM, 2);

                if (originalItem == null)
                    return new PacketData(0x2F, data.getObjects());

                ItemStack itemStack = itemRemapper.replaceItem(originalItem);

                if (windowType == 4) {
                    if (slot >= 1) {
                        slot++;
                    }
                }

                // quick-bar cache
                if (storage.hasObject(QuickBarTracker.class)) {
                    QuickBarTracker quickBarTracker = storage.get(QuickBarTracker.class);
                    quickBarTracker.setItem(slot, itemStack);
                }

                return PacketUtil.createPacket(0x2F, new TypeHolder[]{
                        data.read(0),
                        set(Type.SHORT, slot),
                        set(Type.V1_8R_ITEM, itemStack)
                });
            }
        });

        // window items
        addTranslator(0x30, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                short windowId = data.read(Type.UNSIGNED_BYTE, 0);

                ItemStack[] itemArray = data.read(Type.V1_3R_ITEM_ARRAY, 1);

                int windowType = 0;

                if (storage.hasObject(WindowTypeTracker.class)) {
                    WindowTypeTracker windowTracker = storage.get(WindowTypeTracker.class);
                    windowType = windowTracker.getWindowType(windowId);
                }

                if (windowType == 4) {
                    ItemStack[] copy = itemArray;

                    itemArray = new ItemStack[copy.length + 1];
                    itemArray[0] = copy[0];

                    int slot = 0;

                    while (slot < copy.length - 1) {
                        itemArray[slot + 2] = copy[slot + 1];
                        slot++;
                    }
                    // lapis
                    itemArray[1] = new ItemStack(351, 3, 4, null);
                }

                for (int i = 0; i < itemArray.length; i++) {
                    ItemStack originalItem = itemArray[i];
                    ItemStack item = originalItem;

                    if (originalItem != null) {
                        item = itemRemapper.replaceItem(originalItem);
                    }

                    itemArray[i] = item;

                    // quick-bar cache
                    if (storage.hasObject(QuickBarTracker.class)) {
                        QuickBarTracker quickBarTracker = storage.get(QuickBarTracker.class);
                        quickBarTracker.setItem(i, item);
                    }
                }

                return PacketUtil.createPacket(0x30, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_8R_ITEM_ARRAY, itemArray)
                });
            }
        });

        // open window
        addTranslator(0x2D, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                short windowId = data.read(Type.UNSIGNED_BYTE, 0);
                short type = data.read(Type.UNSIGNED_BYTE, 1);
                String title = data.read(Type.V1_7_STRING, 2);

                short slots = data.read(Type.UNSIGNED_BYTE, 3);
                // non storage inventories have always 0 slots
                slots = InventoryUtils.isNonStorageInventory(type) ? 0 : slots;

                // cache window type (for enchanting tables)
                if (storage.hasObject(WindowTypeTracker.class)) {
                    WindowTypeTracker windowTracker = storage.get(WindowTypeTracker.class);
                    windowTracker.setWindowTypeFor(windowId, type);
                }
                return PacketUtil.createPacket(0x2D, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_7_STRING, InventoryUtils.getNamedTypeFromId(type)),
                        set(Type.V1_7_STRING, InventoryUtils.addTranslateComponent(title)),
                        set(Type.UNSIGNED_BYTE, slots)
                });
            }
        });

        // window click
        addTranslator(0x0E, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                byte windowId = data.read(Type.BYTE, 0);
                short slot = data.read(Type.SHORT, 1);

                int windowType = 0;

                if (storage.hasObject(WindowTypeTracker.class)) {
                    WindowTypeTracker windowTracker = storage.get(WindowTypeTracker.class);
                    windowType = windowTracker.getWindowType(windowId);
                }

                if (windowType == 4) {
                    if (slot > 1) {
                        slot--;
                    }
                }

                if (storage.hasObject(QuickBarTracker.class)) {
                    QuickBarTracker cache = storage.get(QuickBarTracker.class);
                    boolean shiftClick = data.read(Type.BYTE, 4) == 1;

                    updateLocalInventory(cache, slot, shiftClick);
                }
                return PacketUtil.createPacket(0x0E, new TypeHolder[]{
                        data.read(0),
                        set(Type.SHORT, slot),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.V1_3R_ITEM, data.read(Type.V1_8R_ITEM, 5))
                });
            }
        });

        // creative item get
        addTranslator(0x10, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack item = data.read(Type.V1_8R_ITEM, 1);

                boolean notNull = item != null;

                if (notNull && !CreativeItemList.exists(item.getItemId())) {
                    // replace all unknown items to stone
                    item.setItemId(1);
                    item.setData(0);
                }

                return PacketUtil.createPacket(0x10, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_3R_ITEM, item)
                });
            }
        });

        // change held slot
        addTranslator(0x09, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();

                if (storage.hasObject(QuickBarTracker.class)) {
                    QuickBarTracker quickBarTracker = storage.get(QuickBarTracker.class);
                    quickBarTracker.setCurrentHotBarSlot(data.read(Type.SHORT, 0));
                }

                return data;
            }
        });

        // change held slot (server-side)
        addTranslator(0x09, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();

                if (storage.hasObject(QuickBarTracker.class)) {
                    QuickBarTracker quickBarTracker = storage.get(QuickBarTracker.class);
                    quickBarTracker.setCurrentHotBarSlot(data.read(Type.BYTE, 0));
                }
                return data;
            }
        });

        // window property
        addTranslator(0x31, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int property = data.read(Type.SHORT, 1);

                switch (property) {
                    case 0:
                        property = 2;
                        PacketData windowProperty = PacketUtil.createPacket(0x31, new TypeHolder[]{
                                data.read(0),
                                set(Type.SHORT, (short) 3),
                                set(Type.SHORT, (short) 200)
                        });
                        session.sendPacket(windowProperty, PacketDirection.TO_CLIENT, getFrom());
                        break;
                    case 1:
                        property = 0;
                        break;
                    case 2:
                        property = 1;
                        break;
                }

                return PacketUtil.createPacket(0x31, new TypeHolder[]{
                        data.read(0),
                        set(Type.SHORT, property),
                        data.read(2)
                });
            }
        });
    }
}
