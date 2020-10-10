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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_3;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Protocol;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_3.type.arrays.MultiBlockArrayDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_3.type.chunk.ChunkDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_3.type.item.ItemDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_3.type.motion.MotionDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.ItemArrayDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.MetadataDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.PositionArrayDataType;

public class V1_3BProtocol extends Protocol {
    public static final DataType METADATA;
    public static final DataType CHUNK;
    public static final DataType ITEM;
    public static final DataType ITEM_ARRAY;
    public static final DataType POSITION_ARRAY;
    public static final DataType MOTION;
    public static final DataType MULTIBLOCK_ARRAY;

    static {
        // custom instructions
        METADATA = new MetadataDataType(Type.V1_3B_METADATA);
        CHUNK = new ChunkDataType();
        ITEM = new ItemDataType();
        ITEM_ARRAY = new ItemArrayDataType(Type.V1_3B_ITEM_ARRAY, ITEM);
        POSITION_ARRAY = new PositionArrayDataType();
        MOTION = new MotionDataType();
        MULTIBLOCK_ARRAY = new MultiBlockArrayDataType();
    }

    @Override
    public void registerPackets() {

    }
}
