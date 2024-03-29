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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_8;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.StateDependedProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockChangeRecord;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.OptionalPosition;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_2EntityAttributes;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8ChunkBulk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.TabListEntry;
import com.github.dirtpowered.dirtmv.data.protocol.types.CompressedCompoundTagDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.ItemArrayDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.entity.MetadataDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.entity.UseEntityDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.entity.V1_6_2EntityAttributesDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.item.V1_8RItemDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.netty.ReadableBytesDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.netty.TabListEntryDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.netty.UuidDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.netty.VarIntArrayDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.V1_8RMultiBlockDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk.V1_8RChunkBulkDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk.V1_8RChunkDataType;
import net.kyori.adventure.nbt.CompoundBinaryTag;

public class V1_8RProtocol extends BaseProtocol {
    public final static DataType<V1_8Chunk> CHUNK;
    public final static DataType<V1_8ChunkBulk> CHUNK_BULK;
    public final static DataType<java.util.UUID> UUID;
    public final static DataType<ItemStack> ITEM;
    public final static DataType<ItemStack[]> ITEM_ARRAY;
    public final static DataType<WatchableObject[]> METADATA;
    public final static DataType<BlockChangeRecord[]> MULTIBLOCK_ARRAY;
    public final static DataType<OptionalPosition> OPTIONAL_POSITION;
    public final static DataType<int[]> VAR_INT_ARRAY;
    public final static DataType<TabListEntry> TAB_LIST_ENTRY;
    public final static DataType<CompoundBinaryTag> COMPRESSED_TAG;
    public final static DataType<byte[]> READABLE_BYTES;
    public final static DataType<V1_6_2EntityAttributes> ENTITY_ATTRIBUTES;

    private static final StateDependedProtocol STATE_DEPENDED_PROTOCOL;

    static {
        CHUNK = new V1_8RChunkDataType();
        CHUNK_BULK = new V1_8RChunkBulkDataType();
        UUID = new UuidDataType();
        ITEM = new V1_8RItemDataType();
        ITEM_ARRAY = new ItemArrayDataType(Type.V1_8R_ITEM_ARRAY, ITEM);
        METADATA = new MetadataDataType(Type.V1_8R_METADATA);
        MULTIBLOCK_ARRAY = new V1_8RMultiBlockDataType();
        OPTIONAL_POSITION = new UseEntityDataType();
        VAR_INT_ARRAY = new VarIntArrayDataType();
        TAB_LIST_ENTRY = new TabListEntryDataType();
        COMPRESSED_TAG = new CompressedCompoundTagDataType();
        READABLE_BYTES = new ReadableBytesDataType();
        ENTITY_ATTRIBUTES = new V1_6_2EntityAttributesDataType(Type.V1_8_ENTITY_ATTRIBUTES);

        STATE_DEPENDED_PROTOCOL = new V1_8ProtocolDefinitions();
    }

    @Override
    public void registerPackets() {
        setStateDependedProtocol(STATE_DEPENDED_PROTOCOL);
    }
}
