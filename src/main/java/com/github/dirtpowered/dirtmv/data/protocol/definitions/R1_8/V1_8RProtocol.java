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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_8;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.StateDependedProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk.V1_8RChunkBulkDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk.V1_8RChunkDataType;

public class V1_8RProtocol extends BaseProtocol {

    public final static DataType CHUNK;
    public final static DataType CHUNK_BULK;
    private static final StateDependedProtocol STATE_DEPENDED_PROTOCOL;

    static {
        CHUNK = new V1_8RChunkDataType();
        CHUNK_BULK = new V1_8RChunkBulkDataType();
        STATE_DEPENDED_PROTOCOL = new V1_8ProtocolDefinitions();
    }

    @Override
    public void registerPackets() {
        setStateDependedProtocol(STATE_DEPENDED_PROTOCOL);
    }
}
