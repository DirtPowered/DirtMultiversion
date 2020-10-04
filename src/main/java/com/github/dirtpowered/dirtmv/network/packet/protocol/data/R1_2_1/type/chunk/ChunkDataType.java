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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_2_1.type.chunk;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_2Chunk;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ChunkDataType extends DataType<V1_2Chunk> {

    public ChunkDataType() {
        super(Type.V1_2_CHUNK);
    }

    @Override
    public V1_2Chunk read(ByteBuf buffer) throws IOException {
        int chunkX = buffer.readInt();
        int chunkZ = buffer.readInt();
        boolean groundUp = buffer.readBoolean();
        short primaryBitmap = buffer.readShort();
        short additionalBitmap = buffer.readShort();
        int compressedDataSize = buffer.readInt();
        int noop = buffer.readInt();

        byte[] chunk = new byte[compressedDataSize];
        buffer.readBytes(chunk, 0, compressedDataSize);

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

        return new V1_2Chunk(chunkX, chunkZ, groundUp, primaryBitmap, additionalBitmap, size, noop, chunk);
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) {
        V1_2Chunk chunk = (V1_2Chunk) typeHolder.getObject();

        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
        int length;

        try {
            byte[] data = chunk.getData();
            deflater.setInput(data, 0, data.length);
            deflater.finish();

            data = new byte[data.length];
            length = deflater.deflate(data);

            buffer.writeInt(chunk.getChunkX());
            buffer.writeInt(chunk.getChunkZ());
            buffer.writeBoolean(chunk.isGroundUp());
            buffer.writeShort(chunk.getPrimaryBitmap() & 0xffff);
            buffer.writeShort(chunk.getAdditionalBitmap() & 0xffff);
            buffer.writeInt(length);
            buffer.writeInt(chunk.getNoop());
            buffer.writeBytes(data, 0, length);
        } finally {
            deflater.end();
        }
    }
}
