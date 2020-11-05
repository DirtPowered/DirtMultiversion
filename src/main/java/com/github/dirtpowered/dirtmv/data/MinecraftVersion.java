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

package com.github.dirtpowered.dirtmv.data;

import lombok.Getter;

public enum MinecraftVersion {
    B1_3(9, false),
    B1_4(10, false),
    B1_5(11, false),
    B1_6_6(13, false),
    B1_7_3(14, false),
    B1_8_1(17, false),
    R1_0(22, false),
    R1_1(23, false),
    R1_2_1(28, false),
    R1_2_4(29, false),
    R1_3_1(39, false),
    R1_4_6(51, false),
    R1_5_1(60, false),
    R1_5_2(61, false),
    R1_6_1(73, false),
    R1_6_2(74, false),
    R1_6_4(78, false),
    R1_7_2(82, true); // it should be 4, but well ...I'm lazy...

    @Getter
    private int protocolId;

    @Getter
    private boolean nettyProtocol;

    MinecraftVersion(int protocolId, boolean nettyProtocol) {
        this.protocolId = protocolId;
        this.nettyProtocol = nettyProtocol;
    }

    public static MinecraftVersion fromProtocolVersion(int protocolVersion) {
        for (MinecraftVersion version : MinecraftVersion.values()) {
            if (version.protocolId == protocolVersion) {
                return version;
            }
        }

        return null;
    }
}
