package com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_6;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_2.V1_2_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3.V1_3_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_4.V1_4_6RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_5.V1_5RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.types.entity.V1_6_2EntityAttributesDataType;

public class V1_6_2RProtocol extends V1_6RProtocol {

    public static final DataType ENTITY_ATTRIBUTES;

    static {
        ENTITY_ATTRIBUTES = new V1_6_2EntityAttributesDataType(Type.V1_6_2_ENTITY_ATTRIBUTES);
    }

    @Override
    public void registerPackets() {
        dataTypes[0] = new DataType[]{INT};
        dataTypes[1] = new DataType[]{INT, STRING, BYTE, BYTE, BYTE, BYTE, BYTE};
        dataTypes[2] = new DataType[]{BYTE, STRING, STRING, INT};
        dataTypes[3] = new DataType[]{STRING};
        dataTypes[4] = new DataType[]{LONG, LONG};
        dataTypes[5] = new DataType[]{INT, SHORT, V1_3_1RProtocol.ITEM};
        dataTypes[6] = new DataType[]{INT, INT, INT};
        dataTypes[7] = new DataType[]{INT, INT, BYTE};
        dataTypes[8] = new DataType[]{FLOAT, SHORT, FLOAT}; // changed
        dataTypes[9] = new DataType[]{INT, BYTE, BYTE, SHORT, STRING};
        dataTypes[10] = new DataType[]{BYTE};
        dataTypes[11] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, DOUBLE, BYTE};
        dataTypes[12] = new DataType[]{FLOAT, FLOAT, BYTE};
        dataTypes[13] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, DOUBLE, FLOAT, FLOAT, BYTE};
        dataTypes[14] = new DataType[]{BYTE, INT, BYTE, INT, BYTE};
        dataTypes[15] = new DataType[]{INT, BYTE, INT, BYTE, V1_3_1RProtocol.ITEM, BYTE, BYTE, BYTE};
        dataTypes[16] = new DataType[]{SHORT};
        dataTypes[17] = new DataType[]{INT, BYTE, INT, BYTE, INT};
        dataTypes[18] = new DataType[]{INT, BYTE};
        dataTypes[19] = new DataType[]{INT, BYTE, INT}; // changed
        dataTypes[20] = new DataType[]{INT, STRING, INT, INT, INT, BYTE, BYTE, SHORT, V1_4_6RProtocol.METADATA};
        dataTypes[22] = new DataType[]{INT, INT};
        dataTypes[23] = new DataType[]{INT, BYTE, INT, INT, INT, BYTE, BYTE, V1_3BProtocol.MOTION};
        dataTypes[24] = new DataType[]{INT, BYTE, INT, INT, INT, BYTE, BYTE, BYTE, SHORT, SHORT, SHORT, V1_4_6RProtocol.METADATA};
        dataTypes[25] = new DataType[]{INT, STRING, INT, INT, INT, INT};
        dataTypes[26] = new DataType[]{INT, INT, INT, INT, SHORT};
        dataTypes[27] = new DataType[]{FLOAT, FLOAT, BOOLEAN, BOOLEAN}; // new
        dataTypes[28] = new DataType[]{INT, SHORT, SHORT, SHORT};
        dataTypes[29] = new DataType[]{BYTE_INT_ARRAY};
        dataTypes[30] = new DataType[]{INT};
        dataTypes[31] = new DataType[]{INT, BYTE, BYTE, BYTE};
        dataTypes[32] = new DataType[]{INT, BYTE, BYTE};
        dataTypes[33] = new DataType[]{INT, BYTE, BYTE, BYTE, BYTE, BYTE};
        dataTypes[34] = new DataType[]{INT, INT, INT, INT, BYTE, BYTE};
        dataTypes[35] = new DataType[]{INT, BYTE};
        dataTypes[38] = new DataType[]{INT, BYTE};
        dataTypes[39] = new DataType[]{INT, INT, BOOLEAN}; // changed
        dataTypes[40] = new DataType[]{INT, V1_4_6RProtocol.METADATA};
        dataTypes[41] = new DataType[]{INT, BYTE, BYTE, SHORT};
        dataTypes[42] = new DataType[]{INT, BYTE};
        dataTypes[43] = new DataType[]{FLOAT, SHORT, SHORT};
        dataTypes[44] = new DataType[]{ENTITY_ATTRIBUTES};
        dataTypes[51] = new DataType[]{V1_3_1RProtocol.CHUNK};
        dataTypes[52] = new DataType[]{INT, INT, V1_2_1RProtocol.MULTIBLOCK_ARRAY};
        dataTypes[53] = new DataType[]{INT, BYTE, INT, SHORT, BYTE};
        dataTypes[54] = new DataType[]{INT, SHORT, INT, BYTE, BYTE, SHORT};
        dataTypes[55] = new DataType[]{INT, INT, INT, INT, BYTE};
        dataTypes[56] = new DataType[]{V1_4_6RProtocol.CHUNK_BULK};
        dataTypes[60] = new DataType[]{DOUBLE, DOUBLE, DOUBLE, FLOAT, V1_3BProtocol.POSITION_ARRAY, FLOAT, FLOAT, FLOAT};
        dataTypes[61] = new DataType[]{INT, INT, UNSIGNED_BYTE, INT, INT, BYTE};
        dataTypes[62] = new DataType[]{STRING, INT, INT, INT, FLOAT, UNSIGNED_BYTE};
        dataTypes[63] = new DataType[]{STRING, FLOAT, FLOAT, FLOAT, FLOAT, FLOAT, FLOAT, FLOAT, INT};
        dataTypes[70] = new DataType[]{BYTE, BYTE};
        dataTypes[71] = new DataType[]{INT, BYTE, INT, INT, INT};
        dataTypes[100] = new DataType[]{BYTE, BYTE, STRING, BYTE, BYTE}; // TODO: Optional horse data
        dataTypes[101] = new DataType[]{BYTE};
        dataTypes[102] = new DataType[]{BYTE, SHORT, BYTE, SHORT, BYTE, V1_3_1RProtocol.ITEM};
        dataTypes[103] = new DataType[]{BYTE, SHORT, V1_3_1RProtocol.ITEM};
        dataTypes[104] = new DataType[]{BYTE, V1_3_1RProtocol.ITEM_ARRAY};
        dataTypes[105] = new DataType[]{BYTE, SHORT, SHORT};
        dataTypes[106] = new DataType[]{BYTE, SHORT, BYTE};
        dataTypes[107] = new DataType[]{SHORT, V1_3_1RProtocol.ITEM};
        dataTypes[108] = new DataType[]{BYTE, BYTE};
        dataTypes[130] = new DataType[]{INT, SHORT, INT, STRING, STRING, STRING, STRING};
        dataTypes[131] = new DataType[]{SHORT, SHORT, UNSIGNED_SHORT_BYTE_ARRAY};
        dataTypes[132] = new DataType[]{INT, SHORT, INT, BYTE, COMPOUND_TAG};
        dataTypes[133] = new DataType[]{BYTE, INT, INT, INT}; // new
        dataTypes[200] = new DataType[]{INT, INT}; // changed
        dataTypes[201] = new DataType[]{STRING, BYTE, SHORT};
        dataTypes[202] = new DataType[]{BYTE, FLOAT, FLOAT}; // changed
        dataTypes[203] = new DataType[]{STRING};
        dataTypes[204] = new DataType[]{STRING, BYTE, BYTE, BYTE, BYTE};
        dataTypes[205] = new DataType[]{BYTE};
        dataTypes[206] = new DataType[]{STRING, STRING, BYTE};
        dataTypes[207] = new DataType[]{STRING, BYTE, STRING, INT}; // FIXME: something is wrong here
        dataTypes[208] = new DataType[]{BYTE, STRING};
        dataTypes[209] = new DataType[]{V1_5RProtocol.TEAM};
        dataTypes[250] = new DataType[]{STRING, SHORT_BYTE_ARRAY};
        dataTypes[254] = new DataType[]{BYTE};
        dataTypes[252] = new DataType[]{SHORT_BYTE_ARRAY, SHORT_BYTE_ARRAY};
        dataTypes[253] = new DataType[]{STRING, SHORT_BYTE_ARRAY, SHORT_BYTE_ARRAY};
        dataTypes[255] = new DataType[]{STRING};
    }
}
