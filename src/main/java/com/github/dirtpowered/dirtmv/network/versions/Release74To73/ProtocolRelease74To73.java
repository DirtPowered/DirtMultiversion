package com.github.dirtpowered.dirtmv.network.versions.Release74To73;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.mappings.MappingLoader;
import com.github.dirtpowered.dirtmv.data.mappings.model.CreativeTabListModel;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.EntityAttribute;
import com.github.dirtpowered.dirtmv.data.protocol.objects.ItemStack;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_1EntityAttributes;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_6_2EntityAttributes;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.ping.ServerMotd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProtocolRelease74To73 extends ServerProtocol {
    private final CreativeTabListModel creativeTab;

    public ProtocolRelease74To73() {
        super(MinecraftVersion.R1_6_2, MinecraftVersion.R1_6_1);
        creativeTab = MappingLoader.load(CreativeTabListModel.class, "74To37CreativeTabItems");
    }

    @Override
    public void registerTranslators() {
        // handshake
        addTranslator(0x02, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.BYTE, (byte) 73),
                        data.read(1),
                        data.read(2),
                        data.read(3)
                });
            }
        });

        // entity attributes/properties
        addTranslator(0x2C, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                V1_6_1EntityAttributes attrObj = data.read(Type.V1_6_1_ENTITY_ATTRIBUTES, 1);
                List<EntityAttribute> entityAttributes = new ArrayList<>();

                for (Map.Entry<String, Double> entry : attrObj.getAttributes().entrySet()) {
                    entityAttributes.add(new EntityAttribute(entry.getKey(), entry.getValue()));
                }

                V1_6_2EntityAttributes attrObjModern = new V1_6_2EntityAttributes(entityAttributes);

                return PacketUtil.createPacket(0x2C, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_6_2_ENTITY_ATTRIBUTES, attrObjModern)
                });
            }
        });

        // kick disconnect
        addTranslator(0xFF, PacketDirection.TO_CLIENT, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                if (session.getUserData().getPreNettyProtocolState() != PreNettyProtocolState.STATUS)
                    return data;

                String reason = data.read(Type.STRING, 0);

                ServerMotd pingMessage = ServerMotd.deserialize(reason);

                pingMessage.setVersionName("1.6.2");
                pingMessage.setProtocol(session.getUserData().getClientVersion().getRegistryId());

                return PacketUtil.createPacket(0xFF, new TypeHolder[]{
                        set(Type.STRING, ServerMotd.serialize(pingMessage))
                });
            }
        });

        // creative item get
        addTranslator(0x6B, ProtocolState.PLAY, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                ItemStack item = data.read(Type.V1_3R_ITEM, 1);

                boolean notNull = item != null;

                if (notNull && !creativeTab.exists(item.getItemId())) {
                    // replace all unknown items to stone
                    item.setItemId(1);
                    item.setData(0);
                }

                return PacketUtil.createPacket(0x6B, new TypeHolder[]{
                        data.read(0),
                        set(Type.V1_3R_ITEM, item)
                });
            }
        });

        // block place
        addTranslator(0x0F, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                int x = data.read(Type.INT, 0);
                int y = data.read(Type.UNSIGNED_BYTE, 1);
                int z = data.read(Type.INT, 2);

                ItemStack item = data.read(Type.V1_3R_ITEM, 4);

                if (item == null) return data;
                int itemId = item.getItemId();

                int face = data.read(Type.BYTE, 3);

                if (face == 1) {
                    ++y;
                } else if (face == 2) {
                    --z;
                } else if (face == 3) {
                    ++z;
                } else if (face == 4) {
                    --x;
                } else if (face == 5) {
                    ++x;
                }

                if (itemId == 323 && data.read(Type.SHORT, 5) > 0) {
                    PacketData signEditor = PacketUtil.createPacket(0x85, new TypeHolder[]{
                            set(Type.BYTE, (byte) 0), // ignored
                            set(Type.INT, x), // x
                            set(Type.INT, y), // y
                            set(Type.INT, z), // z
                    });

                    session.sendPacket(signEditor, PacketDirection.TO_CLIENT, getFrom());
                }
                return data;
            }
        });
    }
}
