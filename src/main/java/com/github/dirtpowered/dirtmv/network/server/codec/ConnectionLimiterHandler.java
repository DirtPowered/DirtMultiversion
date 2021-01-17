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

package com.github.dirtpowered.dirtmv.network.server.codec;

import com.github.dirtpowered.dirtmv.api.Configuration;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class ConnectionLimiterHandler extends ChannelInboundHandlerAdapter {
    private static final Map<String, Long> connections = new HashMap<>();
    private static final AtomicInteger connectionCounter = new AtomicInteger();
    private final Configuration config;

    public ConnectionLimiterHandler(Configuration configuration) {
        this.config = configuration;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int count = connectionCounter.incrementAndGet();

        if (count > config.getMaxConnections()) {
            ctx.close();
            return;
        }

        InetSocketAddress isa = (InetSocketAddress) ctx.channel().remoteAddress();
        String address = isa.getAddress().getHostAddress();

        if (connections.containsKey(address)) {
            if (System.currentTimeMillis() - connections.get(address) < 350L) {
                connections.put(address, System.currentTimeMillis());

                ctx.close();
                return;
            }
        }

        connections.put(address, System.currentTimeMillis());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        connectionCounter.decrementAndGet();
    }
}

