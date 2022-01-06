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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_2.V1_2_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3_4ChunkBulk;
import com.github.dirtpowered.dirtmv.data.protocol.types.ItemArrayDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.item.V1_3RItemDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk.V1_2_3RChunkDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk.V1_3_4RChunkBulkDataType;

public class V1_3_1RProtocol extends BaseProtocol {
    public static final DataType<ItemStack> ITEM;
    public static final DataType<ItemStack[]> ITEM_ARRAY;
    public final static DataType<V1_2Chunk> CHUNK;
    public final static DataType<V1_3_4ChunkBulk> CHUNK_BULK;

    static {
        ITEM = new V1_3RItemDataType();
        ITEM_ARRAY = new ItemArrayDataType(Type.V1_3R_ITEM_ARRAY, ITEM);
        CHUNK = new V1_2_3RChunkDataType(Type.V1_3_CHUNK);
        CHUNK_BULK = new V1_3_4RChunkBulkDataType(Type.V1_3CHUNK_BULK);
    }

    @Override
    public void registerPackets() {
        dataTypes[0] = new DataType[]{INT};
        dataTypes[1] = new DataType[]{INT, STRING, BYTE, BYTE, BYTE, BYTE, BYTE};
        dataTypes[2] = new DataType[]{BYTE, STRING, STRING, INT};
        dataTypes[3] = new DataType[]{STRING};
        dataTypes[4] = new DataType[]{LONG};
        dataTypes[5] = new DataType[]{INT, SHORT, ITEM};
        dataTypes[6] = new DataType[]{INT, INT, INT};
        dataTypes[7] = new DataType[]{INT, INT, BYTE};
        dataTypes[8] = new DataType[]{SHORT, SHORT, FLOAT};
        dataTypes[9] = new DataType[]{INT, BYTE, BYTE, SHORT, STRING};
        dataTypes[10] = new DataType[]{BYTE};
        dataTypes[11] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, DOUBLE, BYTE};
        dataTypes[12] = new DataType[]{FLOAT, FLOAT, BYTE};
        dataTypes[13] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, DOUBLE, FLOAT, FLOAT, BYTE};
        dataTypes[14] = new DataType[]{BYTE, INT, BYTE, INT, BYTE};
        dataTypes[15] = new DataType[]{INT, BYTE, INT, BYTE, ITEM, BYTE, BYTE, BYTE};
        dataTypes[16] = new DataType[]{SHORT};
        dataTypes[17] = new DataType[]{INT, BYTE, INT, BYTE, INT};
        dataTypes[18] = new DataType[]{INT, BYTE};
        dataTypes[19] = new DataType[]{INT, BYTE};
        dataTypes[20] = new DataType[]{INT, STRING, INT, INT, INT, BYTE, BYTE, SHORT, V1_3BProtocol.METADATA};
        dataTypes[21] = new DataType[]{INT, SHORT, BYTE, SHORT, INT, INT, INT, BYTE, BYTE, BYTE};
        dataTypes[22] = new DataType[]{INT, INT};
        dataTypes[23] = new DataType[]{INT, BYTE, INT, INT, INT, V1_3BProtocol.MOTION};
        dataTypes[24] = new DataType[]{INT, BYTE, INT, INT, INT, BYTE, BYTE, BYTE, SHORT, SHORT, SHORT, V1_3BProtocol.METADATA};
        dataTypes[25] = new DataType[]{INT, STRING, INT, INT, INT, INT};
        dataTypes[26] = new DataType[]{INT, INT, INT, INT, SHORT};
        dataTypes[28] = new DataType[]{INT, SHORT, SHORT, SHORT};
        dataTypes[29] = new DataType[]{BYTE_INT_ARRAY};
        dataTypes[30] = new DataType[]{INT};
        dataTypes[31] = new DataType[]{INT, BYTE, BYTE, BYTE};
        dataTypes[32] = new DataType[]{INT, BYTE, BYTE};
        dataTypes[33] = new DataType[]{INT, BYTE, BYTE, BYTE, BYTE, BYTE};
        dataTypes[34] = new DataType[]{INT, INT, INT, INT, BYTE, BYTE};
        dataTypes[35] = new DataType[]{INT, BYTE};
        dataTypes[38] = new DataType[]{INT, BYTE};
        dataTypes[39] = new DataType[]{INT, INT};
        dataTypes[40] = new DataType[]{INT, V1_3BProtocol.METADATA};
        dataTypes[41] = new DataType[]{INT, BYTE, BYTE, SHORT};
        dataTypes[42] = new DataType[]{INT, BYTE};
        dataTypes[43] = new DataType[]{FLOAT, SHORT, SHORT};
        dataTypes[51] = new DataType[]{CHUNK};
        dataTypes[52] = new DataType[]{INT, INT, V1_2_1RProtocol.MULTIBLOCK_ARRAY};
        dataTypes[53] = new DataType[]{INT, BYTE, INT, SHORT, BYTE};
        dataTypes[54] = new DataType[]{INT, SHORT, INT, BYTE, BYTE, SHORT};
        dataTypes[55] = new DataType[]{INT, INT, INT, INT, BYTE};
        dataTypes[56] = new DataType[]{CHUNK_BULK};
        dataTypes[60] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, FLOAT, V1_3BProtocol.POSITION_ARRAY, FLOAT, FLOAT, FLOAT};
        dataTypes[61] = new DataType[]{INT, INT, UNSIGNED_BYTE, INT, INT};
        dataTypes[62] = new DataType[]{STRING, INT, INT, INT, FLOAT, UNSIGNED_BYTE};
        dataTypes[70] = new DataType[]{BYTE, BYTE};
        dataTypes[71] = new DataType[]{INT, BYTE, INT, INT, INT};
        dataTypes[100] = new DataType[]{BYTE, BYTE, STRING, BYTE};
        dataTypes[101] = new DataType[]{BYTE};
        dataTypes[102] = new DataType[]{BYTE, SHORT, BYTE, SHORT, BYTE, ITEM};
        dataTypes[103] = new DataType[]{BYTE, SHORT, ITEM};
        dataTypes[104] = new DataType[]{BYTE, ITEM_ARRAY};
        dataTypes[105] = new DataType[]{BYTE, SHORT, SHORT};
        dataTypes[106] = new DataType[]{BYTE, SHORT, BYTE};
        dataTypes[107] = new DataType[]{SHORT, ITEM};
        dataTypes[108] = new DataType[]{BYTE, BYTE};
        dataTypes[130] = new DataType[]{INT, SHORT, INT, STRING, STRING, STRING, STRING};
        dataTypes[131] = new DataType[]{SHORT, SHORT, BYTE_BYTE_ARRAY};
        dataTypes[132] = new DataType[]{INT, SHORT, INT, BYTE, COMPOUND_TAG};
        dataTypes[200] = new DataType[]{INT, BYTE};
        dataTypes[201] = new DataType[]{STRING, BYTE, SHORT};
        dataTypes[202] = new DataType[]{BYTE, BYTE, BYTE};
        dataTypes[203] = new DataType[]{STRING};
        dataTypes[204] = new DataType[]{STRING, BYTE, BYTE, BYTE};
        dataTypes[205] = new DataType[]{BYTE};
        dataTypes[250] = new DataType[]{STRING, SHORT_BYTE_ARRAY};
        dataTypes[254] = new DataType[]{};
        dataTypes[252] = new DataType[]{SHORT_BYTE_ARRAY, SHORT_BYTE_ARRAY};
        dataTypes[253] = new DataType[]{STRING, SHORT_BYTE_ARRAY, SHORT_BYTE_ARRAY};
        dataTypes[255] = new DataType[]{STRING};
    }
}
