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

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;

// TODO: configuration file
public class DefaultConfig implements Configuration {

    @Override
    public String getBindAddress() {
        return "localhost";
    }

    @Override
    public int getBindPort() {
        return 25565;
    }

    @Override
    public String getRemoteServerAddress() {
        return "localhost";
    }

    @Override
    public int getRemoteServerPort() {
        return 25567;
    }

    @Override
    public MinecraftVersion getServerVersion() {
        return MinecraftVersion.B1_7_3;
    }

    @Override
    public String preReleaseMOTD() {
        return "&fA Beta 1.7 Minecraft Server";
    }

    @Override
    public int getMaxOnline() {
        return 60;
    }

    @Override
    public String getServerIcon() {
        return "";
    }

    @Override
    public boolean isDebugMode() {
        return false;
    }

    @Override
    public boolean reduceBlockStorageMemory() {
        return true;
    }

    @Override
    public int getMaxConnections() {
        return 20;
    }

    @Override
    public int getMaxPacketsPerSecond() {
        return 150;
    }
}
