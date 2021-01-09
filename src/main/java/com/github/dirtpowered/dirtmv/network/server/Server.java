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

package com.github.dirtpowered.dirtmv.network.server;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.config.Configuration;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.network.server.codec.ChannelConstants;
import com.github.dirtpowered.dirtmv.network.server.codec.ConnectionLimiterHandler;
import com.github.dirtpowered.dirtmv.network.server.codec.PipelineFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.pmw.tinylog.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class Server {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final DirtMultiVersion main;
    private final Server instance;

    @Getter
    private String serverIcon;

    public Server(DirtMultiVersion main) {
        this.main = main;
        this.instance = this;

        setupServerIcon();
    }

    public void bind() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) {
                        ServerSession serverSession = new ServerSession(channel, main, instance);
                        channel.pipeline().addFirst(ChannelConstants.CONNECTION_THROTTLE, new ConnectionLimiterHandler(main.getConfiguration()));
                        channel.pipeline().addLast(ChannelConstants.DEFAULT_PIPELINE, new PipelineFactory(
                                main, serverSession.getUserData(), PacketDirection.CLIENT_TO_SERVER))
                                .addLast(ChannelConstants.SERVER_HANDLER, serverSession);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future;
        try {
            Configuration c = main.getConfiguration();

            future = b.bind(c.getBindAddress(), c.getBindPort()).sync();
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

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Base64OutputStream base64Stream = new Base64OutputStream(outputStream);
                ImageIO.write(image, "png", base64Stream);

                this.serverIcon = MessageFormat.format("data:image/png;base64,{0}", outputStream.toString("UTF-8"));
            } catch (IOException e) {
                Logger.warn("couldn't read {}", file.getName());
            }
        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        main.getSessionRegistry().getSessions().clear();
    }
}
