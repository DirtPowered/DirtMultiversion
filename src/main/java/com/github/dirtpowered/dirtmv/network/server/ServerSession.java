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

package com.github.dirtpowered.dirtmv.network.server;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.interfaces.Tickable;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.data.utils.other.PreNettyPacketNames;
import com.github.dirtpowered.dirtmv.network.client.Client;
import com.github.dirtpowered.dirtmv.network.client.ClientSession;
import com.github.dirtpowered.dirtmv.session.MultiSession;
import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Server to Client connection session
 */
@Log4j2
public class ServerSession extends SimpleChannelInboundHandler<PacketData> implements Tickable {

    @Getter
    private DirtMultiVersion main;

    @Getter
    private SocketChannel channel;

    @Getter
    private UserData userData;

    private Client client;
    private UUID key;

    private Queue<PacketData> initialPacketQueue = new LinkedBlockingQueue<>();
    private Queue<QueuedPacket> packetQueue = new LinkedBlockingQueue<>();
    private AtomicInteger packetCounter = new AtomicInteger();

    private int currentTick = 0;

    ServerSession(SocketChannel channel, DirtMultiVersion server) {
        this.key = UUID.randomUUID();

        this.userData = new UserData();
        this.channel = channel;
        this.main = server;
        this.client = new Client(this);
    }

    /**
     * Translates and sends packet to server
     *
     * @param packet    {@link PacketData} Packet
     * @param direction {@link PacketDirection} sending direction (client/server)
     * @param from      Version to start from
     */
    public void sendPacket(PacketData packet, PacketDirection direction, MinecraftVersion from) {
        MinecraftVersion version = main.getConfiguration().getServerVersion();
        if (from != null && from != version) {
            version = from;
        }

        List<ServerProtocol> protocols = main.getTranslatorRegistry().findProtocol(userData, version);

        boolean flag = direction == PacketDirection.SERVER_TO_CLIENT;

        if (!flag) Collections.reverse(protocols);

        PacketData target = packet;

        for (ServerProtocol protocol : protocols) {
            boolean isNetty = userData.getClientVersion().isNettyProtocol();
            ProtocolState state = isNetty ? userData.getProtocolState() : ProtocolState.PRE_NETTY;

            if (!protocol.getFrom().isNettyProtocol()) {
                state = ProtocolState.PRE_NETTY;
            }

            if (target == null) {
                return;
            }

            PacketTranslator translator = protocol.getTranslatorFor(target.getOpCode(), state, direction);

            String protocolName = protocol.getClass().getSimpleName();

            if (translator != null) {
                if (from == null || from != protocol.getFrom()) {
                    try {
                        target = translator.translate(this, target);
                    } catch (IOException e) {
                        disconnect(e.getMessage());
                    }

                    String namedOpCode = PreNettyPacketNames.getPacketName(packet.getOpCode());
                    Preconditions.checkNotNull(target, "%s returned null while translating %s", protocolName, namedOpCode);

                    boolean debug = main.getConfiguration().isDebugMode();

                    if (target.getOpCode() == -1) {
                        if (state == ProtocolState.PRE_NETTY && debug) {
                            log.debug("cancelling {} | direction: {} | through {}", namedOpCode, direction.name(), protocolName);
                        }
                        return;
                    }
                    if (state == ProtocolState.PRE_NETTY && debug) {
                        log.debug("translating {} | direction: {} | through {}", namedOpCode, direction.name(), protocolName);
                    }
                }
            }
        }

        if (flag) {
            sendPacket(target);
        } else {
            ClientSession clientSession = getClientSession();

            if (target.getOpCode() == 2 /* handshake */ && !hasServerPingProtocol()) {
                connectToServer();
            }

            if (clientSession != null && clientSession.getChannel().isActive()) {
                clientSession.sendPacket(target);
                return;
            }

            initialPacketQueue.add(target);
        }
    }

    private void handleQueuedPackets() {
        if (!packetQueue.isEmpty()) {
            QueuedPacket queuedPacket = packetQueue.poll();
            sendPacket(queuedPacket.getPacket(), queuedPacket.getDirection(), queuedPacket.getVersion());
        }
    }

