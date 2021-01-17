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

package com.github.dirtpowered.dirtmv;

import com.github.dirtpowered.dirtmv.api.Configuration;
import com.github.dirtpowered.dirtmv.config.YamlConfig;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_4.V1_4BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_5.V1_5BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_7.V1_7BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_8.V1_8BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_0.V1_0RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_1.V1_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_2.V1_2_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3.V1_3_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_4.V1_4_6RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_5.V1_5RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_6.V1_6RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_6.V1_6_2RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7.V1_7_2RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7.V1_7_6RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_8.V1_8RProtocol;
import com.github.dirtpowered.dirtmv.data.registry.ProtocolRegistry;
import com.github.dirtpowered.dirtmv.data.registry.TranslatorRegistry;
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.github.dirtpowered.dirtmv.network.server.Server;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta10To9.ProtocolBeta10To9;
import com.github.dirtpowered.dirtmv.network.versions.Beta11To10.ProtocolBeta11To10;
import com.github.dirtpowered.dirtmv.network.versions.Beta13To11.ProtocolBeta13To11;
import com.github.dirtpowered.dirtmv.network.versions.Beta14To13.ProtocolBeta14To13;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.ProtocolBeta17to14;
import com.github.dirtpowered.dirtmv.network.versions.Release22To17.ProtocolRelease22To17;
import com.github.dirtpowered.dirtmv.network.versions.Release23To22.ProtocolRelease23To22;
import com.github.dirtpowered.dirtmv.network.versions.Release28To23.ProtocolRelease28To23;
import com.github.dirtpowered.dirtmv.network.versions.Release29To28.ProtocolRelease29To28;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.ProtocolRelease39To29;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.ProtocolRelease47To5;
import com.github.dirtpowered.dirtmv.network.versions.Release4To78.ProtocolRelease4To78;
import com.github.dirtpowered.dirtmv.network.versions.Release51To39.ProtocolRelease51To39;
import com.github.dirtpowered.dirtmv.network.versions.Release5To4.ProtocolRelease5To4;
import com.github.dirtpowered.dirtmv.network.versions.Release60To51.ProtocolRelease60To51;
import com.github.dirtpowered.dirtmv.network.versions.Release73To61.ProtocolRelease73To61;
import com.github.dirtpowered.dirtmv.network.versions.Release74To73.ProtocolRelease74To73;
import com.github.dirtpowered.dirtmv.network.versions.Release78To74.ProtocolRelease78To74;
import com.github.dirtpowered.dirtmv.session.MultiSession;
import com.github.dirtpowered.dirtmv.session.SessionRegistry;
import com.github.dirtpowered.dirtmv.viaversion.ViaPlugin;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import org.pmw.tinylog.Logger;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class DirtMultiVersion implements Runnable {
    private final ExecutorService executorService;
    private final SessionRegistry sessionRegistry;
    private final TranslatorRegistry translatorRegistry;
    private final Random sharedRandom;
    private final Configuration configuration;
    private final EventLoopGroup loopGroup;
    private final Server server;
    private ViaPlugin viaPlugin;

    private DirtMultiVersion() {
        sharedRandom = new Random();
        loopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        executorService = Executors.newCachedThreadPool();
        configuration = new YamlConfig();
        translatorRegistry = new TranslatorRegistry(this);
        server = new Server(this);
        sessionRegistry = new SessionRegistry();

        // register supported protocols
        ProtocolRegistry.registerProtocol(MinecraftVersion.B1_3, new V1_3BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.B1_4, new V1_4BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.B1_5, new V1_5BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.B1_6_6, new V1_7BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.B1_7_3, new V1_7BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.B1_8_1, new V1_8BProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_0, new V1_0RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_1, new V1_1RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_2_1, new V1_2_1RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_2_4, new V1_2_1RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_3_1, new V1_3_1RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_4_6, new V1_4_6RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_5_1, new V1_5RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_5_2, new V1_5RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_6_1, new V1_6RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_6_2, new V1_6_2RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_6_4, new V1_6_2RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_7_2, new V1_7_2RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_7_6, new V1_7_6RProtocol());
        ProtocolRegistry.registerProtocol(MinecraftVersion.R1_8, new V1_8RProtocol());

        // register protocol translators
        translatorRegistry.registerTranslator(new ProtocolRelease47To5());
        translatorRegistry.registerTranslator(new ProtocolRelease5To4());
        translatorRegistry.registerTranslator(new ProtocolRelease4To78());
        translatorRegistry.registerTranslator(new ProtocolRelease78To74());
        translatorRegistry.registerTranslator(new ProtocolRelease74To73());
        translatorRegistry.registerTranslator(new ProtocolRelease73To61());
        translatorRegistry.registerTranslator(new ProtocolRelease60To51());
        translatorRegistry.registerTranslator(new ProtocolRelease51To39());
        translatorRegistry.registerTranslator(new ProtocolRelease39To29());
        translatorRegistry.registerTranslator(new ProtocolRelease29To28());
        translatorRegistry.registerTranslator(new ProtocolRelease28To23());
        translatorRegistry.registerTranslator(new ProtocolRelease22To17());
        translatorRegistry.registerTranslator(new ProtocolRelease23To22());
        translatorRegistry.registerTranslator(new ProtocolBeta17to14());
        translatorRegistry.registerTranslator(new ProtocolBeta14To13());
        translatorRegistry.registerTranslator(new ProtocolBeta13To11());
        translatorRegistry.registerTranslator(new ProtocolBeta11To10());
        translatorRegistry.registerTranslator(new ProtocolBeta10To9());

        // start tick-loop
        setupGlobalTask();

        if (configuration.enableViaVersion()) {
            Logger.info("Loading ViaVersion ...");
            this.viaPlugin = new ViaPlugin(server);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // disconnect all players
            sessionRegistry.getSessions().forEach((uuid, multiSession) -> {
                ServerSession session = multiSession.getServerSession();
                if (session != null) {
                    session.disconnect(ChatUtils.LEGACY_COLOR_CHAR + "cDisconnected");
                }
            });

            // release loop group
            loopGroup.shutdownGracefully();
            server.stop();

            Logger.info("bye!");
        }));

        server.bind();
    }

    private void setupGlobalTask() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Main Thread"));
        executor.scheduleAtFixedRate(this, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    public static void main(String... args) {
        new DirtMultiVersion();
    }

    @Override
    public void run() {
        try {
            if (this.viaPlugin != null) {
                this.viaPlugin.tick();
            }
            for (MultiSession val : sessionRegistry.getSessions().values()) {
                val.getServerSession().tick();
            }
        } catch (Exception e) {
            Logger.error("Exception in tick loop: {}", e.getMessage());
        }
    }
}
