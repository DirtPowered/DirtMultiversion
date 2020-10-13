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

package com.github.dirtpowered.dirtmv.config;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import lombok.Getter;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class Configuration {
    public static String serverAddress;
    public static int serverPort;
    public static String proxyLocalAddress;
    public static int proxyLocalPort;
    public static MinecraftVersion serverVersion;

    private DirtMultiVersion main;

    public Configuration(DirtMultiVersion main) {
        this.main = main;
    }

    private String fixPath(String path) {
        return path.isEmpty() ? "" : path + "/";
    }

    public void loadConfiguration(String path) {
        YamlFile config = new YamlFile(fixPath(path) + "config.yml");
        try {
            if (config.exists()) {
                System.out.println("Loading configuration file");
                config.load();
            } else {
                Path p = Paths.get("src/main/resources/config.yml");
                if (Files.exists(p)) {
                    Files.copy(p, Paths.get(fixPath(path) + "config.yml"));
                } else {
                    InputStream inputStream = getClass().getResourceAsStream("/config.yml");
                    Files.copy(inputStream, Paths.get(fixPath(path) + "config.yml"));
                }
                config.load();
            }

            try {
                serverVersion = MinecraftVersion.valueOf(config.getString("general.serverVersion"));
            } catch (Exception e) {
                System.out.println("'serverVersion' is wrong, defaulting to B1_5");
                serverVersion = MinecraftVersion.B1_5;
            }

            serverAddress = config.getString("general.serverAddress");
            serverPort = config.getInt("general.serverPort");
            proxyLocalAddress = config.getString("general.proxyLocalAddress");
            proxyLocalPort = config.getInt("general.proxyLocalPort");

            System.out.println("Finished loading configuration!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
