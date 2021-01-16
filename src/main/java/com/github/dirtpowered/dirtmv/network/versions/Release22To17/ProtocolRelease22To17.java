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

package com.github.dirtpowered.dirtmv.network.versions.Release22To17;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Location;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage.BlockStorage;
import com.github.dirtpowered.dirtmv.network.versions.Release22To17.item.LegacyItemList;
import com.github.dirtpowered.dirtmv.network.versions.Release22To17.movement.MovementTranslator;

public class ProtocolRelease22To17 extends ServerProtocol {

    public ProtocolRelease22To17() {
        super(MinecraftVersion.R1_0, MinecraftVersion.B1_8_1);
    }

    @Override
    public void registerTranslators() {

        // login
        addTranslator(0x01, PacketDirection.TO_SERVER, new PacketTranslator() {

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
        addTranslator(0x0F, PacketDirection.TO_SERVER, new PacketTranslator() {

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
        addTranslator(0x66, PacketDirection.TO_SERVER, new PacketTranslator() {

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
        addTranslator(0x6B, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack item = data.read(Type.V1_0R_ITEM, 1);

                boolean notNull = item != null;

                if (notNull && !LegacyItemList.exists(item.getItemId())) {
                    // replace all unknown items to stone
                    item.setItemId(1);
                    item.setData(0);
                }

                return PacketUtil.createPacket(0x6B, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_8B_ITEM, item)
                });
            }
        });

        // set experience
        addTranslator(0x2B, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                float exp = data.read(Type.BYTE, 0).floatValue();
                short level = data.read(Type.BYTE, 1).shortValue();

                exp = (exp - 1) / (10 * level);

                return PacketUtil.createPacket(0x2B, new TypeHolder[]{
                        set(Type.FLOAT, exp),
                        set(Type.SHORT, level),
                        set(Type.SHORT, level)
                });
            }
        });

        // set slot
        addTranslator(0x67, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack item = data.read(Type.V1_3B_ITEM, 2);

                if (item != null && LegacyItemList.isEnchantable(item.getItemId()))
                    item.setCompoundTag(null);

                return PacketUtil.createPacket(0x67, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_0R_ITEM, item)
                });
            }
        });

        // window items
        addTranslator(0x68, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack[] items = data.read(Type.V1_3B_ITEM_ARRAY, 1);

                for (ItemStack item : items) {
                    if (item != null && LegacyItemList.isEnchantable(item.getItemId())) {
                        item.setCompoundTag(null);
                    }
                }

                return PacketUtil.createPacket(0x68, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_0R_ITEM_ARRAY, items)
                });
            }
        });


        // player look move
        addTranslator(0x0D, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                if (!storage.hasObject(BlockStorage.class)) {
                    return data;
                }

                double x = data.read(Type.DOUBLE, 0);
                double y = data.read(Type.DOUBLE, 1);
                double originalStance = data.read(Type.DOUBLE, 2);
                double z = data.read(Type.DOUBLE, 3);

                MovementTranslator.updateBoundingBox(session, new Location(x, y, z));
                Location loc = MovementTranslator.correctPosition(session, x, y, z);

                return PacketUtil.createPacket(0x0D, new TypeHolder[] {
                        set(Type.DOUBLE, loc.getX()),
                        set(Type.DOUBLE, loc.getY()),
                        set(Type.DOUBLE, loc.getY() + (originalStance - y)), // stance
                        set(Type.DOUBLE, loc.getZ()),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                });
            }
        });

        // player position
        addTranslator(0x0B, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ProtocolStorage storage = session.getStorage();
                if (!storage.hasObject(BlockStorage.class)) {
                    return data;
                }

                double x = data.read(Type.DOUBLE, 0);
                double y = data.read(Type.DOUBLE, 1);
                double z = data.read(Type.DOUBLE, 3);

                MovementTranslator.updateBoundingBox(session, new Location(x, y, z));
                Location loc = MovementTranslator.correctPosition(session, x, y, z);

                return PacketUtil.createPacket(0x0B, new TypeHolder[] {
                        set(Type.DOUBLE, loc.getX()),
                        set(Type.DOUBLE, loc.getY()),
                        set(Type.DOUBLE, loc.getY() + 1.6200000047683716D),
                        set(Type.DOUBLE, loc.getZ()),
                        data.read(4)
                });
            }
        });
    }
}
