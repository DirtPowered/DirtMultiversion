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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.StateDependedProtocol;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;

public class V1_7_6ProtocolDefinitions extends StateDependedProtocol {

    @Override
    public void registerPackets() {
        // register all 1.7.2 packets
        V1_7_2RProtocol.STATE_DEPENDED_PROTOCOL.getPackets().forEach((packetRegObj, dataTypes) -> getPackets().put(packetRegObj, dataTypes));

        // spawn player
        addPacket(0x0C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                V1_7_2RProtocol.STRING, // uuid
                V1_7_2RProtocol.STRING, // name
                V1_7_2RProtocol.VAR_INT,
                BaseProtocol.INT, // x
                BaseProtocol.INT, // y
                BaseProtocol.INT, // z
                BaseProtocol.BYTE, // yaw
                BaseProtocol.BYTE, // pitch
                BaseProtocol.SHORT, // held item
                V1_7_2RProtocol.METADATA
        });
    }
}
