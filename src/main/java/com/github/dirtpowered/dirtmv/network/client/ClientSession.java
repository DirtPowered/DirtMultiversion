/*
 * Copyright (c) 2020-2021 Dirt Powered
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

package com.github.dirtpowered.dirtmv.network.client;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.interfaces.Callback;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.session.MultiSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import org.pmw.tinylog.Logger;

import java.util.UUID;

/**
 * Client to Server connection session
 */
public class ClientSession extends SimpleChannelInboundHandler<PacketData> {
    private final ServerSession serverSession;

    @Getter
    private final SocketChannel channel;

    private final UUID key;

    private final Callback callback;
    private final DirtMultiVersion main;

    private boolean stateLock;

    ClientSession(DirtMultiVersion main, UUID key, ServerSession serverSession, SocketChannel ch, Callback callback) {
        this.main = main;
        this.serverSession = serverSession;
        this.channel = ch;
        this.key = key;
        this.callback = callback;
        this.stateLock = false;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PacketData packetData) {
        // server to client packets
        serverSession.sendPacket(packetData, PacketDirection.TO_CLIENT, null);

        // notify other sessions
        if (!stateLock) {
            boolean postNettyFlag = serverSession.getUserData().getProtocolState() == ProtocolState.PLAY;
            boolean preNettyFlag = serverSession.getUserData().getPreNettyProtocolState() == PreNettyProtocolState.IN_GAME;

            if (preNettyFlag || postNettyFlag) {
                MinecraftVersion server = main.getConfiguration().getServerVersion();
                MinecraftVersion client = serverSession.getUserData().getClientVersion();

                // call #onConnect only in user protocols
                main.getTranslatorRegistry().getAllProtocolsBetween(server, client).forEach(t -> t.onConnect(serverSession));
                stateLock = true;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Logger.info("[{}] connected to remote server", serverSession.getLogTag());
        serverSession.getMain().getSessionRegistry().addSession(key, new MultiSession(this, serverSession));

        callback.onComplete();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Logger.info("[{}] disconnected from remote server", serverSession.getLogTag());
        serverSession.getMain().getSessionRegistry().removeSession(key);
        channel.close();

        // disconnect from proxy
        serverSession.disconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        // disconnect from proxy
        serverSession.disconnect(cause.getMessage());
    }

    /**
     * Sends raw packet to server
     *
     * @param packetData {@link PacketData} Packet
     */
    public void sendPacket(PacketData packetData) {
        channel.writeAndFlush(packetData);
    }

    /**
     * Closing remote connection
     */
    public void disconnectRemote() {
        channel.close();
    }
}
