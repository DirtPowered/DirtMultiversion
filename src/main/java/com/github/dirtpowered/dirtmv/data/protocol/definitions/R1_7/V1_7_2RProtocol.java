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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.StateDependedProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.types.ByteArrayDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.entity.MetadataDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.entity.V1_6_2EntityAttributesDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.netty.V1_7StringDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.netty.VarIntDataType;

public class V1_7_2RProtocol extends BaseProtocol {

    public static final DataType VAR_INT;
    public static final DataType STRING;
    public static final DataType VAR_INT_BYTE_ARRAY;
    public final static DataType METADATA;
    public static final DataType ENTITY_ATTRIBUTES;

    public static final StateDependedProtocol STATE_DEPENDED_PROTOCOL;

    static {
        VAR_INT = new VarIntDataType();
        STRING = new V1_7StringDataType();
        VAR_INT_BYTE_ARRAY = new ByteArrayDataType(Type.VAR_INT_BYTE_ARRAY);
        METADATA = new MetadataDataType(Type.V1_7R_METADATA);
        ENTITY_ATTRIBUTES = new V1_6_2EntityAttributesDataType(Type.V1_7_ENTITY_ATTRIBUTES);

        STATE_DEPENDED_PROTOCOL = new V1_7_2ProtocolDefinitions();
    }

    @Override
    public void registerPackets() {
        setStateDependedProtocol(STATE_DEPENDED_PROTOCOL);
    }
}
