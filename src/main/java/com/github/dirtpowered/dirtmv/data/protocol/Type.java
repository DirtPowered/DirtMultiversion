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

package com.github.dirtpowered.dirtmv.data.protocol;

import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_8.V1_8BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_0.V1_0RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_2.V1_2_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3.V1_3_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_4.V1_4_6RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_5.V1_5RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_6.V1_6RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_6.V1_6_2RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7.V1_7_2RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_8.V1_8RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockLocation;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Motion;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2MultiBlockArray;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BChunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BMultiBlockArray;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3_4ChunkBulk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_5Team;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_1EntityAttributes;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_2EntityAttributes;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8ChunkBulk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.mojang.nbt.CompoundTag;

import java.io.IOException;
import java.util.UUID;

public class Type {
    public static final TypeObject<Byte> BYTE = new TypeObject<>(Byte.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.BYTE.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Short> UNSIGNED_BYTE = new TypeObject<>(Short.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.UNSIGNED_BYTE.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Double> DOUBLE = new TypeObject<>(Double.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.DOUBLE.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Float> FLOAT = new TypeObject<>(Float.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.FLOAT.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Integer> INT = new TypeObject<>(Integer.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.INT.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Long> LONG = new TypeObject<>(Long.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.LONG.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Short> SHORT = new TypeObject<>(Short.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.SHORT.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Integer> UNSIGNED_SHORT = new TypeObject<>(Integer.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.UNSIGNED_SHORT.write(holder, packetOutput);
        }
    });

    public static final TypeObject<String> STRING = new TypeObject<>(String.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.STRING.write(holder, packetOutput);
        }
    });

    public static final TypeObject<String> UTF8_STRING = new TypeObject<>(String.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.UTF8_STRING.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Boolean> BOOLEAN = new TypeObject<>(Boolean.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.BOOLEAN.write(holder, packetOutput);
        }
    });

    public static final TypeObject<byte[]> BYTE_BYTE_ARRAY = new TypeObject<>(byte[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.BYTE_BYTE_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<byte[]> SHORT_BYTE_ARRAY = new TypeObject<>(byte[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.SHORT_BYTE_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<byte[]> UNSIGNED_SHORT_BYTE_ARRAY = new TypeObject<>(byte[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.UNSIGNED_SHORT_BYTE_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<byte[]> INT_BYTE_ARRAY = new TypeObject<>(byte[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.INT_BYTE_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<int[]> BYTE_INT_ARRAY = new TypeObject<>(int[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.BYTE_INT_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<BlockLocation[]> POSITION_ARRAY = new TypeObject<>(BlockLocation[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3BProtocol.POSITION_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_3BMultiBlockArray> V1_3BMULTIBLOCK_ARRAY = new TypeObject<>(V1_3BMultiBlockArray.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3BProtocol.MULTIBLOCK_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_2MultiBlockArray> V1_2MULTIBLOCK_ARRAY = new TypeObject<>(V1_2MultiBlockArray.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_2_1RProtocol.MULTIBLOCK_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack[]> V1_3B_ITEM_ARRAY = new TypeObject<>(ItemStack[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3BProtocol.ITEM_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack[]> V1_0R_ITEM_ARRAY = new TypeObject<>(ItemStack[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_0RProtocol.ITEM_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack[]> V1_3R_ITEM_ARRAY = new TypeObject<>(ItemStack[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3_1RProtocol.ITEM_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_3BChunk> V1_3B_CHUNK = new TypeObject<>(V1_3BChunk.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3BProtocol.CHUNK.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_2Chunk> V1_2_CHUNK = new TypeObject<>(V1_2Chunk.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_2_1RProtocol.CHUNK.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_2Chunk> V1_3_CHUNK = new TypeObject<>(V1_2Chunk.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3_1RProtocol.CHUNK.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_3_4ChunkBulk> V1_3CHUNK_BULK = new TypeObject<>(V1_3_4ChunkBulk.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3_1RProtocol.CHUNK_BULK.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_3_4ChunkBulk> V1_4CHUNK_BULK = new TypeObject<>(V1_3_4ChunkBulk.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_4_6RProtocol.CHUNK_BULK.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack> V1_3B_ITEM = new TypeObject<>(ItemStack.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3BProtocol.ITEM.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack> V1_8B_ITEM = new TypeObject<>(ItemStack.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_8BProtocol.ITEM.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack> V1_0R_ITEM = new TypeObject<>(ItemStack.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_0RProtocol.ITEM.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack> V1_3R_ITEM = new TypeObject<>(ItemStack.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3_1RProtocol.ITEM.write(holder, packetOutput);
        }
    });

    public static final TypeObject<WatchableObject[]> V1_3B_METADATA = new TypeObject<>(WatchableObject[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3BProtocol.METADATA.write(holder, packetOutput);
        }
    });

    public static final TypeObject<WatchableObject[]> V1_4R_METADATA = new TypeObject<>(WatchableObject[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_4_6RProtocol.METADATA.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Motion> MOTION = new TypeObject<>(Motion.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_3BProtocol.MOTION.write(holder, packetOutput);
        }
    });

    public static final TypeObject<CompoundTag> COMPOUND_TAG = new TypeObject<>(CompoundTag.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            BaseProtocol.COMPOUND_TAG.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_5Team> V1_5_TEAM = new TypeObject<>(V1_5Team.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_5RProtocol.TEAM.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_6_1EntityAttributes> V1_6_1_ENTITY_ATTRIBUTES = new TypeObject<>(V1_6_1EntityAttributes.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_6RProtocol.ENTITY_ATTRIBUTES.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_6_2EntityAttributes> V1_6_2_ENTITY_ATTRIBUTES = new TypeObject<>(V1_6_2EntityAttributes.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_6_2RProtocol.ENTITY_ATTRIBUTES.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_6_2EntityAttributes> V1_7_ENTITY_ATTRIBUTES = new TypeObject<>(V1_6_2EntityAttributes.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_7_2RProtocol.ENTITY_ATTRIBUTES.write(holder, packetOutput);
        }
    });

    public static final TypeObject<Integer> VAR_INT = new TypeObject<>(Integer.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_7_2RProtocol.VAR_INT.write(holder, packetOutput);
        }
    });

    public static final TypeObject<String> V1_7_STRING = new TypeObject<>(String.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_7_2RProtocol.STRING.write(holder, packetOutput);
        }
    });

    public static final TypeObject<byte[]> VAR_INT_BYTE_ARRAY = new TypeObject<>(byte[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_7_2RProtocol.VAR_INT_BYTE_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<WatchableObject[]> V1_7R_METADATA = new TypeObject<>(WatchableObject[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_7_2RProtocol.METADATA.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_8Chunk> V1_8R_CHUNK = new TypeObject<>(V1_8Chunk.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_8RProtocol.CHUNK.write(holder, packetOutput);
        }
    });

    public static final TypeObject<V1_8ChunkBulk> V1_8R_CHUNK_BULK = new TypeObject<>(V1_8ChunkBulk.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_8RProtocol.CHUNK_BULK.write(holder, packetOutput);
        }
    });

    public static final TypeObject<UUID> UUID = new TypeObject<>(UUID.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_8RProtocol.UUID.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack> V1_8R_ITEM = new TypeObject<>(ItemStack.class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_8RProtocol.ITEM.write(holder, packetOutput);
        }
    });

    public static final TypeObject<ItemStack[]> V1_8R_ITEM_ARRAY = new TypeObject<>(ItemStack[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_8RProtocol.ITEM_ARRAY.write(holder, packetOutput);
        }
    });

    public static final TypeObject<WatchableObject[]> V1_8R_METADATA = new TypeObject<>(WatchableObject[].class, new TypeHandler() {
        @Override
        public void handle(TypeHolder holder, PacketOutput packetOutput) throws IOException {
            V1_8RProtocol.METADATA.write(holder, packetOutput);
        }
    });
}
