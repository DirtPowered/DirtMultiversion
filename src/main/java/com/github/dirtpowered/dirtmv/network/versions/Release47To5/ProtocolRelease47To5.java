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
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.chunk.V1_3ToV1_8ChunkTranslator;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.item.ItemRemapper;
import com.github.dirtpowered.dirtmv.network.versions.Release4To78.ping.ServerPing;
import com.google.gson.Gson;

public class ProtocolRelease47To5 extends ServerProtocol {

    private ItemBlockDataTransformer itemRemapper;

    public ProtocolRelease47To5() {
        super(MinecraftVersion.R1_8, MinecraftVersion.R1_7_6);

        itemRemapper = new ItemRemapper();
    }

    private long toBlockPosition(int x, int y, int z) {
        return (((long) x & 0x3FFFFFF) << 38) | ((((long) y) & 0xFFF) << 26) | (((long) z) & 0x3FFFFFF);
    }

    @Override
    public void registerTranslators() {
        // status ping
        addTranslator(0x00, ProtocolState.STATUS, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String json = data.read(Type.V1_7_STRING, 0);
                ServerPing serverPing = new Gson().fromJson(json, ServerPing.class);

                ServerPing.Version versionObj = new ServerPing.Version();
                versionObj.setName("1.8");
                versionObj.setProtocol(47);

                serverPing.setVersion(versionObj);

                return PacketUtil.createPacket(0x00, new TypeHolder[]{
                        set(Type.V1_7_STRING, serverPing.toString())
                });
            }
        });

        // keep alive
        addTranslator(0x00, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x00, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0))
                });
            }
        });

        // join game
        addTranslator(0x01, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x01, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                        set(Type.BOOLEAN, false)
                });
            }
        });

        // spawn position
        addTranslator(0x05, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                int y = data.read(Type.INT, 1);
                int z = data.read(Type.INT, 2);

                return PacketUtil.createPacket(0x05, new TypeHolder[]{
                        set(Type.LONG, toBlockPosition(x, y, z))
                });
            }
        });

        // update health
        addTranslator(0x06, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x06, new TypeHolder[]{
                        data.read(0),
                        set(Type.VAR_INT, data.read(Type.SHORT, 1)),
                        data.read(2)
                });
            }
        });

        // position and look
        addTranslator(0x08, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x08, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.BYTE, (byte) 0),
                });
            }
        });

        // chat
        addTranslator(0x02, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, (byte) 0),
                });
            }
        });

        // spawn painting
        addTranslator(0x10, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 2);
                int y = data.read(Type.INT, 3);
                int z = data.read(Type.INT, 4);

                int direction = data.read(Type.INT, 5);

                switch (direction) {
                    case 0:
                        z += 1;
                        break;
                    case 1:
                        x -= 1;
                        break;
                    case 2:
                        z -= 1;
                        break;
                    case 3:
                        x += 1;
                        break;
                }

                return PacketUtil.createPacket(0x10, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.LONG, toBlockPosition(x, y, z)),
                        set(Type.BYTE, data.read(Type.INT, 5).byteValue())
                });
            }
        });

        // chunk data
        addTranslator(0x21, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new V1_3ToV1_8ChunkTranslator());

        // block change
        addTranslator(0x23, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                short y = data.read(Type.UNSIGNED_BYTE, 1);
                int z = data.read(Type.INT, 2);

                return PacketUtil.createPacket(0x23, new TypeHolder[]{
                        set(Type.LONG, toBlockPosition(x, y, z)),
                        set(Type.VAR_INT, data.read(Type.VAR_INT, 3) << 4 | (data.read(Type.UNSIGNED_BYTE, 4) & 15))
                });
            }
        });

        // block break animation
        addTranslator(0x25, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 1);
                int y = data.read(Type.INT, 2);
                int z = data.read(Type.INT, 3);

                return PacketUtil.createPacket(0x25, new TypeHolder[]{
                        data.read(0),
                        set(Type.LONG, toBlockPosition(x, y, z)),
                        data.read(4)
                });
            }
        });

        // chunk bulk
        addTranslator(0x26, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // set slot
        addTranslator(0x2F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack originalItem = data.read(Type.V1_3R_ITEM, 2);

                if (originalItem == null)
                    return new PacketData(0x2F, data.getObjects());

                ItemStack itemStack = itemRemapper.replaceItem(originalItem);

                return PacketUtil.createPacket(0x2F, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_8R_ITEM, itemStack)
                });
            }
        });

        // window items
        addTranslator(0x30, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack[] itemArray = data.read(Type.V1_3R_ITEM_ARRAY, 1);

                for (int i = 0; i < itemArray.length; i++) {
                    ItemStack originalItem = itemArray[i];
                    ItemStack item = originalItem;

                    if (originalItem != null) {
                        item = itemRemapper.replaceItem(originalItem);
                    }

                    itemArray[i] = item;
                }

                return PacketUtil.createPacket(0x30, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_8R_ITEM_ARRAY, itemArray)
                });
            }
        });

        // update sign
        addTranslator(0x33, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                short y = data.read(Type.SHORT, 1);
                int z = data.read(Type.INT, 2);

                String[] lines = new String[4];
                for (int i = 0; i < 4; i++) {
                    lines[i] = ChatUtils.legacyToJsonString(data.read(Type.V1_7_STRING, 3 + i));
                }

                return PacketUtil.createPacket(0x33, new TypeHolder[] {
                        set(Type.LONG, toBlockPosition(x, y, z)),
                        set(Type.V1_7_STRING, lines[0]),
                        set(Type.V1_7_STRING, lines[1]),
                        set(Type.V1_7_STRING, lines[2]),
                        set(Type.V1_7_STRING, lines[3]),
                });
            }
        });

        // sign editor
        addTranslator(0x36, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                int y = data.read(Type.INT, 1);
                int z = data.read(Type.INT, 2);

                return PacketUtil.createPacket(0x36, new TypeHolder[] {
                        set(Type.LONG, toBlockPosition(x, y, z))
                });
            }
        });

        // client packets

        // keep alive
        addTranslator(0x00, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x00, new TypeHolder[]{
                        set(Type.INT, data.read(Type.VAR_INT, 0))
                });
            }
        });

        // player
        addTranslator(0x03, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x03, new TypeHolder[]{
                        set(Type.BYTE, data.read(Type.UNSIGNED_BYTE, 0).byteValue())
                });
            }
        });

        // player position
        addTranslator(0x04, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x04, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.DOUBLE, data.read(Type.DOUBLE, 1) + 1.62D), // stance
                        data.read(2),
                        set(Type.BYTE, data.read(Type.UNSIGNED_BYTE, 3).byteValue())
                });
            }
        });

        // player look
        addTranslator(0x05, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x05, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.BYTE, data.read(Type.UNSIGNED_BYTE, 2).byteValue())
                });
            }
        });

        // player pos look
        addTranslator(0x06, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x06, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.DOUBLE, data.read(Type.DOUBLE, 1) + 1.62D),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.BYTE, data.read(Type.UNSIGNED_BYTE, 5).byteValue())
                });
            }
        });

        // animation
        addTranslator(0x0A, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x0A, new TypeHolder[] {
                        set(Type.INT, 0),
                        set(Type.BYTE, (byte) 1)
                });
            }
        });

        // client settings
        addTranslator(0x15, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x15, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        data.read(2),
                        data.read(3),
                        data.read(4),
                        set(Type.BOOLEAN, false)
                });
            }
        });

        // entity equipment
        addTranslator(0x04, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // respawn
        addTranslator(0x07, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // collect item
        addTranslator(0x0D, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // spawn mob
        addTranslator(0x0F, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // spawn player
        addTranslator(0x0C, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity destroy
        addTranslator(0x13, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity
        addTranslator(0x14, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity relative move
        addTranslator(0x15, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity look
        addTranslator(0x16, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity look move
        addTranslator(0x17, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity teleport
        addTranslator(0x18, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity head look
        addTranslator(0x19, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity velocity
        addTranslator(0x12, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity attributes
        addTranslator(0x20, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // entity metadata
        addTranslator(0x1C, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // multi block change
        addTranslator(0x22, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // set experience
        addTranslator(0x1F, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // effect
        addTranslator(0x28, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // sound effect
        addTranslator(0x29, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // update tile entity
        addTranslator(0x35, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // tab list item
        addTranslator(0x38, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);

        // custom payload
        addTranslator(0x3F, -1, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT);
    }
}
