package com.github.dirtpowered.dirtmv.data.protocol.types.entity;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.AttributeModifier;
import com.github.dirtpowered.dirtmv.data.protocol.objects.EntityAttribute;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_2EntityAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class V1_6_2EntityAttributesDataType extends DataType<V1_6_2EntityAttributes> {

    public V1_6_2EntityAttributesDataType() {
        super(Type.V1_6_2_ENTITY_ATTRIBUTES);
    }

    @Override
    public V1_6_2EntityAttributes read(PacketInput packetInput) throws IOException {
        int entityId = packetInput.readInt();
        int attrCount = packetInput.readInt();

        List<EntityAttribute> entityAttributes = new ArrayList<>();

        for (int i = 0; i < attrCount; i++) {
            String name = (String) BaseProtocol.STRING.read(packetInput);
            double value = packetInput.readDouble();

            EntityAttribute entityAttribute = new EntityAttribute(name, value);

            int additionalData = packetInput.readUnsignedByte();
            for (int j = 0; j < additionalData; j++) {
                UUID uuid = new UUID(packetInput.readLong(), packetInput.readLong());

                double amount = packetInput.readDouble();
                int operation = packetInput.readByte();

                entityAttribute.addAttribute(new AttributeModifier(uuid, amount, operation));
            }

            entityAttributes.add(entityAttribute);
        }

        return new V1_6_2EntityAttributes(entityId, entityAttributes);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) throws IOException {
        V1_6_2EntityAttributes entityAttributes = (V1_6_2EntityAttributes) typeHolder.getObject();

        packetOutput.writeInt(entityAttributes.getEntityId());
        packetOutput.writeInt(entityAttributes.getEntityAttributes().size());

        for (EntityAttribute key : entityAttributes.getEntityAttributes()) {
            BaseProtocol.STRING.write(new TypeHolder(Type.STRING, key.getName()), packetOutput);
            packetOutput.writeDouble(key.getValue());

            packetOutput.writeByte(key.getAttributeModifiers().size());

            for (AttributeModifier attributeModifier : key.getAttributeModifiers()) {
                packetOutput.writeLong(attributeModifier.getUuid().getMostSignificantBits());
                packetOutput.writeLong(attributeModifier.getUuid().getLeastSignificantBits());

                packetOutput.writeDouble(attributeModifier.getAmount());
                packetOutput.writeByte(attributeModifier.getOperation());
            }
        }
    }
}
