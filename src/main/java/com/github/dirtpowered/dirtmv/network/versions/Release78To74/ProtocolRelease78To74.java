package com.github.dirtpowered.dirtmv.network.versions.Release78To74;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.ping.ServerMotd;

public class ProtocolRelease78To74 extends ServerProtocol {

    public ProtocolRelease78To74() {
        super(MinecraftVersion.R1_6_4, MinecraftVersion.R1_6_2);
    }

    @Override
    public void registerTranslators() {
        // handshake
        addTranslator(0x02, PacketDirection.TO_SERVER, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {

                return PacketUtil.createPacket(0x02, new TypeHolder[]{
                        set(Type.BYTE, (byte) 74),
                        data.read(1),
                        data.read(2),
                        data.read(3)
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

                pingMessage.setVersionName("1.6.4");
                pingMessage.setProtocol(session.getUserData().getClientVersion().getRegistryId());

                return PacketUtil.createPacket(0xFF, new TypeHolder[]{
                        set(Type.STRING, ServerMotd.serialize(pingMessage))
                });
            }
        });
    }
}
