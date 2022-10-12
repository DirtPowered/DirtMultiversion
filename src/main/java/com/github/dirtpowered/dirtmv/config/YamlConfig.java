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

package com.github.dirtpowered.dirtmv.config;

import com.github.dirtpowered.dirtmv.api.Configuration;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import org.pmw.tinylog.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class YamlConfig implements Configuration {
    private Map<String, Object> objects;

    public YamlConfig() {
        File file = new File("config.yml");
        try {
            if (!file.exists()) {
                Files.copy(YamlConfig.class.getResourceAsStream("/config.yml"), file.toPath());
            }
            this.objects = new Yaml().load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBindAddress() {
        return (String) objects.getOrDefault("bind_address", "localhost");
    }

    @Override
    public int getBindPort() {
        return (int) objects.getOrDefault("bind_port", 25565);
    }

    @Override
    public String getRemoteServerAddress() {
        return (String) objects.getOrDefault("remote_address", "localhost");
    }

    @Override
    public void setRemoteServerAddress(String address) {
        // empty
    }

    @Override
    public int getRemoteServerPort() {
        return (int) objects.getOrDefault("remote_port", 25565);
    }

    @Override
    public void setRemoteServerPort(int port) {
        // empty
    }

    @Override
    public MinecraftVersion getServerVersion() {
        String val = (String) objects.getOrDefault("server_version", "B1_7_3");
        MinecraftVersion version = null;

        try {
            version = MinecraftVersion.valueOf(val);
        } catch (IllegalArgumentException e) {
            Logger.error("invalid server version {}, stopping", val);
            System.exit(0);
        }
        return version;
    }

    @Override
    public void setServerVersion(MinecraftVersion minecraftVersion) {
        // empty
    }

    @Override
    public String preReleaseMOTD() {
        return (String) objects.getOrDefault("prerelease_motd", "A Minecraft Server");
    }

    @Override
    public int getMaxOnline() {
        return (int) objects.getOrDefault("prerelease_motd_max_online", 20);
    }

    @Override
    public boolean isDebugMode() {
        return false;
    }

    @Override
    public boolean reduceBlockStorageMemory() {
        return (Boolean) objects.getOrDefault("reduce_blockstorage_memory", false);
    }

    @Override
    public int getMaxConnections() {
        return (int) objects.getOrDefault("max_proxy_connections", 20);
    }

    @Override
    public int getMaxPacketsPerSecond() {
        return (int) objects.getOrDefault("max_packets_per_second", 20);
    }

    @Override
    public boolean replaceChests() {
        return (boolean) objects.getOrDefault("replace_chests", true);
    }

    @Override
    public boolean enableViaVersion() {
        return (boolean) objects.getOrDefault("viaversion_support", false);
    }

    @Override
    public int getCompressionThreshold() {
        return (int) objects.getOrDefault("compression_threshold", 256);
    }

    @Override
    public boolean enableCommandLogging() {
        return (boolean) objects.getOrDefault("enable_command_logging", false);
    }

    @Override
    public int getConnectionThrottleTime() {
        return (int) objects.getOrDefault("connection_throttle", 350);
    }
}