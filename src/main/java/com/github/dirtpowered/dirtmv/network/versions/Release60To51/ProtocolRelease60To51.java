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

package com.github.dirtpowered.dirtmv.network.versions.Release60To51;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Motion;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

import java.io.IOException;

public class ProtocolRelease60To51 extends ServerProtocol {

    public ProtocolRelease60To51() {
        super(MinecraftVersion.R1_5_1, MinecraftVersion.R1_4_6);
    }

    @Override
    public void registerTranslators() {
        // handshake
        addTranslator(0x02, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                if (data.getObjects().length < 3) {
                    return new PacketData(-1);
                }

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.BYTE, 51),
                        data.read(1),
                        data.read(2),
                        data.read(3)
                });
            }
        });

        // open window
        addTranslator(0x64, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x64, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.BYTE, 0) // use original title
                });
            }
        });

        // window click
        addTranslator(0x66, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                boolean leftClickFlag = false;
                boolean startDragging = false;
                boolean endDragging = false;
                boolean droppingUsingQ = false;
                boolean addSlot = false;

                short slot = data.read(Type.SHORT, 1);

                byte param = data.read(Type.BYTE, 2);
                byte inventoryAction = data.read(Type.BYTE, 4);

                ItemStack item = data.read(Type.V1_3R_ITEM, 5);

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
                    PacketData closeWindow = PacketUtil.createPacket(0x65, new TypeHolder[] {
                            set(Type.BYTE, (byte) 0)
                    });

                    session.sendPacket(closeWindow, PacketDirection.SERVER_TO_CLIENT, getFrom());
                    return new PacketData(-1);
                }

                // random item
                ItemStack i = new ItemStack(352, 2, 0, null);

                ItemStack itemStack = item == null ? (slot < 0 ? null : i) : item;

                if (itemStack == null && !clickingOutside)
                    return new PacketData(-1);

                return PacketUtil.createPacket(0x66, new TypeHolder[] {
                        data.read(0),
                        data.read(1),
                        set(Type.BYTE, (byte) mouseClick),
                        set(Type.SHORT, 0),
                        set(Type.BYTE, (byte) (usingShift ? 1 : 0)),
                        set(Type.V1_3R_ITEM, itemStack)
                });
            }
        });

        // vehicle spawn
        addTranslator(0x17, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                Motion motion = data.read(Type.MOTION, 7);

                int throwerId = motion.getThrowerId();
                byte vehicleType = data.read(Type.BYTE, 1);

                byte vehicleFixedType = vehicleType;

                switch (vehicleType) {
                    case (byte) 10: // rideable minecart
                        vehicleFixedType = 10;
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
    }
}
