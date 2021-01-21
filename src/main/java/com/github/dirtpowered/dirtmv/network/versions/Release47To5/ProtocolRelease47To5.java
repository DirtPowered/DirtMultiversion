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
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3.V1_3_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_8.V1_8RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyInputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.io.NettyOutputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockChangeRecord;
import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockLocation;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.OptionalPosition;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2MultiBlockArray;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3_4ChunkBulk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8ChunkBulk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.PlayerListEntry;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.TabListAction;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.TabListEntry;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage.BlockStorage;
import com.github.dirtpowered.dirtmv.network.versions.Release28To23.chunk.DimensionTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.chunk.DataFixers;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.chunk.V1_3ToV1_8ChunkTranslator;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.entity.OnGroundTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.entity.V1_7EntityTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory.QuickBarTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory.WindowTypeTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.other.BlockMiningTimeFixer;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.other.HardnessTable;
import com.github.dirtpowered.dirtmv.network.versions.Release4To78.ping.ServerPing;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.entity.EntityTracker;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ProtocolRelease47To5 extends ServerProtocol {

    public ProtocolRelease47To5() {
        super(MinecraftVersion.R1_8, MinecraftVersion.R1_7_6);

        addGroup(new MovementPackets());
        addGroup(new InventoryPackets());
        addGroup(new EntityPackets());
    }

    @Override
    public void onConnect(ServerSession session) {
        ProtocolStorage storage = session.getStorage();
        storage.set(OnGroundTracker.class, new OnGroundTracker());
        storage.set(WindowTypeTracker.class, new WindowTypeTracker());
        storage.set(QuickBarTracker.class, new QuickBarTracker());

        // check if 1.6 entity tracker exists - if not, create one
        if (!storage.hasObject(EntityTracker.class)) {
            storage.set(V1_7EntityTracker.class, new V1_7EntityTracker());
        }

        // fixes block hardness inconsistencies
        if (session.getMain().getConfiguration().getServerVersion() == MinecraftVersion.B1_7_3) {
            storage.set(BlockMiningTimeFixer.class, new BlockMiningTimeFixer(session));
        }
    }

    public static long toBlockPosition(int x, int y, int z) {
        return (((long) x & 0x3FFFFFF) << 38) | ((((long) y) & 0xFFF) << 26) | (((long) z) & 0x3FFFFFF);
    }

    private BlockLocation fromBlockPosition(long encodedPosition) {
        int x = (int) (encodedPosition >> 38);
        int y = (int) ((encodedPosition >> 26) & 4095);
        int z = (int) ((encodedPosition << 38) >> 38);

        return new BlockLocation(x, y, z);
    }

    @Override
    public void registerTranslators() {
        // status ping
        addTranslator(0x00, ProtocolState.STATUS, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String json = data.read(Type.V1_7_STRING, 0);
                ServerPing serverPing = new Gson().fromJson(json, ServerPing.class);

                ServerPing.Version versionObj = new ServerPing.Version();
                versionObj.setName("1.8.x (unstable)");
                versionObj.setProtocol(47);

                serverPing.setVersion(versionObj);

                return PacketUtil.createPacket(0x00, new TypeHolder[]{
                        set(Type.V1_7_STRING, serverPing.toString())
                });
            }
        });

        // keep alive
        addTranslator(0x00, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x00, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0))
                });
            }
        });

        // join game
        addTranslator(0x01, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

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
        addTranslator(0x05, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

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
        addTranslator(0x06, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x06, new TypeHolder[]{
                        data.read(0),
                        set(Type.VAR_INT, (int) data.read(Type.SHORT, 1)),
                        data.read(2)
                });
            }
        });

        // chat
        addTranslator(0x02, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        data.read(0),
                        set(Type.BYTE, (byte) 0),
                });
            }
        });

        // chunk data
        addTranslator(0x21, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                V1_2Chunk chunk = data.read(Type.V1_3_CHUNK, 0);

                int chunkX = chunk.getChunkX();
                int chunkZ = chunk.getChunkZ();

                short bitmap = chunk.getPrimaryBitmap();
                boolean groundUp = chunk.isGroundUp();

                if (groundUp && bitmap == 0) {
                    V1_8Chunk emptyChunk = new V1_8Chunk(chunkX, chunkZ, true, bitmap, new byte[0]);

                    return PacketUtil.createPacket(0x21, new TypeHolder[]{
                            new TypeHolder(Type.V1_8R_CHUNK, emptyChunk)
                    });
                }

                V1_3ToV1_8ChunkTranslator chunkTransformer;
                if (chunk.getStorage() != null) {
                    // use existing chunk storage (pre 1.2 servers)
                    chunkTransformer = new V1_3ToV1_8ChunkTranslator(chunk.getStorage(), groundUp, bitmap);
                } else {
                    ProtocolStorage storage = session.getStorage();

                    boolean skyLight = true;
                    if (storage.hasObject(DimensionTracker.class)) {
                        DimensionTracker tracker = storage.get(DimensionTracker.class);
                        skyLight = tracker.getDimension() == 0;
                    }
                    chunkTransformer = new V1_3ToV1_8ChunkTranslator(chunk.getUncompressedData(), bitmap, skyLight, groundUp);
                }

                V1_8Chunk newChunk = new V1_8Chunk(chunkX, chunkZ, groundUp, bitmap, chunkTransformer.getChunkData());

                return PacketUtil.createPacket(0x21, new TypeHolder[]{
                        new TypeHolder(Type.V1_8R_CHUNK, newChunk)
                });
            }
        });

        // chunk bulk
        addTranslator(0x26, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                V1_3_4ChunkBulk oldChunkBulk = data.read(Type.V1_4CHUNK_BULK, 0);

                int[] x = oldChunkBulk.getColumnX();
                int[] z = oldChunkBulk.getColumnZ();

                int columnAmount = oldChunkBulk.getChunks().length;

                V1_3ToV1_8ChunkTranslator[] bulks = new V1_3ToV1_8ChunkTranslator[columnAmount];

                for (int i = 0; i < columnAmount; i++) {
                    bulks[i] = new V1_3ToV1_8ChunkTranslator(
                            oldChunkBulk.getChunks()[i],
                            oldChunkBulk.getPrimaryBitmaps()[i],
                            oldChunkBulk.isSkylight(),
                            true
                    );
                }

                V1_8ChunkBulk.Chunk[] chunks = new V1_8ChunkBulk.Chunk[columnAmount];

                for (int i = 0; i < columnAmount; i++) {
                    chunks[i] = new V1_8ChunkBulk.Chunk();

                    chunks[i].setData(bulks[i].getChunkData());
                    chunks[i].setDataSize(oldChunkBulk.getPrimaryBitmaps()[i]);
                }

                V1_8ChunkBulk chunkBulk = new V1_8ChunkBulk(oldChunkBulk.isSkylight(), x, z, chunks);

                return PacketUtil.createPacket(0x26, new TypeHolder[]{
                        set(Type.V1_8R_CHUNK_BULK, chunkBulk)
                });
            }
        });

        // multi block change
        addTranslator(0x22, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                V1_2MultiBlockArray blockArray = data.read(Type.V1_2MULTIBLOCK_ARRAY, 2);
                DataInput dis = new DataInputStream(new ByteArrayInputStream(blockArray.getData()));

                BlockChangeRecord[] blockChangeRecords = new BlockChangeRecord[blockArray.getRecordCount()];

                for (int i = 0; i < blockArray.getRecordCount(); i++) {
                    try {
                        short pos = dis.readShort();
                        short packedBlock = dis.readShort();

                        int blockId = packedBlock >> 4;
                        int blockData = packedBlock & 15;

                        blockData = DataFixers.getCorrectedDataFor(blockId, blockData);

                        blockChangeRecords[i] = new BlockChangeRecord(pos, (short) (blockId & 4095) << 4 | blockData & 15);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return PacketUtil.createPacket(0x22, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.V1_8R_MULTIBLOCK_ARRAY, blockChangeRecords)
                });
            }
        });

        // block change
        addTranslator(0x23, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                short y = data.read(Type.UNSIGNED_BYTE, 1);
                int z = data.read(Type.INT, 2);

                int blockId = data.read(Type.VAR_INT, 3);
                int blockData = data.read(Type.UNSIGNED_BYTE, 4);

                blockData = DataFixers.getCorrectedDataFor(blockId, blockData);

                return PacketUtil.createPacket(0x23, new TypeHolder[]{
                        set(Type.LONG, toBlockPosition(x, y, z)),
                        set(Type.VAR_INT, blockId << 4| blockData & 15)
                });
            }
        });

        // block action
        addTranslator(0x24, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                short y = data.read(Type.SHORT, 1);
                int z = data.read(Type.INT, 2);

                return PacketUtil.createPacket(0x24, new TypeHolder[]{
                        set(Type.LONG, toBlockPosition(x, y, z)),
                        data.read(3),
                        data.read(4),
                        data.read(5)
                });
            }
        });

        // block break animation
        addTranslator(0x25, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

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

        // effect
        addTranslator(0x28, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 1);
                byte y = data.read(Type.BYTE, 2);
                int z = data.read(Type.INT, 3);

                return PacketUtil.createPacket(0x28, new TypeHolder[]{
                        data.read(0),
                        set(Type.LONG, toBlockPosition(x, y, z)),
                        data.read(4),
                        data.read(5)
                });
            }
        });

        // update sign
        addTranslator(0x33, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                short y = data.read(Type.SHORT, 1);
                int z = data.read(Type.INT, 2);

                String[] lines = new String[4];
                for (int i = 0; i < 4; i++) {
                    lines[i] = ChatUtils.legacyToJsonString(data.read(Type.V1_7_STRING, 3 + i));
                }

                return PacketUtil.createPacket(0x33, new TypeHolder[]{
                        set(Type.LONG, toBlockPosition(x, y, z)),
                        set(Type.V1_7_STRING, lines[0]),
                        set(Type.V1_7_STRING, lines[1]),
                        set(Type.V1_7_STRING, lines[2]),
                        set(Type.V1_7_STRING, lines[3]),
                });
            }
        });

        // sign editor
        addTranslator(0x36, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                int y = data.read(Type.INT, 1);
                int z = data.read(Type.INT, 2);

                return PacketUtil.createPacket(0x36, new TypeHolder[]{
                        set(Type.LONG, toBlockPosition(x, y, z))
                });
            }
        });

        // tab list item
        addTranslator(0x38, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                String u = data.read(Type.V1_7_STRING, 0);
                if (u == null) { // skip if server is updating ping or something
                    return cancel();
                }

                String username = ChatUtils.stripColor(u);

                boolean online = data.read(Type.BOOLEAN, 1);

                UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));

                if (online) {
                    TabListEntry tabAddListEntry = new TabListEntry(TabListAction.ADD_PLAYER, new PlayerListEntry[]{
                            new PlayerListEntry(new GameProfile(uuid, username), new Property[0], 0, 0, null)
                    });

                    return PacketUtil.createPacket(0x38, new TypeHolder[]{
                            set(Type.TAB_LIST_ENTRY, tabAddListEntry)
                    });
                } else {
                    TabListEntry tabRemoveListEntry = new TabListEntry(TabListAction.REMOVE_PLAYER, new PlayerListEntry[]{
                            new PlayerListEntry(new GameProfile(uuid, username))
                    });

                    return PacketUtil.createPacket(0x38, new TypeHolder[]{
                            set(Type.TAB_LIST_ENTRY, tabRemoveListEntry)
                    });
                }
            }
        });

        // set experience
        addTranslator(0x1F, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x1F, new TypeHolder[]{
                        data.read(0),
                        set(Type.VAR_INT, data.read(Type.SHORT, 1).intValue()),
                        set(Type.VAR_INT, data.read(Type.SHORT, 2).intValue())
                });
            }
        });

        // use bed
        addTranslator(0x0A, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 1);
                byte y = data.read(Type.BYTE, 2);
                int z = data.read(Type.INT, 3);

                return PacketUtil.createPacket(0x0A, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        set(Type.LONG, toBlockPosition(x, y, z))
                });
            }
        });

        // collect item
        addTranslator(0x0D, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x0D, new TypeHolder[]{
                        set(Type.VAR_INT, data.read(Type.INT, 0)),
                        set(Type.VAR_INT, data.read(Type.INT, 1))
                });
            }
        });

        // custom payload
        addTranslator(0x3F, ProtocolState.PLAY, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                byte[] bytes = data.read(Type.SHORT_BYTE_ARRAY, 1);

                // TODO: TrList channel remap
                return PacketUtil.createPacket(0x3F, new TypeHolder[]{
                        data.read(0),
                        set(Type.READABLE_BYTES, bytes)
                });
            }
        });

        // client packets

        // keep alive
        addTranslator(0x00, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x00, new TypeHolder[]{
                        set(Type.INT, data.read(Type.VAR_INT, 0))
                });
            }
        });

        // use entity
        addTranslator(0x02, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                OptionalPosition optPos = data.read(Type.V1_8R_USE_ENTITY_OPTIONAL_POSITION, 1);
                int action = optPos.getAction();

                if (action == 2) {
                    return cancel();
                }

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.INT, data.read(Type.VAR_INT, 0)),
                        set(Type.BYTE, (byte) action)
                });
            }
        });

        // player digging
        addTranslator(0x07, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                long encodedPosition = data.read(Type.LONG, 1);
                int action = data.read(Type.UNSIGNED_BYTE, 0);

                BlockLocation l = fromBlockPosition(encodedPosition);

                ProtocolStorage storage = session.getStorage();
                if (storage.hasObject(BlockMiningTimeFixer.class) && storage.hasObject(BlockStorage.class)) {
                    BlockMiningTimeFixer blockMiningTimeFixer = storage.get(BlockMiningTimeFixer.class);
                    BlockStorage blockStorage = storage.get(BlockStorage.class);

                    switch (action) {
                        case 0: // start digging
                            blockMiningTimeFixer.onBlockStartBreaking(l);
                            break;
                        case 1: // cancel digging
                            blockMiningTimeFixer.onBlockCancelBreaking(l);
                            break;
                        case 2: // finish digging
                            if (HardnessTable.exist(blockStorage.getBlockAt(l.getX(), l.getY(), l.getZ()))) {
                                return cancel();
                            }
                            break;
                    }
                }

                return PacketUtil.createPacket(0x07, new TypeHolder[]{
                        data.read(0),
                        set(Type.INT, l.getX()),
                        set(Type.UNSIGNED_BYTE, (short) l.getY()),
                        set(Type.INT, l.getZ()),
                        set(Type.UNSIGNED_BYTE, data.read(Type.BYTE, 2).shortValue())
                });
            }
        });

        // place block
        addTranslator(0x08, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                long encodedPosition = data.read(Type.LONG, 0);

                BlockLocation l = fromBlockPosition(encodedPosition);
                ItemStack itemStack = data.read(Type.V1_8R_ITEM, 2);

                // 1.0+ servers are kicking players when placed block is 0. Should be null
                if (itemStack != null && itemStack.getItemId() == 0)
                    itemStack = null;

                if (itemStack != null && itemStack.getItemId() == 387 /* written book */) {
                    PacketData payload = PacketUtil.createPacket(0x3F, new TypeHolder[]{
                            set(Type.V1_7_STRING, "MC|BOpen")
                    });

                    session.sendPacket(payload, PacketDirection.TO_CLIENT, getFrom());
                }

                return PacketUtil.createPacket(0x08, new TypeHolder[]{
                        set(Type.INT, l.getX()),
                        set(Type.UNSIGNED_BYTE, (short) l.getY()),
                        set(Type.INT, l.getZ()),
                        data.read(1),
                        set(Type.V1_3R_ITEM, itemStack),
                        data.read(3),
                        data.read(4),
                        data.read(5),
                });
            }
        });

        // animation
        addTranslator(0x0A, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x0A, new TypeHolder[]{
                        set(Type.INT, 0),
                        set(Type.BYTE, (byte) 1)
                });
            }
        });

        // entity action
        addTranslator(0x0B, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int action = data.read(Type.VAR_INT, 1);

                return PacketUtil.createPacket(0x0B, new TypeHolder[]{
                        set(Type.INT, data.read(Type.VAR_INT, 0)),
                        set(Type.BYTE, (byte) (action + 1)),
                        set(Type.INT, data.read(Type.VAR_INT, 2)),
                });
            }
        });

        // update sign
        addTranslator(0x12, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                long encodedPosition = data.read(Type.LONG, 0);
                BlockLocation l = fromBlockPosition(encodedPosition);

                String[] lines = new String[4];
                for (int i = 0; i < 4; i++) {
                    String msg = data.read(Type.V1_7_STRING, 1 + i);
                    if (msg.startsWith("\"")) {
                        msg = ChatUtils.createChatComponentFromInvalidJson(msg);
                    }
                    msg = ChatUtils.jsonToLegacy(msg);
                    lines[i] = msg;
                }

                return PacketUtil.createPacket(0x12, new TypeHolder[]{
                        set(Type.INT, l.getX()),
                        set(Type.SHORT, (short) l.getY()),
                        set(Type.INT, l.getZ()),
                        set(Type.V1_7_STRING, lines[0]),
                        set(Type.V1_7_STRING, lines[1]),
                        set(Type.V1_7_STRING, lines[2]),
                        set(Type.V1_7_STRING, lines[3]),
                });
            }
        });

        // tab complete
        addTranslator(0x14, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x14, new TypeHolder[]{
                        data.read(0)
                });
            }
        });

        // client settings
        addTranslator(0x15, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

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

        // custom payload
        addTranslator(0x17, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) throws IOException {
                byte[] bytes = data.read(Type.READABLE_BYTES, 1);
                String channel = data.read(Type.V1_7_STRING, 0);

                if (channel.equals("MC|BEdit") || channel.equals("MC|BSign")) {
                    NettyInputWrapper in = new NettyInputWrapper(Unpooled.wrappedBuffer(bytes));
                    NettyOutputWrapper out = new NettyOutputWrapper(Unpooled.buffer());

                    ItemStack compressedItem = V1_8RProtocol.ITEM.read(in);

                    V1_3_1RProtocol.ITEM.write(new TypeHolder(Type.V1_3R_ITEM, compressedItem), out);
                    bytes = out.array();
                }

                return PacketUtil.createPacket(0x17, new TypeHolder[]{
                        data.read(0),
                        set(Type.SHORT_BYTE_ARRAY, bytes)
                });
            }
        });

        // player input
        addTranslator(0x0C, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                byte status = data.read(Type.BYTE, 2);

                return PacketUtil.createPacket(0x0C, new TypeHolder[]{
                        data.read(0),
                        data.read(1),
                        set(Type.BOOLEAN, (status & 1) == 1),
                        set(Type.BOOLEAN, (status & 2) == 2)
                });
            }
        });

        // entity attributes
        addTranslator(0x20, -1, ProtocolState.PLAY, PacketDirection.TO_CLIENT);

        // map data
        addTranslator(0x34, -1, ProtocolState.PLAY, PacketDirection.TO_CLIENT);

        // spawn particle
        addTranslator(0x2A, -1, ProtocolState.PLAY, PacketDirection.TO_CLIENT);
    }
}
