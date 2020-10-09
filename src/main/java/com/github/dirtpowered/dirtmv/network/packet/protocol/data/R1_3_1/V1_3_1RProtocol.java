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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_3_1;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Protocol;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.V1_7BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_2_1.V1_2_1RProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_2_1.type.chunk.ChunkDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_3_1.type.item.ItemDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.ItemArrayDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.MetadataDataType;

public class V1_3_1RProtocol extends Protocol {

    public static final DataType ITEM;
    public static final DataType ITEM_ARRAY;
    public final static DataType CHUNK;
    public static final DataType METADATA;

    static {
        ITEM = new ItemDataType();
        ITEM_ARRAY = new ItemArrayDataType(Type.V1_3R_ITEM, ITEM);
        CHUNK = new ChunkDataType(Type.V1_3_CHUNK);
        METADATA = new MetadataDataType(Type.V1_3_METADATA);
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
        dataTypes[21] = new DataType[]{INT, SHORT, BYTE, SHORT, INT, INT, INT, BYTE, BYTE, BYTE};
        dataTypes[22] = new DataType[]{INT, INT};
        dataTypes[23] = new DataType[]{INT, BYTE, INT, INT, INT, V1_7BProtocol.MOTION};
        dataTypes[24] = new DataType[]{INT, BYTE, INT, INT, INT, BYTE, BYTE, BYTE, SHORT, SHORT, SHORT, METADATA};
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
        dataTypes[40] = new DataType[]{METADATA};
        dataTypes[43] = new DataType[]{FLOAT, SHORT, SHORT};
        dataTypes[51] = new DataType[]{CHUNK};
        dataTypes[52] = new DataType[]{INT, INT, V1_2_1RProtocol.MULTIBLOCK_ARRAY};
        dataTypes[53] = new DataType[]{INT, BYTE, INT, SHORT, BYTE};
        dataTypes[54] = new DataType[]{INT, SHORT, INT, BYTE, BYTE, SHORT};
        dataTypes[55] = new DataType[]{INT, INT, INT, INT, BYTE};
        dataTypes[56] = new DataType[]{}; //TODO: Chunk Bulk
        dataTypes[60] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, FLOAT, V1_7BProtocol.POSITION_ARRAY, FLOAT, FLOAT, FLOAT};
        dataTypes[62] = new DataType[]{STRING, INT, INT, INT, FLOAT, BYTE};
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
        dataTypes[132] = new DataType[]{}; //TODO: Tile data
        dataTypes[200] = new DataType[]{INT, BYTE};
        dataTypes[201] = new DataType[]{STRING, BYTE, SHORT};
        dataTypes[202] = new DataType[]{BYTE, BYTE, BYTE};
        dataTypes[203] = new DataType[]{STRING};
        dataTypes[204] = new DataType[]{STRING, BYTE, BYTE, BYTE};
        dataTypes[205] = new DataType[]{BYTE};
        dataTypes[254] = new DataType[]{};
        dataTypes[252] = new DataType[]{SHORT_BYTE_ARRAY, SHORT_BYTE_ARRAY};
        dataTypes[253] = new DataType[]{STRING, SHORT_BYTE_ARRAY, SHORT_BYTE_ARRAY};
        dataTypes[255] = new DataType[]{STRING};
    }
}
