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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.BlockLocation;
import io.netty.buffer.ByteBuf;

public class PositionArrayDataType extends DataType<BlockLocation[]> {

    public PositionArrayDataType() {
        super(Type.POSITION_ARRAY);
    }

    @Override
    public BlockLocation[] read(ByteBuf buffer) {
        int blockAmount = buffer.readInt();

        BlockLocation[] blockLocations = new BlockLocation[blockAmount];

        for (int i = 0; i < blockAmount; i++) {
            int locX = buffer.readByte();
            int locY = buffer.readByte();
            int locZ = buffer.readByte();

            blockLocations[i] = new BlockLocation(locX, locY, locZ);
        }

        return blockLocations;
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) {
        BlockLocation[] blockLocations = (BlockLocation[]) typeHolder.getObject();

        buffer.writeInt(blockLocations.length);

        for (BlockLocation record : blockLocations) {
            buffer.writeByte(record.getX());
            buffer.writeByte(record.getY());
            buffer.writeByte(record.getZ());
        }
    }
}
