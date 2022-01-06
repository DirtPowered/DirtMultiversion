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

package com.github.dirtpowered.dirtmv.network.versions.Release73To61.ping;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerMotd {
    private int protocol;
    private String versionName;
    private String motd;
    private int online;
    private int max;

    public static ServerMotd deserialize(String message) {
        String[] parts = message.split("\00");
        return new ServerMotd(Integer.parseInt(parts[1]), parts[2], parts[3], Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
    }

    public static String serialize(ServerMotd serverMotd) {
        String colorChar = "\u00a7";
        String splitChar = "\00";

        return colorChar + "1"
                + splitChar + serverMotd.getProtocol()
                + splitChar + serverMotd.getVersionName()
                + splitChar + serverMotd.getMotd()
                + splitChar + serverMotd.getOnline()
                + splitChar + serverMotd.getMax();
    }
}
