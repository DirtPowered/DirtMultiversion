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

public class V1_3_1RProtocol extends Protocol {

    @Override
    public void registerPackets() {
        dataTypes[0] = new DataType[]{INT};
        dataTypes[1] = new DataType[]{INT, STRING, BYTE, BYTE, BYTE, BYTE, BYTE};
        dataTypes[2] = new DataType[]{BYTE, STRING, STRING, INT};
        dataTypes[3] = new DataType[]{STRING};
        dataTypes[4] = new DataType[]{LONG};
        dataTypes[205] = new DataType[]{BYTE};
        dataTypes[254] = new DataType[]{};
        dataTypes[252] = new DataType[]{SHORT_BYTE_ARRAY, SHORT_BYTE_ARRAY};
        dataTypes[253] = new DataType[]{STRING, SHORT_BYTE_ARRAY, SHORT_BYTE_ARRAY};
        dataTypes[255] = new DataType[]{STRING};
    }
}
