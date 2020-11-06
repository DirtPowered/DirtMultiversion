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

package com.github.dirtpowered.dirtmv.network.server.codec;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.network.server.codec.netty.DetectionHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class PipelineFactory extends ChannelInitializer {

    private static final int TIMEOUT = 30;

    private DirtMultiVersion main;
    private PacketDirection packetDirection;
    private UserData userData;

    public PipelineFactory(DirtMultiVersion main, UserData userData, PacketDirection packetDirection) {
        this.main = main;
        this.packetDirection = packetDirection;
        this.userData = userData;
    }

    @Override
    protected void initChannel(Channel channel) {
        if (packetDirection == PacketDirection.CLIENT_TO_SERVER) {
            channel.pipeline()
                    .addFirst(ChannelConstants.DETECTION_HANDLER, new DetectionHandler(main, userData));
        } else {
            channel.pipeline()
                    .addLast(ChannelConstants.LEGACY_DECODER, new PacketDecoder(main, packetDirection, userData))
                    .addLast(ChannelConstants.LEGACY_ENCODER, new PacketEncoder());
        }

        channel.pipeline()
                .addLast(ChannelConstants.TIMEOUT_HANDLER, new ReadTimeoutHandler(TIMEOUT));
    }
}