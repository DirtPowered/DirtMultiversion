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

package com.github.dirtpowered.dirtmv.data.protocol;

import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

public abstract class StateDependedProtocol {

    private Map<PacketRegObj, DataType[]> packets = new HashMap<>();

    public StateDependedProtocol() {
        registerPackets();
    }

    public abstract void registerPackets();

    protected void addPacket(int packetId, ProtocolState protocolState, PacketDirection packetDirection, DataType[] instructions) {
        packets.put(new PacketRegObj(packetId, protocolState, packetDirection), instructions);
    }

    public DataType[] getInstruction(int packetId, ProtocolState protocolState, PacketDirection packetDirection) {
        return packets.get(new PacketRegObj(packetId, protocolState, packetDirection));
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class PacketRegObj {
        private int packetId;
        private ProtocolState protocolState;
        private PacketDirection packetDirection;
    }
}
