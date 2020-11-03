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

package com.github.dirtpowered.dirtmv.network.versions.Release22To17;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release22To17.item.LegacyItemList;
import com.mojang.nbt.CompoundTag;

public class ProtocolRelease22To17 extends ServerProtocol {

    public ProtocolRelease22To17() {
        super(MinecraftVersion.R1_0, MinecraftVersion.B1_8_1);
    }

    @Override
    public void registerTranslators() {

        // login
        addTranslator(0x01, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        set(Type.INT, 17), // protocol version
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        data.read(7),
                });
            }
        });

        // block place
        addTranslator(0x0F, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x0F, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.V1_3B_ITEM, data.read(Type.V1_0R_ITEM, 4))
                });
            }
        });

        // window click
        addTranslator(0x66, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x66, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.V1_3B_ITEM, data.read(Type.V1_0R_ITEM, 5))
                });
            }
        });

        // creative set slot
        addTranslator(0x6B, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x6B, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_0R_ITEM, data.read(Type.V1_8B_ITEM, 1))
                });
            }
        });

        // set experience
        addTranslator(0x2B, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                float exp = 0.0F; // TODO convert progress

                short level = data.read(Type.BYTE, 1).shortValue();
                short totalExperience = data.read(Type.BYTE, 0).shortValue();

                return PacketUtil.createPacket(0x2B, new TypeHolder[]{
                        set(Type.FLOAT, exp),
                        set(Type.SHORT, level),
                        set(Type.SHORT, totalExperience)
                });
            }
        });

        // set slot
        addTranslator(0x67, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack item = data.read(Type.V1_3B_ITEM, 2);

                if (item != null && LegacyItemList.isEnchantable(item.getItemId()))
                    item.setCompoundTag(new CompoundTag("tag"));

                return PacketUtil.createPacket(0x67, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_0R_ITEM, item)
                });
            }
        });

        // window items
        addTranslator(0x68, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack[] items = data.read(Type.V1_3B_ITEM_ARRAY, 1);

                for (ItemStack item : items) {
                    if (item != null && LegacyItemList.isEnchantable(item.getItemId())) {
                        item.setCompoundTag(new CompoundTag("tag"));
                    }
                }

                return PacketUtil.createPacket(0x68, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_0R_ITEM_ARRAY, items)
                });
            }
        });
    }
}
