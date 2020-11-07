package com.github.dirtpowered.dirtmv.network.versions.Release74To73;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.EntityAttribute;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_1EntityAttributes;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_2EntityAttributes;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.ping.ServerMotd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProtocolRelease74To73 extends ServerProtocol {

    public ProtocolRelease74To73() {
        super(MinecraftVersion.R1_6_2, MinecraftVersion.R1_6_1);
    }

    @Override
    public void registerTranslators() {
        // handshake
        addTranslator(0x02, PacketDirection.CLIENT_TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                if (data.getObjects().length < 3) {
                    return new PacketData(-1);
                }

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.BYTE, 73),
                        data.read(1),
                        data.read(2),
                        data.read(3)
                });
            }
        });

        // entity attributes/properties
        addTranslator(0x2C, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                V1_6_1EntityAttributes attrObj = data.read(Type.V1_6_1_ENTITY_ATTRIBUTES, 0);
                List<EntityAttribute> entityAttributes = new ArrayList<>();

                for (Map.Entry<String, Double> entry : attrObj.getAttributes().entrySet()) {
                    entityAttributes.add(new EntityAttribute(entry.getKey(), entry.getValue()));
                }

                V1_6_2EntityAttributes attrObjModern = new V1_6_2EntityAttributes(attrObj.getEntityId(), entityAttributes);

                return PacketUtil.createPacket(0x2C, new TypeHolder[]{
                        set(Type.V1_6_2_ENTITY_ATTRIBUTES, attrObjModern)
                });
            }
        });

        // kick disconnect
        addTranslator(0xFF, PacketDirection.SERVER_TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                if (session.getUserData().getPreNettyProtocolState() != PreNettyProtocolState.STATUS)
                    return data;

                String reason = data.read(Type.STRING, 0);

                ServerMotd pingMessage = ServerMotd.deserialize(reason);

                pingMessage.setVersionName("1.6.2");
                pingMessage.setProtocol(session.getUserData().getClientVersion().getProtocolId());

                return PacketUtil.createPacket(0xFF, new TypeHolder[]{
                        set(Type.STRING, ServerMotd.serialize(pingMessage))
                });
            }
        });
    }
}
