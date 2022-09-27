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

package com.github.dirtpowered.dirtmv.viaversion;

import com.github.dirtpowered.dirtmv.api.DirtServer;
import com.github.dirtpowered.dirtmv.viaversion.config.ViaConfigImpl;
import com.github.dirtpowered.dirtmv.viaversion.platform.DirtViaApi;
import com.github.dirtpowered.dirtmv.viaversion.platform.DummyInjector;
import com.github.dirtpowered.dirtmv.viaversion.providers.ViaHandItemProvider;
import com.github.dirtpowered.dirtmv.viaversion.util.WrappedLogger;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

public class ViaPlugin implements ViaPlatform<DirtServer> {
    private final Logger wrappedLogger;
    private final DirtServer api;
    private final ViaConfigImpl config;
    private final ViaAPI<DirtServer> viaAPI;

    public ViaPlugin(DirtServer server) {
        this.wrappedLogger = new WrappedLogger();
        this.api = server;
        this.viaAPI = new DirtViaApi();
        this.config = new ViaConfigImpl();

        Via.init(ViaManagerImpl.builder()
                .platform(this)
                .injector(new DummyInjector())
                .build());

        ((ViaManagerImpl) Via.getManager()).init();

        Via.getManager().getProviders().use(VersionProvider.class, userConnection -> ProtocolVersion.v1_8.getVersion());
        Via.getManager().getProviders().use(HandItemProvider.class, new ViaHandItemProvider(server));
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
    public PlatformTask<?> runAsync(Runnable runnable) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public PlatformTask<?> runSync(Runnable runnable) {
        //throw new UnsupportedOperationException("not supported yet");
        return null;
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
    public PlatformTask<?> runSync(Runnable runnable, long l) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public PlatformTask<?> runRepeatingSync(Runnable runnable, long l) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public ViaAPI<DirtServer> getApi() {
        return viaAPI;
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
    public boolean hasPlugin(String s) {
        return false;
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

    @Override
    public JsonObject getDump() {
        return null;
    }
}