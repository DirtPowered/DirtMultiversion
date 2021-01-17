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

package com.github.dirtpowered.dirtmv.viaversion;

import com.github.dirtpowered.dirtmv.api.DirtServer;
import com.github.dirtpowered.dirtmv.data.interfaces.Tickable;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.viaversion.config.ViaConfig;
import com.github.dirtpowered.dirtmv.viaversion.platform.DummyInjector;
import com.github.dirtpowered.dirtmv.viaversion.providers.IdleMovementProvider;
import com.github.dirtpowered.dirtmv.viaversion.providers.ViaHandItemProvider;
import com.github.dirtpowered.dirtmv.viaversion.util.WrappedLogger;
import com.google.gson.JsonObject;
import us.myles.ViaVersion.ViaManager;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.ViaVersionConfig;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.platform.TaskId;
import us.myles.ViaVersion.api.platform.ViaConnectionManager;
import us.myles.ViaVersion.api.platform.ViaPlatform;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.protocols.base.VersionProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.MovementTracker;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

public class ViaPlugin implements ViaPlatform<DirtServer>, Tickable {
    private final Logger wrappedLogger;
    private final ViaConnectionManager connectionManager;
    private final DirtServer api;
    private final ViaConfig config;

    public ViaPlugin(DirtServer server) {
        this.wrappedLogger = new WrappedLogger();
        this.connectionManager = new ViaConnectionManager();
        this.api = server;
        this.config = new ViaConfig();

        Via.init(ViaManager.builder()
                .platform(this)
                .injector(new DummyInjector())
                .build());

        Via.getManager().init();
        Via.getManager().getProviders().use(VersionProvider.class, new VersionProvider() {
            @Override
            public int getServerProtocol(UserConnection connection) {
                return ProtocolVersion.v1_8.getVersion();
            }
        });

        Via.getManager().getProviders().use(HandItemProvider.class, new ViaHandItemProvider(server));
        Via.getManager().getProviders().use(MovementTransmitterProvider.class, new IdleMovementProvider());
    }

    @Override
    public void tick() {
        for (UserConnection info : Via.getManager().getConnections()) {
            ProtocolInfo protocolInfo = info.getProtocolInfo();
            if (protocolInfo == null || !protocolInfo.getPipeline().contains(Protocol1_9To1_8.class))
                continue;

            MovementTracker movementTracker = info.get(MovementTracker.class);
            if (movementTracker == null) continue;

            long nextIdleUpdate = movementTracker.getNextIdlePacket();

            if (info.getChannel() != null && info.getChannel().isOpen()) {
                if (nextIdleUpdate <= System.currentTimeMillis()) {
                    MovementTransmitterProvider idle = Via.getManager().getProviders().get(MovementTransmitterProvider.class);
                    if (idle != null) {
                        UserData data = api.getUserDataFromUsername(protocolInfo.getUsername());
                        if (data != null) {
                            boolean isNetty = api.getConfiguration().getServerVersion().isNettyProtocol();

                            if (isNetty || data.getPreNettyProtocolState() == PreNettyProtocolState.IN_GAME) {
                                idle.sendPlayer(info);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPlatformName() {
        return api.getName();
    }

    @Override
    public String getPlatformVersion() {
        return api.getVersion();
    }

    @Override
    public String getPluginVersion() {
        return "1.0";
    }

    @Override
    public Logger getLogger() {
        return wrappedLogger;
    }

    @Override
    public boolean isPluginEnabled() {
        return true;
    }

    @Override
    public ViaVersionConfig getConf() {
        return config;
    }

    @Override
    public ConfigurationProvider getConfigurationProvider() {
        return config;
    }

    @Override
    public TaskId runAsync(Runnable runnable) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public TaskId runSync(Runnable runnable) {
        //throw new UnsupportedOperationException("not supported yet");
        return null;
    }

    @Override
    public TaskId runSync(Runnable runnable, Long aLong) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public TaskId runRepeatingSync(Runnable runnable, Long aLong) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public void cancelTask(TaskId taskId) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public ViaAPI<DirtServer> getApi() {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public JsonObject getDump() {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public void sendMessage(UUID uuid, String s) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public boolean isOldClientsAllowed() {
        return true;
    }

    @Override
    public ViaConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public boolean isProxy() {
        return true;
    }

    @Override
    public File getDataFolder() {
        return new File("ViaVersion");
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        return new ViaCommandSender[0];
    }

    @Override
    public void onReload() {

    }
}