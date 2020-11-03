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

package com.github.dirtpowered.dirtmv.data.user;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.entity.EntityTracker;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.translator.PreNettyProtocolState;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import lombok.Data;

import javax.crypto.SecretKey;

@Data
public class UserData {
    private MinecraftVersion clientVersion;
    private boolean protocolDetected;
    private PacketData proxyRequest;
    private SecretKey secretKey;
    private String username;
    private PreNettyProtocolState preNettyProtocolState;
    private int dimension;
    private int entityId;
    private int vehicleEntityId;
    private EntityTracker entityTracker;
    private ProtocolState protocolState;
    private String address;
    private int port;

    public UserData() {
        this.clientVersion = MinecraftVersion.B1_5;
        this.protocolDetected = false;
        this.preNettyProtocolState = PreNettyProtocolState.STATUS;
        this.protocolState = ProtocolState.HANDSHAKE;
        this.entityTracker = new EntityTracker();
    }
}
