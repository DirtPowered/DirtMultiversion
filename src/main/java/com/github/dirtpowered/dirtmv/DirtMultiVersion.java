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

package com.github.dirtpowered.dirtmv;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.handler.registry.TranslatorRegistry;
import com.github.dirtpowered.dirtmv.network.packet.protocol.ProtocolRegistry;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.V1_7BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_8.V1_8BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_0.V1_0RProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_1.V1_1RProtocol;
import com.github.dirtpowered.dirtmv.network.server.Server;
import com.github.dirtpowered.dirtmv.session.SessionRegistry;
import lombok.Getter;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class DirtMultiVersion {
    private final ScheduledExecutorService scheduledExecutorService;
    private SessionRegistry sessionRegistry;
    private TranslatorRegistry translatorRegistry;
    private Random sharedRandom;

    private DirtMultiVersion() {
        V1_7BProtocol v1_7BProtocol = new V1_7BProtocol();
        v1_7BProtocol.registerPackets();

        V1_8BProtocol v1_8BProtocol = new V1_8BProtocol();
        v1_8BProtocol.registerPackets();

        V1_0RProtocol v1_0RProtocol = new V1_0RProtocol();
        v1_0RProtocol.registerPackets();

        V1_1RProtocol v1_1RProtocol = new V1_1RProtocol();
        v1_1RProtocol.registerPackets();

        ProtocolRegistry.registerProtocol(MinecraftVersion.B_1_6_6, v1_7BProtocol);
        ProtocolRegistry.registerProtocol(MinecraftVersion.B_1_7_3, v1_7BProtocol);
        ProtocolRegistry.registerProtocol(MinecraftVersion.B_1_8_1, v1_8BProtocol);
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_0, v1_0RProtocol);
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_1, v1_1RProtocol);

        translatorRegistry = new TranslatorRegistry();
        sessionRegistry = new SessionRegistry();
        scheduledExecutorService = Executors.newScheduledThreadPool(32);

        sharedRandom = new Random();

        new Server(this);
    }

    public static void main(String... args) {
        new DirtMultiVersion();
    }
}
