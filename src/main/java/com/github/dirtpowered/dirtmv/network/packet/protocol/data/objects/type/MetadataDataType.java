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
import com.github.dirtpowered.dirtmv.network.packet.Protocol;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.V1_7BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.BlockLocation;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.WatchableObject;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class MetadataDataType extends DataType<List<WatchableObject>> {

    public MetadataDataType(Type type) {
        super(type);
    }

    @Override
    public List<WatchableObject> read(ByteBuf buffer) throws IOException {

        ArrayList<WatchableObject> dataMap = null;

        for (byte b = buffer.readByte(); b != 127; b = buffer.readByte()) {
            if (dataMap == null) {
                dataMap = new ArrayList<>();
            }

            MetadataType type = MetadataType.fromType((b & 224) >> 5);
            int index = b & 31;
            WatchableObject value = null;

            switch (type) {
                case BYTE:
                    value = new WatchableObject(type, index, Protocol.BYTE.read(buffer));
                    break;
                case SHORT:
                    value = new WatchableObject(type, index, Protocol.SHORT.read(buffer));
                    break;
                case INT:
                    value = new WatchableObject(type, index, Protocol.INT.read(buffer));
                    break;
                case FLOAT:
                    value = new WatchableObject(type, index, Protocol.FLOAT.read(buffer));
                    break;
                case STRING:
                    value = new WatchableObject(type, index, Protocol.STRING.read(buffer));
                    break;
                case ITEM:
                    value = new WatchableObject(type, index, V1_7BProtocol.ITEM.read(buffer));
                    break;
                case POSITION:
                    int x = buffer.readInt();
                    int y = buffer.readInt();
                    int z = buffer.readInt();

                    value = new WatchableObject(type, index, new BlockLocation(x, y, z));
                    break;
            }

            dataMap.add(value);
        }

        return dataMap;
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) throws IOException {
        List<WatchableObject> watchableObjects = (List<WatchableObject>) typeHolder.getObject();

        if (watchableObjects == null || watchableObjects.isEmpty()) {
            buffer.writeByte(127);
            return;
        }

        for (WatchableObject watchableObject : watchableObjects) {
            int header = (watchableObject.getType().getType() << 5 | watchableObject.getIndex() & 31) & 255;
            buffer.writeByte(header);
            switch (watchableObject.getType()) {
                case BYTE:
                    Protocol.BYTE.write(new TypeHolder(Type.BYTE, watchableObject.getValue()), buffer);
                    break;
                case SHORT:
                    Protocol.SHORT.write(new TypeHolder(Type.SHORT, watchableObject.getValue()), buffer);
                    break;
                case INT:
                    Protocol.INT.write(new TypeHolder(Type.INT, watchableObject.getValue()), buffer);
                    break;
                case FLOAT:
                    Protocol.FLOAT.write(new TypeHolder(Type.FLOAT, watchableObject.getValue()), buffer);
                    break;
                case STRING:
                    Protocol.STRING.write(new TypeHolder(Type.STRING, watchableObject.getValue()), buffer);
                    break;
                case ITEM:
                    V1_7BProtocol.ITEM.write(new TypeHolder(Type.V1_7B_ITEM, watchableObject.getValue()), buffer);
                    break;
                case POSITION:
                    BlockLocation location = (BlockLocation) watchableObject.getValue();

                    buffer.writeInt(location.getX());
                    buffer.writeInt(location.getY());
                    buffer.writeInt(location.getZ());
                    break;
            }
        }

        buffer.writeByte(127);
    }
}