    public void queuePacket(PacketData packet, PacketDirection direction, MinecraftVersion version) {
        packetQueue.add(new QueuedPacket(packet, direction, version));
    }

    @Override
    public void tick() {
        handleQueuedPackets();
        for (Object o : userData.getProtocolStorage().getSavedObjects().values()) {
            if (o instanceof Tickable) {
                ((Tickable) o).tick();
            }
        }

        if (currentTick % 20 == 0) {
            if (packetCounter.get() >= main.getConfiguration().getMaxPacketsPerSecond()) {
                disconnect("You're sending too many packets. Lag?");
            }

            packetCounter.set(0);
        }
        currentTick++;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PacketData packetData) throws IOException {
        // client to server packets
        sendPacket(packetData, PacketDirection.CLIENT_TO_SERVER, null);
        packetCounter.getAndIncrement();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (hasServerPingProtocol()) {
            connectToServer();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        disconnect();

        // notify other sessions
        for (ServerProtocol t : main.getTranslatorRegistry().getProtocols().values()) {
            t.onDisconnect(this);
        }

        ClientSession clientSession = getClientSession();
        if (clientSession != null) {
            clientSession.disconnectRemote();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // disconnect from proxy
        disconnect(cause.getMessage());
    }

    @SneakyThrows
    public void broadcastPacket(PacketData packetData, MinecraftVersion from) {
        for (MultiSession entry : main.getSessionRegistry().getSessions().values()) {
            if (entry != null) {
                ServerSession serverSession = entry.getServerSession();
                if (!(serverSession.userData.getClientVersion().getRegistryId() >= from.getRegistryId())) {
                    return;
                }

                serverSession.sendPacket(packetData, PacketDirection.SERVER_TO_CLIENT, from);
            }
        }
    }

    private void sendPacket(PacketData packetData) {
        channel.writeAndFlush(packetData);
    }

    public ClientSession getClientSession() {
        if (main.getSessionRegistry().getSession(key) == null)
            return null;

        return main.getSessionRegistry().getSession(key).getClientSession();
    }

    public void disconnect(String message) {
        if (message == null || message.isEmpty()) {
            channel.close();
        } else {
            log.warn("[{}] disconnected with message: {}", getLogTag(), message);
            sendDisconnectPacket(message);
        }

        initialPacketQueue.clear();
        packetQueue.clear();
    }

    private void sendDisconnectPacket(String message) {
        PacketData kickPacket;

        if (userData.getClientVersion().isNettyProtocol()) {
            boolean state = userData.getProtocolState() == ProtocolState.LOGIN;

            kickPacket = PacketUtil.createPacket(state ? 0x00 : 0x40, new TypeHolder[]{
                    new TypeHolder(Type.V1_7_STRING, ChatUtils.legacyToJsonString(message)),
            });
        } else {
            kickPacket = PacketUtil.createPacket(0xFF, new TypeHolder[]{
                    new TypeHolder(Type.STRING, message),
            });
        }

        sendPacket(kickPacket);
    }

    public void disconnect() {
        disconnect(StringUtil.EMPTY_STRING);
    }


    private String getAddress() {
        return channel.localAddress().toString();
    }

    public String getLogTag() {
        String tag = getAddress();
        if (userData.getUsername() != null) {
            tag = userData.getUsername();
        }

        return tag;
    }

    private void connectToServer() {
        if (getClientSession() == null) {
            client.createClient(key, () -> {
                while (!initialPacketQueue.isEmpty()) {
                    sendPacket(initialPacketQueue.poll(), PacketDirection.CLIENT_TO_SERVER, null);
                }
            });
        }
    }

    private boolean hasServerPingProtocol() {
        return main.getConfiguration().getServerVersion().getRegistryId() > 17;
    }

    public int getConnectionCount() {
        return main.getSessionRegistry().getSessions().size();
    }

    @Data
    @AllArgsConstructor
    private final static class QueuedPacket {
        private PacketData packet;
        private PacketDirection direction;
        private MinecraftVersion version;
    }
}
