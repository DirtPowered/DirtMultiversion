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

package com.github.dirtpowered.dirtmv.data.protocol.types.entity;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.TypeObject;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3.V1_3_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7.V1_7_2RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockLocation;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.MetadataType;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;

import java.io.IOException;
import java.util.ArrayList;

public class MetadataDataType extends DataType<WatchableObject[]> {

    public MetadataDataType(TypeObject type) {
        super(type);
    }

    @Override
    public WatchableObject[] read(PacketInput packetInput) throws IOException {

        ArrayList<WatchableObject> dataMap = new ArrayList<>();

        for (byte b = packetInput.readByte(); b != 127; b = packetInput.readByte()) {
            MetadataType type = MetadataType.fromType((b & 224) >> 5);
            int index = b & 31;
            WatchableObject value = null;

            switch (type) {
                case BYTE:
                    value = new WatchableObject(type, index, BaseProtocol.BYTE.read(packetInput));
                    break;
                case SHORT:
                    value = new WatchableObject(type, index, BaseProtocol.SHORT.read(packetInput));
                    break;
                case INT:
                    value = new WatchableObject(type, index, BaseProtocol.INT.read(packetInput));
                    break;
                case FLOAT:
                    value = new WatchableObject(type, index, BaseProtocol.FLOAT.read(packetInput));
                    break;
                case STRING:
                    if (getType() == Type.V1_7R_METADATA) {
                        value = new WatchableObject(type, index, V1_7_2RProtocol.STRING.read(packetInput));
                    } else {
                        value = new WatchableObject(type, index, BaseProtocol.STRING.read(packetInput));
                    }
                    break;
                case ITEM:
                    ItemStack itemStack;

                    if (getType() == Type.V1_4R_METADATA || getType() == Type.V1_7R_METADATA) {
                        itemStack = (ItemStack) V1_3_1RProtocol.ITEM.read(packetInput);
                    } else {
                        itemStack = (ItemStack) V1_3BProtocol.ITEM.read(packetInput);
                    }
                    value = new WatchableObject(type, index, itemStack);
                    break;
                case POSITION:
                    int x = packetInput.readInt();
                    int y = packetInput.readInt();
                    int z = packetInput.readInt();

                    value = new WatchableObject(type, index, new BlockLocation(x, y, z));
                    break;
            }

            dataMap.add(value);
        }

        return dataMap.toArray(new WatchableObject[0]);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) throws IOException {
        WatchableObject[] watchableObjects = (WatchableObject[]) typeHolder.getObject();

        if (watchableObjects == null || watchableObjects.length == 0) {
            packetOutput.writeByte(127);
            return;
        }

        for (WatchableObject watchableObject : watchableObjects) {
            int header = (watchableObject.getType().getType() << 5 | watchableObject.getIndex() & 31) & 255;
            packetOutput.writeByte(header);
            switch (watchableObject.getType()) {
                case BYTE:
                    BaseProtocol.BYTE.write(new TypeHolder(Type.BYTE, watchableObject.getValue()), packetOutput);
                    break;
                case SHORT:
                    BaseProtocol.SHORT.write(new TypeHolder(Type.SHORT, watchableObject.getValue()), packetOutput);
                    break;
                case INT:
                    BaseProtocol.INT.write(new TypeHolder(Type.INT, watchableObject.getValue()), packetOutput);
                    break;
                case FLOAT:
                    BaseProtocol.FLOAT.write(new TypeHolder(Type.FLOAT, watchableObject.getValue()), packetOutput);
                    break;
                case STRING:
                    if (getType() == Type.V1_7R_METADATA) {
                        V1_7_2RProtocol.STRING.write(new TypeHolder(Type.V1_7_STRING, watchableObject.getValue()), packetOutput);
                    } else {
                        BaseProtocol.STRING.write(new TypeHolder(Type.STRING, watchableObject.getValue()), packetOutput);
                    }
                    break;
                case ITEM:
                    if (getType() == Type.V1_4R_METADATA || getType() == Type.V1_7R_METADATA) {
                        V1_3_1RProtocol.ITEM.write(new TypeHolder(Type.V1_3R_ITEM, watchableObject.getValue()), packetOutput);
                    } else {
                        V1_3BProtocol.ITEM.write(new TypeHolder(Type.V1_3B_ITEM, watchableObject.getValue()), packetOutput);
                    }
                    break;
                case POSITION:
                    BlockLocation location = (BlockLocation) watchableObject.getValue();

                    packetOutput.writeInt(location.getX());
                    packetOutput.writeInt(location.getY());
                    packetOutput.writeInt(location.getZ());
                    break;
            }
        }

        packetOutput.writeByte(127);
    }
}
