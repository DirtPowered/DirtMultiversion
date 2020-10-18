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
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.ItemStack;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.Motion;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

public class ProtocolRelease60To51 extends ServerProtocol {

    public ProtocolRelease60To51() {
        super(MinecraftVersion.R1_5_1, MinecraftVersion.R1_4_6);
    }

    @Override
    public void registerTranslators() {
        addTranslator(0x02 /* HANDSHAKE */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
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

        addTranslator(0x64 /* OPEN WINDOW */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                return PacketUtil.createPacket(0x64, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        set(Type.BYTE, 1) // use original title
                });
            }
        });

        addTranslator(0x68 /* WINDOW ITEMS */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                ItemStack[] itemArray = (ItemStack[]) data.read(1).getObject();
                // TODO: Inventory cache
                return data;
            }
        });

        addTranslator(0x67 /* SET SLOT */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                ItemStack item = (ItemStack) data.read(2).getObject();
                // TODO: Inventory cache
                return data;
            }
        });

        addTranslator(0x66 /* WINDOW CLICK */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                // TODO: fix inventory issues
                return data;
            }
        });


        addTranslator(0x17 /* VEHICLE SPAWN */, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
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
