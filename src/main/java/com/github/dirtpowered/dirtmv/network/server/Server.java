/*
 * Copyright (c) 2020-2022 Dirt Powered
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
import com.github.dirtpowered.dirtmv.api.Configuration;
import com.github.dirtpowered.dirtmv.api.DirtServer;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.network.server.codec.ChannelConstants;
import com.github.dirtpowered.dirtmv.network.server.codec.ConnectionLimiterHandler;
import com.github.dirtpowered.dirtmv.network.server.codec.PipelineFactory;
import com.github.dirtpowered.dirtmv.session.MultiSession;
import com.google.common.base.Preconditions;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.pmw.tinylog.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Server implements DirtServer {
    private EventLoopGroup bossGroup;

    @Getter
    private final DirtMultiVersion main;

    private final Server instance;
    private String serverIcon;

    public Server(DirtMultiVersion main) {
        this.main = main;
        this.instance = this;

        setupServerIcon();
    }

    public void bind() {
        Class<? extends ServerChannel> socketChannel;

        if (Epoll.isAvailable()) {
            Logger.info("Epoll is available. Using it");
            socketChannel = EpollServerSocketChannel.class;
            bossGroup = new EpollEventLoopGroup();
        } else {
            Logger.warn("Epoll not available, using NIO. Reason: " + Epoll.unavailabilityCause().getMessage());
            socketChannel = NioServerSocketChannel.class;
            bossGroup = new NioEventLoopGroup();
        }

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup)
                .channel(socketChannel)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) {
                        ServerSession serverSession = new ServerSession(channel, main, instance);

                        channel.pipeline().addFirst(
                                "timer", new ReadTimeoutHandler(30)
                        );
                        channel.pipeline().addLast(
                                ChannelConstants.CONNECTION_THROTTLE,
                                new ConnectionLimiterHandler(main.getConfiguration())
                        );
                        channel.pipeline().addLast(
                                ChannelConstants.DEFAULT_PIPELINE,
                                new PipelineFactory(main, serverSession.getUserData(), PacketDirection.TO_SERVER)
                        );
                        channel.pipeline().addLast(
                                ChannelConstants.SERVER_HANDLER,
                                serverSession
                        );
                    }
                })
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future;
        try {
            Configuration c = main.getConfiguration();

            future = b.bind(c.getBindAddress(), c.getBindPort()).sync().addListener(callback -> Logger.info("ready for connections!"));
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Logger.error("address already in use: {}", e.getLocalizedMessage());
            stop();
        }
    }

    private void setupServerIcon() {
        File file = new File("server-icon.png");

        if (!file.exists()) {
            Logger.warn("couldn't find {}", file.getName());
        } else {
            try {
                BufferedImage image = ImageIO.read(file);
                if (image.getHeight() != 64 && image.getWidth() != 64) {
                    Logger.warn("image must be 64x64 pixels");
                    return;
                }

                byte[] bytes = IOUtils.toByteArray(Files.newInputStream(file.toPath()));
                String encodedString = new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8);

                this.serverIcon = MessageFormat.format("data:image/png;base64,{0}", encodedString);
            } catch (IOException e) {
                Logger.warn("couldn't read {}", file.getName());
            }
        }
    }

    @Override
    public String getName() {
        return "DirtMultiVersion";
    }

    @Override
    public String getVersion() {
        String impl = getClass().getPackage().getImplementationVersion();
        return impl == null ? "unknown" : impl;
    }

    @Override
    public Configuration getConfiguration() {
        return main.getConfiguration();
    }

    @Override
    public UserData getUserDataFromUsername(String username) {
        UserData userData = null;

        for (MultiSession entry : main.getSessionRegistry().getSessions().values()) {
            if (entry != null) {
                ServerSession session = entry.getServerSession();
                if (session != null) {
                    UserData data = session.getUserData();

                    if (data.getUsername() != null && data.getUsername().equals(username)) {
                        userData = session.getUserData();
                    }
                }
            }
        }

        return userData;
    }

    @Override
    public List<UserData> getAllConnections() {
        List<UserData> list = new ArrayList<>();

        for (MultiSession entry : main.getSessionRegistry().getSessions().values()) {
            UserData userData = entry.getServerSession().getUserData();
            list.add(userData);
        }
        return list;
    }

    @Override
    public void setRemoteServer(String address, int port) {
        Preconditions.checkNotNull(address, "remote address cannot be null");
        Preconditions.checkArgument(port < 65535, "remote port out of range");

        getConfiguration().setRemoteServerAddress(address);
        getConfiguration().setRemoteServerPort(port);
    }

    @Override
    public void setRemoteVersion(MinecraftVersion version) {
        Preconditions.checkNotNull(version, "version cannot be null");
        getConfiguration().setServerVersion(version);
    }

    @Override
    public String getServerIconBase64() {
        return serverIcon;
    }

    public void stop() {
        this.bossGroup.shutdownGracefully();
        this.main.getSessionRegistry().getSessions().clear();
    }
}