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

package com.github.dirtpowered.dirtmv.network.packet;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.packet.protocol.ProtocolRegistry;
import com.github.dirtpowered.dirtmv.utils.PreNettyPacketNames;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class PacketUtil {

    public static PacketData readPacket(MinecraftVersion version, ByteBuf buffer) throws IOException {
        short packetId = buffer.readUnsignedByte();

        Protocol protocol = ProtocolRegistry.getProtocolFromVersion(version);
        Preconditions.checkNotNull(protocol, "Protocol %s is not registered", version);

        DataType[] parts = protocol.dataTypes[packetId];

        String packetMapping = PreNettyPacketNames.getPacketName(packetId);
        String protocolName = protocol.getClass().getSimpleName();

        Preconditions.checkNotNull(parts, "Unknown packet id %s (%s) in protocol %s", packetId, packetMapping, protocolName);

        TypeHolder[] typeHolders = new TypeHolder[parts.length];

        int i = 0;

        while (i < parts.length) {
            DataType dataType = parts[i];
            typeHolders[i] = new TypeHolder(dataType.getType(), dataType.read(buffer));
            i++;
        }

        return new PacketData(packetId, typeHolders);
    }

    public static PacketData createPacket(int packetId, TypeHolder[] packetData) {
        return new PacketData(packetId, packetData);
    }
}
