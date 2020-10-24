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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_8;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.types.item.V1_8BItemDataType;

public class V1_8BProtocol extends BaseProtocol {
    public static final DataType ITEM;

    static {
        // custom instructions
        ITEM = new V1_8BItemDataType();
    }

    @Override
    public void registerPackets() {
        dataTypes[0] = new DataType[]{INT};
        dataTypes[1] = new DataType[]{INT, STRING, LONG, INT, BYTE, BYTE, BYTE, BYTE};
        dataTypes[2] = new DataType[]{STRING};
        dataTypes[3] = new DataType[]{STRING};
        dataTypes[4] = new DataType[]{LONG};
        dataTypes[5] = new DataType[]{INT, SHORT, SHORT, SHORT};
        dataTypes[6] = new DataType[]{INT, INT, INT};
        dataTypes[7] = new DataType[]{INT, INT, BYTE};
        dataTypes[8] = new DataType[]{SHORT, SHORT, FLOAT};
        dataTypes[9] = new DataType[]{BYTE, BYTE, BYTE, SHORT, LONG};
        dataTypes[10] = new DataType[]{BYTE};
        dataTypes[11] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, DOUBLE, BYTE};
        dataTypes[12] = new DataType[]{FLOAT, FLOAT, BYTE};
        dataTypes[13] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, DOUBLE, FLOAT, FLOAT, BYTE};
        dataTypes[14] = new DataType[]{BYTE, INT, BYTE, INT, BYTE};
        dataTypes[15] = new DataType[]{INT, BYTE, INT, BYTE, V1_3BProtocol.ITEM};
        dataTypes[16] = new DataType[]{SHORT};
        dataTypes[17] = new DataType[]{INT, BYTE, INT, BYTE, INT};
        dataTypes[18] = new DataType[]{INT, BYTE};
        dataTypes[19] = new DataType[]{INT, BYTE};
        dataTypes[20] = new DataType[]{INT, STRING, INT, INT, INT, BYTE, BYTE, SHORT};
        dataTypes[21] = new DataType[]{INT, SHORT, BYTE, SHORT, INT, INT, INT, BYTE, BYTE, BYTE};
        dataTypes[22] = new DataType[]{INT, INT};
        dataTypes[23] = new DataType[]{INT, BYTE, INT, INT, INT, V1_3BProtocol.MOTION};
        dataTypes[24] = new DataType[]{INT, BYTE, INT, INT, INT, BYTE, BYTE, V1_3BProtocol.METADATA};
        dataTypes[25] = new DataType[]{INT, STRING, INT, INT, INT, INT};
        dataTypes[26] = new DataType[]{INT, INT, INT, INT, SHORT};
        dataTypes[27] = new DataType[]{FLOAT, FLOAT, FLOAT, FLOAT, BYTE, BYTE};
        dataTypes[28] = new DataType[]{INT, SHORT, SHORT, SHORT};
        dataTypes[29] = new DataType[]{INT};
        dataTypes[30] = new DataType[]{INT};
        dataTypes[31] = new DataType[]{INT, BYTE, BYTE, BYTE};
        dataTypes[32] = new DataType[]{INT, BYTE, BYTE};
        dataTypes[33] = new DataType[]{INT, BYTE, BYTE, BYTE, BYTE, BYTE};
        dataTypes[34] = new DataType[]{INT, INT, INT, INT, BYTE, BYTE};
        dataTypes[38] = new DataType[]{INT, BYTE};
        dataTypes[39] = new DataType[]{INT, INT};
        dataTypes[40] = new DataType[]{INT, V1_3BProtocol.METADATA};
        dataTypes[41] = new DataType[]{INT, BYTE, BYTE, SHORT};
        dataTypes[42] = new DataType[]{INT, BYTE};
        dataTypes[43] = new DataType[]{BYTE, BYTE, SHORT};
        dataTypes[50] = new DataType[]{INT, INT, BYTE};
        dataTypes[51] = new DataType[]{V1_3BProtocol.CHUNK};
        dataTypes[52] = new DataType[]{INT, INT, V1_3BProtocol.MULTIBLOCK_ARRAY};
        dataTypes[53] = new DataType[]{INT, BYTE, INT, BYTE, BYTE};
        dataTypes[54] = new DataType[]{INT, SHORT, INT, BYTE, BYTE};
        dataTypes[60] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, FLOAT, V1_3BProtocol.POSITION_ARRAY};
        dataTypes[61] = new DataType[]{INT, INT, UNSIGNED_BYTE, INT, INT};
        dataTypes[70] = new DataType[]{BYTE, BYTE};
        dataTypes[71] = new DataType[]{INT, BYTE, INT, INT, INT};
        dataTypes[100] = new DataType[]{BYTE, BYTE, STRING, BYTE};
        dataTypes[101] = new DataType[]{BYTE};
        dataTypes[102] = new DataType[]{BYTE, SHORT, BYTE, SHORT, BYTE, V1_3BProtocol.ITEM};
        dataTypes[103] = new DataType[]{BYTE, SHORT, V1_3BProtocol.ITEM};
        dataTypes[104] = new DataType[]{BYTE, V1_3BProtocol.ITEM_ARRAY};
        dataTypes[105] = new DataType[]{BYTE, SHORT, SHORT};
        dataTypes[106] = new DataType[]{BYTE, SHORT, BYTE};
        dataTypes[107] = new DataType[]{SHORT, ITEM};
        dataTypes[130] = new DataType[]{INT, SHORT, INT, STRING, STRING, STRING, STRING};
        dataTypes[131] = new DataType[]{SHORT, SHORT, BYTE_BYTE_ARRAY};
        dataTypes[200] = new DataType[]{INT, BYTE};
        dataTypes[201] = new DataType[]{STRING, BYTE, SHORT};
        dataTypes[254] = new DataType[]{};
        dataTypes[255] = new DataType[]{STRING};
    }
}
