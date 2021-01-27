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

package com.github.dirtpowered.dirtmv.data;

import lombok.Getter;

@Getter
public enum MinecraftVersion {
    B1_3(9, false, "b1.3"),
    B1_4(10, false, "b1.4"),
    B1_5(11, false, "b1.5"),
    B1_6_6(13, false, "b1.6"),
    B1_7_3(14, false, "b1.7.3"),
    B1_8_1(17, false, "b1.8"),
    R1_0(22, false, "1.0"),
    R1_1(23, false, "1.1"),
    R1_2_1(28, false, "1.2.1"),
    R1_2_4(29, false, "1.2.5"),
    R1_3_1(39, false, "1.3"),
    R1_4_6(51, false, "1.4"),
    R1_5_1(60, false, "1.5.1"),
    R1_5_2(61, false, "1.5.2"),
    R1_6_1(73, false, "1.6.1"),
    R1_6_2(74, false, "1.6.2"),
    R1_6_4(78, false, "1.6.4"),
    R1_7_2(80, 4, true, "1.7.2"),
    R1_7_6(90, 5, true, "1.7.6"),
    R1_8(100, 47, true, "1.8.9");

    private final int registryId;
    private final boolean nettyProtocol;
    private final String friendlyName;
    private int protocolNettyId;

    MinecraftVersion(int registryId, boolean nettyProtocol, String friendlyName) {
        this.registryId = registryId;
        this.nettyProtocol = nettyProtocol;
        this.friendlyName = friendlyName;
    }

    MinecraftVersion(int registryId, int protocolNettyId, boolean nettyProtocol, String friendlyName) {
        this.registryId = registryId;
        this.protocolNettyId = protocolNettyId;
        this.nettyProtocol = nettyProtocol;
        this.friendlyName = friendlyName;
    }

    public static MinecraftVersion fromRegistryId(int id) {
        for (MinecraftVersion version : MinecraftVersion.values()) {
            if (version.registryId == id) {
                return version;
            }
        }

        return B1_3;
    }

    public static MinecraftVersion fromNettyProtocolId(int id) {
        for (MinecraftVersion version : MinecraftVersion.values()) {
            if (version.protocolNettyId == id) {
                return version;
            }
        }

        return R1_8;
    }
}
