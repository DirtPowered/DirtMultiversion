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
import com.github.dirtpowered.dirtmv.data.Constants;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.network.client.Client;
import com.github.dirtpowered.dirtmv.network.client.ClientSession;
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.utils.PreNettyPacketNames;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.StringUtil;
import lombok.Getter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Server to Client connection session
 */
public class ServerSession extends SimpleChannelInboundHandler<PacketData> {

    @Getter
    private DirtMultiVersion main;

    @Getter
    private SocketChannel channel;

    @Getter
    private UserData userData;

    private Client client;
    private UUID key;

    private Queue<PacketData> initialPacketQueue = new LinkedBlockingQueue<>();

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
     * @param packet         {@link PacketData} Packet
     * @param direction      {@link PacketDirection} sending direction (client/server)
     * @param serverProtocol current protocol class
     */
    public void sendPacket(PacketData packet, PacketDirection direction, Class serverProtocol) throws IOException {
        List<ServerProtocol> protocols = main.getTranslatorRegistry().findProtocol(userData.getClientVersion(), Constants.REMOTE_SERVER_VERSION);
        boolean flag = direction == PacketDirection.SERVER_TO_CLIENT;

        if (!flag) Collections.reverse(protocols);

        PacketData target = packet;

        for (ServerProtocol protocol : protocols) {
            PacketTranslator translator = protocol.getTranslatorFor(target.getOpCode());

            if (translator != null) {
                if (serverProtocol == null || serverProtocol != protocol.getClass()) {
                    target = translator.translate(this, direction, target);

                    String namedOpCode = PreNettyPacketNames.getPacketName(packet.getOpCode());

                    if (target.getOpCode() == -1) {
                        System.out.println("cancelling " + namedOpCode + " " +
                                "| direction: " + direction.name() + " | through " + protocol.getClass().getSimpleName());
                        return;
                    }

                    System.out.println("translating " + namedOpCode + " " +
                            "| direction: " + direction.name() + " | through " + protocol.getClass().getSimpleName());
                }
            }
        }

        if (flag) {
            sendPacket(target);
        } else {
            ClientSession clientSession = getClientSession();

            if (clientSession != null) {
                clientSession.sendPacket(target);
                return;
            }

            initialPacketQueue.add(target);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PacketData packetData) throws IOException {
        // client to server packets
        sendPacket(packetData, PacketDirection.CLIENT_TO_SERVER, null);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        main.getScheduledExecutorService().execute(() -> client.createClient(key, () -> {
            if (!initialPacketQueue.isEmpty()) {
                try {
                    sendPacket(initialPacketQueue.poll(), PacketDirection.CLIENT_TO_SERVER, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        disconnect();

        ClientSession clientSession = getClientSession();
        if (clientSession != null) {
            clientSession.disconnectRemote();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        // disconnect from proxy
        disconnect(cause.getMessage());
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
        if (message.isEmpty()) {
            channel.close();
        } else {
            sendPacket(PacketUtil.createPacket(
                    0xFF,

                    new TypeHolder[]{
                            new TypeHolder(Type.STRING, message),
                    }));
        }

        initialPacketQueue.clear();
    }

    public void disconnect() {
        disconnect(StringUtil.EMPTY_STRING);
    }
}
