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

import com.github.dirtpowered.dirtmv.config.Configuration;
import com.github.dirtpowered.dirtmv.data.interfaces.Callback;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.server.codec.ChannelConstants;
import com.github.dirtpowered.dirtmv.network.server.codec.PipelineFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.UUID;

public class Client {
    private final ServerSession serverSession;

    public Client(ServerSession serverSession) {
        this.serverSession = serverSession;
    }

    public void createClient(UUID key, Callback callback) {
        EventLoopGroup loopGroup = serverSession.getMain().getLoopGroup();
        Bootstrap clientBootstrap = new Bootstrap();

        clientBootstrap.group(loopGroup);
        clientBootstrap.channel(NioSocketChannel.class);
        clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        clientBootstrap.option(ChannelOption.TCP_NODELAY, true);

        Configuration c = serverSession.getMain().getConfiguration();

        clientBootstrap.remoteAddress(new InetSocketAddress(c.getRemoteServerAddress(), c.getRemoteServerPort()));

        clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(
                        ChannelConstants.DEFAULT_PIPELINE,
                        new PipelineFactory(serverSession.getMain(), serverSession.getUserData(), PacketDirection.TO_CLIENT)
                );
                ch.pipeline().addLast(
                        ChannelConstants.CLIENT_HANDLER,
                        new ClientSession(key, serverSession, ch, callback)
                );
            }
        });

        clientBootstrap.connect().addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                serverSession.disconnect(ChatUtils.LEGACY_COLOR_CHAR + "cUnable to connect to remote server");
                channelFuture.channel().close();
            }
        });
    }
}