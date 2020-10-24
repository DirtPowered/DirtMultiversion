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

package com.github.dirtpowered.dirtmv.data.protocol.types.world.chunk;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BChunk;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class V1_3BChunkDataType extends DataType<V1_3BChunk> {

    public V1_3BChunkDataType() {
        super(Type.V1_3B_CHUNK);
    }

    @Override
    public V1_3BChunk read(PacketInput packetInput) {
        int x = packetInput.readInt();
        int y = packetInput.readShort();
        int z = packetInput.readInt();

        int xSize = packetInput.readByte() + 1;
        int ySize = packetInput.readByte() + 1;
        int zSize = packetInput.readByte() + 1;

        byte[] chunk;

        int chunkSize = packetInput.readInt();
        byte[] buf = packetInput.readBytes(chunkSize);

        chunk = new byte[xSize * ySize * zSize * 5 / 2];

        Inflater inflater = new Inflater();
        inflater.setInput(buf);

        try {
            inflater.inflate(chunk);
        } catch (DataFormatException ignored) {
        } finally {
            inflater.end();
        }

        return new V1_3BChunk(x, y, z, xSize, ySize, zSize, chunk);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) {
        V1_3BChunk data1 = (V1_3BChunk) typeHolder.getObject();

        packetOutput.writeInt(data1.getX());
        packetOutput.writeShort(data1.getY());
        packetOutput.writeInt(data1.getZ());

        packetOutput.writeByte(data1.getXSize() - 1);
        packetOutput.writeByte(data1.getYSize() - 1);
        packetOutput.writeByte(data1.getZSize() - 1);

        byte[] data = data1.getChunk();
        Deflater deflater = new Deflater(Deflater.BEST_SPEED);

        try {
            deflater.setInput(data);
            deflater.finish();
            byte[] chunk = new byte[(data1.getXSize() + 1) * (data1.getZSize() + 1) * (data1.getYSize() + 1) * 5 / 2];
            int chunkSize = deflater.deflate(chunk);

            packetOutput.writeInt(chunkSize);
            packetOutput.writeBytes(chunk, chunkSize);
        } finally {
            deflater.end();
        }
    }
}
