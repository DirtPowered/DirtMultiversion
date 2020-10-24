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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.types.ItemArrayDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.PositionArrayDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.entity.MetadataDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.entity.VehicleMotionDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.item.V1_3BItemDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.V1_3BMultiBlockDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk.V1_3BChunkDataType;

public class V1_3BProtocol extends BaseProtocol {
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
        CHUNK = new V1_3BChunkDataType();
        ITEM = new V1_3BItemDataType();
        ITEM_ARRAY = new ItemArrayDataType(Type.V1_3B_ITEM_ARRAY, ITEM);
        POSITION_ARRAY = new PositionArrayDataType();
        MOTION = new VehicleMotionDataType();
        MULTIBLOCK_ARRAY = new V1_3BMultiBlockDataType();
    }

    @Override
    public void registerPackets() {

    }
}
