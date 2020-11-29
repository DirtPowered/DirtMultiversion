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

public interface Configuration {

    /**
     * Address to listen for connections
     */
    String getBindAddress();

    /**
     * Port to listen for connections
     */
    int getBindPort();

    /**
     * Remote server address
     */
    String getRemoteServerAddress();

    /**
     * Remote server port
     */
    int getRemoteServerPort();

    /**
     * Remote server version
     */
    MinecraftVersion getServerVersion();

    /**
     * Message of the day for pre b1.8 servers
     */
    String preReleaseMOTD();

    /**
     * Max online players (visual) for pre b1.8 servers
     */
    int getMaxOnline();

    /**
     * Server icon (Base64 64x64 image)
     */
    String getServerIcon();

    /**
     * Debug mode, printing all packets, additional info
     */
    boolean isDebugMode();
}
