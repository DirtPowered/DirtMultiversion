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

package com.github.dirtpowered.dirtmv.network.packet;

import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_8.V1_8BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_0.V1_0RProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_2_1.V1_2_1RProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_3_1.V1_3_1RProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.io.IOException;

public class PacketData {

    @Getter
    private int opCode;

    @Getter
    private TypeHolder[] objects;

    public PacketData(int opCode, TypeHolder... objects) {
        this.opCode = opCode;
        this.objects = objects;
    }

    public TypeHolder read(int index) {
        return objects[index];
    }

    public ByteBuf toMessage() throws IOException {
        ByteBuf buffer = Unpooled.buffer();

        for (TypeHolder typeHolder : objects) {
            switch (typeHolder.getType()) {
                case INT:
                    Protocol.INT.write(typeHolder, buffer);
                    break;
                case BYTE:
                    Protocol.BYTE.write(typeHolder, buffer);
                    break;
                case UNSIGNED_BYTE:
                    Protocol.UNSIGNED_BYTE.write(typeHolder, buffer);
                    break;
                case STRING:
                    Protocol.STRING.write(typeHolder, buffer);
                    break;
                case LONG:
                    Protocol.LONG.write(typeHolder, buffer);
                    break;
                case SHORT:
                    Protocol.SHORT.write(typeHolder, buffer);
                    break;
                case FLOAT:
                    Protocol.FLOAT.write(typeHolder, buffer);
                    break;
                case DOUBLE:
                    Protocol.DOUBLE.write(typeHolder, buffer);
                    break;
                case V1_3B_METADATA:
                    V1_3BProtocol.METADATA.write(typeHolder, buffer);
                    break;
                case V1_3B_ITEM:
                    V1_3BProtocol.ITEM.write(typeHolder, buffer);
                    break;
                case V1_3B_ITEM_ARRAY:
                    V1_3BProtocol.ITEM_ARRAY.write(typeHolder, buffer);
                    break;
                case V1_3B_CHUNK:
                    V1_3BProtocol.CHUNK.write(typeHolder, buffer);
                    break;
                case UTF8_STRING:
                    Protocol.UTF8_STRING.write(typeHolder, buffer);
                    break;
                case BYTE_BYTE_ARRAY:
                    Protocol.BYTE_BYTE_ARRAY.write(typeHolder, buffer);
                    break;
                case SHORT_BYTE_ARRAY:
                    Protocol.SHORT_BYTE_ARRAY.write(typeHolder, buffer);
                    break;
                case V1_8B_ITEM:
                    V1_8BProtocol.ITEM.write(typeHolder, buffer);
                    break;
                case POSITION_ARRAY:
                    V1_3BProtocol.POSITION_ARRAY.write(typeHolder, buffer);
                    break;
                case MOTION:
                    V1_3BProtocol.MOTION.write(typeHolder, buffer);
                    break;
                case V1_3BMULTIBLOCK_ARRAY:
                    V1_3BProtocol.MULTIBLOCK_ARRAY.write(typeHolder, buffer);
                    break;
                case V1_0R_ITEM:
                    V1_0RProtocol.ITEM.write(typeHolder, buffer);
                    break;
                case V1_0R_ITEM_ARRAY:
                    V1_0RProtocol.ITEM_ARRAY.write(typeHolder, buffer);
                    break;
                case V1_2MULTIBLOCK_ARRAY:
                    V1_2_1RProtocol.MULTIBLOCK_ARRAY.write(typeHolder, buffer);
                    break;
                case V1_2_CHUNK:
                    V1_2_1RProtocol.CHUNK.write(typeHolder, buffer);
                    break;
                case V1_3R_ITEM:
                    V1_3_1RProtocol.ITEM.write(typeHolder, buffer);
                    break;
                case V1_3R_ITEM_ARRAY:
                    V1_3_1RProtocol.ITEM_ARRAY.write(typeHolder, buffer);
                    break;
                case V1_3_CHUNK:
                    V1_3_1RProtocol.CHUNK.write(typeHolder, buffer);
                    break;
                case BYTE_INT_ARRAY:
                    Protocol.BYTE_INT_ARRAY.write(typeHolder, buffer);
                    break;
                case INT_BYTE_ARRAY:
                    Protocol.INT_BYTE_ARRAY.write(typeHolder, buffer);
                    break;
            }
        }

        return buffer;
    }
}
