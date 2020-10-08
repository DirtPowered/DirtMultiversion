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
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.ItemStack;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.utils.item.LegacyItemList;
import com.mojang.nbt.CompoundTag;

import java.util.List;

public class ProtocolRelease22To17 extends ServerProtocol {

    public ProtocolRelease22To17() {
        super(MinecraftVersion.R1_0, MinecraftVersion.B_1_8_1);
    }

    @Override
    public void registerTranslators() {

        addTranslator(0x01 /* LOGIN */, new PacketTranslator() {
            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (dir == PacketDirection.CLIENT_TO_SERVER) {
                    return PacketUtil.createPacket(MinecraftVersion.B_1_8_1, 0x01, new TypeHolder[]{
                            new TypeHolder(Type.INT, 17), // protocol version
                            data.read(1),
                            data.read(2),
                            data.read(3),
                            data.read(4),
                            data.read(5),
                            data.read(6),
                            data.read(7),
                    });
                }

                return data;
            }
        });

        addTranslator(0x0F /* BLOCK PLACE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(MinecraftVersion.B_1_8_1, 0x0F, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        new TypeHolder(Type.V1_7B_ITEM, data.read(4).getObject())
                });
            }
        });

        addTranslator(0x66 /* WINDOW CLICK */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(MinecraftVersion.B_1_8_1, 0x66, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        new TypeHolder(Type.V1_7B_ITEM, data.read(5).getObject())
                });
            }
        });

        addTranslator(0x6B /* CREATIVE SET SLOT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(MinecraftVersion.B_1_8_1, 0x6B, new TypeHolder[]{
                        data.read(0),
                        new TypeHolder(Type.V1_0R_ITEM, data.read(1).getObject())
                });
            }
        });

        addTranslator(0x2B /* SET EXPERIENCE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                float exp = 0.0F; // TODO convert progress

                short level = ((Byte) data.read(1).getObject()).shortValue();
                short totalExperience = ((Byte) data.read(0).getObject()).shortValue();

                return PacketUtil.createPacket(MinecraftVersion.R1_0, 0x2B, new TypeHolder[]{
                        new TypeHolder(Type.FLOAT, exp),
                        new TypeHolder(Type.SHORT, level),
                        new TypeHolder(Type.SHORT, totalExperience)
                });
            }
        });

        addTranslator(0x67 /* SET SLOT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                ItemStack item = (ItemStack) data.read(2).getObject();

                if (item != null && LegacyItemList.isEnchantable(item.getItemId()))
                    item.setCompoundTag(new CompoundTag("tag"));

                return PacketUtil.createPacket(MinecraftVersion.R1_0, 0x67, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        new TypeHolder(Type.V1_0R_ITEM, item)
                });
            }
        });

        addTranslator(0x68, /* WINDOW ITEMS */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                ItemStack[] items = (ItemStack[]) data.read(1).getObject();

                for (ItemStack item : items) {
                    if (item != null && LegacyItemList.isEnchantable(item.getItemId())) {
                        item.setCompoundTag(new CompoundTag("tag"));
                    }
                }

                return PacketUtil.createPacket(MinecraftVersion.R1_0, 0x68, new TypeHolder[]{
                        data.read(0),
                        new TypeHolder(Type.V1_0R_ITEM_ARRAY, items)
                });
            }
        });

        addTranslator(0x18 /* MOB_SPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                List<WatchableObject> watchableObjects = (List<WatchableObject>) data.read(7).getObject();

                return PacketUtil.createPacket(MinecraftVersion.R1_0, 0x18, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        new TypeHolder(Type.V1_0_METADATA, watchableObjects)
                });
            }
        });

        addTranslator(0x28, /* METADATA */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                List<WatchableObject> watchableObjects = (List<WatchableObject>) data.read(0).getObject();

                return PacketUtil.createPacket(MinecraftVersion.R1_0, 0x28, new TypeHolder[] {
                        new TypeHolder(Type.V1_0_METADATA, watchableObjects)
                });
            }
        });
    }
}
