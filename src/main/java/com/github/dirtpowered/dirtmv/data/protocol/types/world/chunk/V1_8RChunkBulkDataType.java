/*
 * Copyright (c) 2020-2021 Dirt Powered
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

package com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk;

import com.github.dirtpowered.dirtmv.data.chunk.ChunkUtils;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8ChunkBulk;

import java.io.IOException;

public class V1_8RChunkBulkDataType extends DataType<V1_8ChunkBulk> {

    public V1_8RChunkBulkDataType() {
        super(Type.V1_8R_CHUNK_BULK);
    }

    @Override
    public V1_8ChunkBulk read(PacketInput packetInput) {
        boolean skylight = packetInput.readBoolean();
        int i = packetInput.readVarInt();

        int[] x = new int[i];
        int[] z = new int[i];

        V1_8ChunkBulk.Chunk[] chunks = new V1_8ChunkBulk.Chunk[i];

        for (int j = 0; j < i; ++j) {
            x[j] = packetInput.readInt();
            z[j] = packetInput.readInt();

            chunks[j] = new V1_8ChunkBulk.Chunk();
            chunks[j].dataSize = packetInput.readUnsignedShort();
            chunks[j].data = new byte[ChunkUtils.calculateDataSize(Integer.bitCount(chunks[j].dataSize), skylight)];
        }

        for (int k = 0; k < i; ++k) {
            chunks[k].data = packetInput.readBytes(chunks[k].data.length);
        }

        return new V1_8ChunkBulk(skylight, x, z, chunks);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) throws IOException {
        V1_8ChunkBulk chunkBulk = (V1_8ChunkBulk) typeHolder.getObject();

        packetOutput.writeBoolean(chunkBulk.isSkylight());
        packetOutput.writeVarInt(chunkBulk.getChunks().length);

        for (int i = 0; i < chunkBulk.getX().length; ++i) {
            packetOutput.writeInt(chunkBulk.getX()[i]);
            packetOutput.writeInt(chunkBulk.getZ()[i]);
            packetOutput.writeShort((short) (chunkBulk.getChunks()[i].dataSize));
        }

        for (int j = 0; j < chunkBulk.getX().length; ++j) {
            packetOutput.writeBytes(chunkBulk.getChunks()[j].data);
        }
    }
}