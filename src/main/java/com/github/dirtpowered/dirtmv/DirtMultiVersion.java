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
import com.github.dirtpowered.dirtmv.network.data.registry.TranslatorRegistry;
import com.github.dirtpowered.dirtmv.network.packet.protocol.ProtocolRegistry;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.V1_7BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_8.V1_8BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_0.V1_0RProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_1.V1_1RProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_2_1.V1_2_1RProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_3_1.V1_3_1RProtocol;
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
        ProtocolRegistry.registerProtocol(MinecraftVersion.B_1_6_6, new V1_7BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.B_1_7_3, new V1_7BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.B_1_8_1, new V1_8BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_0, new V1_0RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_1, new V1_1RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_2_1, new V1_2_1RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_2_4, new V1_2_1RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_3_1, new V1_3_1RProtocol());

        translatorRegistry = new TranslatorRegistry();
        sessionRegistry = new SessionRegistry();
        scheduledExecutorService = Executors.newScheduledThreadPool(32);

        sharedRandom = new Random();

        System.out.println("(DirtMultiVersion) Started on IP: " + Configuration.proxyLocalAddress + ":" + Configuration.proxyLocalPort);
        new Server(this);
    }

    public static void main(String... args) {
        new DirtMultiVersion();
    }
}
