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

package com.github.dirtpowered.dirtmv.network.versions.Release60To51;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.mappings.MappingLoader;
import com.github.dirtpowered.dirtmv.data.mappings.model.CreativeTabListModel;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Motion;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

public class ProtocolRelease60To51 extends ServerProtocol {
    private final CreativeTabListModel creativeTab;

    public ProtocolRelease60To51() {
        super(MinecraftVersion.R1_5_1, MinecraftVersion.R1_4_6);
        creativeTab = MappingLoader.load(CreativeTabListModel.class, "60To51CreativeTabItems");
    }

    @Override
    public void registerTranslators() {
        // handshake
        addTranslator(0x02, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.BYTE, (byte) 51),
                        data.read(1),
                        data.read(2),
                        data.read(3)
                });
            }
        });

        // open window
        addTranslator(0x64, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x64, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.BYTE, (byte) 0) // use original title
                });
            }
        });

        // window click
        addTranslator(0x66, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                boolean leftClickFlag = false;
                boolean startDragging = false;
                boolean endDragging = false;
                boolean droppingUsingQ = false;
                boolean addSlot = false;

                short slot = data.read(Type.SHORT, 1);

                byte param = data.read(Type.BYTE, 2);
                byte inventoryAction = data.read(Type.BYTE, 4);

                switch (inventoryAction) {
                    case 0:
                        leftClickFlag = param == 0;
                        break;
                    case 4:
                        droppingUsingQ = param + (slot != -999 ? 2 : 0) == 2;
                        break;
                    case 5:
                        startDragging = param == 0;
                        endDragging = param == 2;
                        addSlot = param == 1;
                        break;
                }

                boolean leftClick = leftClickFlag || startDragging || addSlot || endDragging;
                boolean clickingOutside = slot == -999 && inventoryAction != 5;
                boolean usingShift = inventoryAction == 1;

                int mouseClick = leftClick ? 0 : 1;

                if (droppingUsingQ) {
                    PacketData closeWindow = PacketUtil.createPacket(0x65, new TypeHolder[]{
                            set(Type.BYTE, (byte) 0)
                    });

                    session.sendPacket(closeWindow, PacketDirection.TO_CLIENT, getFrom());
                    return cancel();
                }

                if (slot < 0 && !clickingOutside)
                    return cancel();

                return PacketUtil.createPacket(0x66, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.BYTE, (byte) mouseClick),
                        data.read(3),
                        set(Type.BYTE, (byte) (usingShift ? 1 : 0)),
                        set(Type.V1_3R_ITEM, new ItemStack(34, 0, 0, null))
                });
            }
        });

        // vehicle spawn
        addTranslator(0x17, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                Motion motion = data.read(Type.MOTION, 7);

                int throwerId = motion.getThrowerId();
                byte vehicleType = data.read(Type.BYTE, 1);

                byte vehicleFixedType = vehicleType;

                switch (vehicleType) {
                    case (byte) 10: // rideable minecart
                        throwerId = 0;
                        break;
                    case (byte) 11: // chest minecart
                        vehicleFixedType = 10;
                        throwerId = 1;
                        break;
                    case (byte) 12: // furnace minecart
                        vehicleFixedType = 10;
                        throwerId = 2;
                        break;
                }

                motion.setThrowerId(throwerId);

                return PacketUtil.createPacket(0x17, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, vehicleFixedType),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        data.read(6),
                        set(Type.MOTION, motion)
                });
            }
        });

        // creative item get
        addTranslator(0x6B, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack item = data.read(Type.V1_3R_ITEM, 1);

                boolean notNull = item != null;

                if (notNull && !creativeTab.exists(item.getItemId())) {
                    // replace all unknown items to stone
                    item.setItemId(1);
                    item.setData(0);
                }

                return PacketUtil.createPacket(0x6B, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_3R_ITEM, item)
                });
            }
        });
    }
}
