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

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8Chunk;

import java.io.IOException;

public class V1_8RChunkDataType extends DataType<V1_8Chunk> {

    public V1_8RChunkDataType() {
        super(Type.V1_8R_CHUNK);
    }

    @Override
    public V1_8Chunk read(PacketInput packetInput) {
        int chunkX = packetInput.readInt();
        int chunkZ = packetInput.readInt();

        boolean fullChunk = packetInput.readBoolean();

        short mask = packetInput.readShort();

        int dataLength = packetInput.readVarInt();
        byte[] chunkData = packetInput.readBytes(dataLength);

        return new V1_8Chunk(chunkX, chunkZ, fullChunk, mask, chunkData);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) throws IOException {
        V1_8Chunk chunk = (V1_8Chunk) typeHolder.getObject();

        packetOutput.writeInt(chunk.getChunkX());
        packetOutput.writeInt(chunk.getChunkZ());
        packetOutput.writeBoolean(chunk.isFullChunk());
        packetOutput.writeShort(chunk.getMask());
        packetOutput.writeVarInt(chunk.getChunkData().length);
        packetOutput.writeBytes(chunk.getChunkData(), chunk.getChunkData().length);
    }
}
