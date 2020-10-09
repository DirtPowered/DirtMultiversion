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
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class PacketUtil {

    public static PacketData readPacket(MinecraftVersion version, ByteBuf buffer) throws IOException {
        int packetId = buffer.readUnsignedByte();

        Protocol protocol = ProtocolRegistry.getProtocolFromVersion(version);
        DataType[] parts = protocol.dataTypes[packetId];

        if (parts == null)
            throw new IOException("Unknown packet id " + packetId + " (" + PreNettyPacketNames.getPacketName(packetId) + ")");

        TypeHolder[] typeHolders = new TypeHolder[parts.length];

        int i = 0;

        while (i < parts.length) {
            DataType dataType = parts[i];
            typeHolders[i] = new TypeHolder(dataType.getType(), dataType.read(buffer));
            i++;
        }

        return new PacketData(packetId, typeHolders);
    }

    public static PacketData createPacket(MinecraftVersion version, int packetId, TypeHolder[] packetData) {
        Protocol protocol = ProtocolRegistry.getProtocolFromVersion(version);

        DataType[] parts = protocol.dataTypes[packetId];

        for (int i = 0; i < parts.length; i++) {
            DataType dataType = parts[i];
            TypeHolder holder = packetData[i];

            if (!dataType.getType().equals(holder.getType())) {
                String err = "[" + packetId + "] " + "Wrong data type " + holder.getType() + " at index '" + i + "', expected " + dataType.getType();
                return new PacketData(0xFF, new TypeHolder(Type.STRING, err));
            }
        }

        return new PacketData(packetId, packetData);
    }
}
