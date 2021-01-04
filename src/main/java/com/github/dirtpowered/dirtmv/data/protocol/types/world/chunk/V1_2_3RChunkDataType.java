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
import com.github.dirtpowered.dirtmv.data.protocol.TypeObject;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class V1_2_3RChunkDataType extends DataType<V1_2Chunk> {

    public V1_2_3RChunkDataType(TypeObject type) {
        super(type);
    }

    @Override
    public V1_2Chunk read(PacketInput packetInput) throws IOException {
        int chunkX = packetInput.readInt();
        int chunkZ = packetInput.readInt();

        boolean groundUp = packetInput.readBoolean();

        short primaryBitmap = packetInput.readShort();
        short additionalBitmap = packetInput.readShort();

        int compressedDataSize = packetInput.readInt();

        if (getType() == Type.V1_2_CHUNK) packetInput.readInt(); // unused

        byte[] chunk = packetInput.readBytes(compressedDataSize);

        int i = 0;
        for (int j = 0; j < 16; j++) {
            i += primaryBitmap >> j & 1;
        }

        int size = 12288 * i;

        if (groundUp) {
            size += 256;
        }

        byte[] buf = new byte[size];

        Inflater inflater = new Inflater();
        inflater.setInput(chunk, 0, compressedDataSize);

        try {
            inflater.inflate(buf);
        } catch (DataFormatException e) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        return new V1_2Chunk(chunkX, chunkZ, groundUp, primaryBitmap, additionalBitmap, compressedDataSize, chunk, buf, null);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) {
        V1_2Chunk chunk = (V1_2Chunk) typeHolder.getObject();

        packetOutput.writeInt(chunk.getChunkX());
        packetOutput.writeInt(chunk.getChunkZ());

        packetOutput.writeBoolean(chunk.isGroundUp());

        packetOutput.writeShort(chunk.getPrimaryBitmap() & 65535);
        packetOutput.writeShort(chunk.getAdditionalBitmap() & 65535);

        packetOutput.writeInt(chunk.getCompressedDataSize());
        if (getType() == Type.V1_2_CHUNK) packetOutput.writeInt(0);

        packetOutput.writeBytes(chunk.getData(), chunk.getCompressedDataSize());
    }
}
