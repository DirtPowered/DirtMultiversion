package com.github.dirtpowered.dirtmv.config;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Log4j2
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
    public int getRemoteServerPort() {
        return (int) objects.getOrDefault("remote_port", 25565);
    }

    @Override
    public MinecraftVersion getServerVersion() {
        String val = (String) objects.getOrDefault("server_version", "B1_7_3");
        MinecraftVersion version = null;

        try {
            version = MinecraftVersion.valueOf(val);
        } catch (IllegalArgumentException e) {
            log.error("invalid server version {}, stopping", val);
            System.exit(0);
        }
        return version;
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
    public String getServerIcon() {
        return (String) objects.getOrDefault("server-icon", "");
    }

    @Override
    public boolean isDebugMode() {
        return false;
    }

    @Override
    public boolean reduceBlockStorageMemory() {
        return (Boolean) objects.getOrDefault("reduce_blockstorage_memory", true);
    }

    @Override
    public int getMaxConnections() {
        return (int) objects.getOrDefault("max_proxy_connections", 20);
    }

    @Override
    public int getMaxPacketsPerSecond() {
        return (int) objects.getOrDefault("max_packets_per_second", 20);
    }
}