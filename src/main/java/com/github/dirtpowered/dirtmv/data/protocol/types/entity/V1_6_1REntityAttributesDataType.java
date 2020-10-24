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
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_1EntityAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class V1_6_1REntityAttributesDataType extends DataType<V1_6_1EntityAttributes> {

    public V1_6_1REntityAttributesDataType() {
        super(Type.V1_6_1_ENTITY_ATTRIBUTES);
    }

    @Override
    public V1_6_1EntityAttributes read(PacketInput packetInput) throws IOException {
        int entityId = packetInput.readInt();
        int attrCount = packetInput.readInt();

        Map<String, Double> attributes = new HashMap<>();

        for (int i = 0; i < attrCount; i++) {
            attributes.put((String) BaseProtocol.STRING.read(packetInput), packetInput.readDouble());
        }

        return new V1_6_1EntityAttributes(entityId, attributes);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) throws IOException {
        V1_6_1EntityAttributes attributes = (V1_6_1EntityAttributes) typeHolder.getObject();

        packetOutput.writeInt(attributes.getEntityId());
        packetOutput.writeInt(attributes.getAttributes().size());

        for (String key : attributes.getAttributes().keySet()) {
            BaseProtocol.STRING.write(new TypeHolder(Type.STRING, key), packetOutput);
            packetOutput.writeDouble(attributes.getAttributes().get(key));
        }
    }
}
