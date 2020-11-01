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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.StateDependedProtocol;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;

public class V1_7_2ProtocolDefinitions extends StateDependedProtocol {

    @Override
    public void registerPackets() {
        // handshake
        addPacket(0x00, ProtocolState.HANDSHAKE, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // protocol version
                V1_7_2RProtocol.STRING, // server address
                BaseProtocol.UNSIGNED_SHORT, // server port
                V1_7_2RProtocol.VAR_INT // next state
        });

        // server info request
        addPacket(0x00, ProtocolState.PING, PacketDirection.CLIENT_TO_SERVER, new DataType[0]);

        // server info response
        addPacket(0x00, ProtocolState.PING, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING // JSON Message
        });

        // ping
        addPacket(0x01, ProtocolState.PING, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.LONG // time
        });

        // pong
        addPacket(0x01, ProtocolState.PING, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.LONG // time
        });

        // login disconnect
        addPacket(0x00, ProtocolState.LOGIN, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // disconnect reason
        });
    }
}
