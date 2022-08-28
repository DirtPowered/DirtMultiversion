/*
 * Copyright (c) 2020-2022 Dirt Powered
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
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3_4ChunkBulk;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class V1_3_4RChunkBulkDataType extends DataType<V1_3_4ChunkBulk> {

    public V1_3_4RChunkBulkDataType(TypeObject type) {
        super(type);
    }

    @Override
    public V1_3_4ChunkBulk read(PacketInput packetInput) throws IOException {
        short columnAmount = packetInput.readShort();
        int arrayLength = packetInput.readInt();

        boolean skylight = false;
        if (getType() == Type.V1_4CHUNK_BULK) {
            skylight = packetInput.readBoolean();
        }

        int[] columnX = new int[columnAmount];
        int[] columnZ = new int[columnAmount];

        int[] primaryBitMasks = new int[columnAmount];
        int[] additionalBitMasks = new int[columnAmount];

        byte[][] chunks = new byte[columnAmount][];

        byte[] compressedSizeArray = packetInput.readBytes(arrayLength);

        byte[] decompressed = new byte[196864 * columnAmount];

        Inflater inflater = new Inflater();
        inflater.setInput(compressedSizeArray, 0, arrayLength);

        try {
            inflater.inflate(decompressed);
        } catch (DataFormatException dataformatexception) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        int length = 0;

        for (int i = 0; i < columnAmount; i++) {
            columnX[i] = packetInput.readInt();
            columnZ[i] = packetInput.readInt();

            primaryBitMasks[i] = packetInput.readShort();
            additionalBitMasks[i] = packetInput.readShort();
            int offset = 0;

            for (int j = 0; j < 16; j++) {
                offset += primaryBitMasks[i] >> j & 1;
            }

            int dataSize;
            if (skylight) {
                dataSize = 2048 * 5 * offset + 256;
            } else {
                dataSize = 2048 * 4 * offset + 256;
            }

            chunks[i] = new byte[dataSize];
            System.arraycopy(decompressed, length, chunks[i], 0, dataSize);

            length += dataSize;
        }

        return new V1_3_4ChunkBulk(columnX, columnZ, skylight, primaryBitMasks, additionalBitMasks, compressedSizeArray, chunks, arrayLength);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) {
        V1_3_4ChunkBulk chunkBulk = (V1_3_4ChunkBulk) typeHolder.getObject();

        packetOutput.writeShort(chunkBulk.getColumnX().length);
        packetOutput.writeInt(chunkBulk.getLength());
        if (getType() == Type.V1_4CHUNK_BULK) {
            packetOutput.writeBoolean(chunkBulk.isSkylight());
        }

        packetOutput.writeBytes(chunkBulk.getCompressedSize(), chunkBulk.getLength());

        for (int i = 0; i < chunkBulk.getColumnX().length; i++) {
            packetOutput.writeInt(chunkBulk.getColumnX()[i]);
            packetOutput.writeInt(chunkBulk.getColumnZ()[i]);
            packetOutput.writeShort(chunkBulk.getPrimaryBitmaps()[i]);
            packetOutput.writeShort(chunkBulk.getAdditionalBitmaps()[i]);
        }
    }
}
