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
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3.V1_3_1RProtocol;
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
        addPacket(0x00, ProtocolState.STATUS, PacketDirection.CLIENT_TO_SERVER, new DataType[0]);

        // ping
        addPacket(0x01, ProtocolState.STATUS, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.LONG // time
        });

        // login start
        addPacket(0x00, ProtocolState.LOGIN, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.STRING, // player name
        });

        // encryption response
        addPacket(0x01, ProtocolState.LOGIN, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.VAR_INT_BYTE_ARRAY, // verify key
                V1_7_2RProtocol.VAR_INT_BYTE_ARRAY, // token
        });


        // keep-alive
        addPacket(0x00, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.INT // keep-alive id
        });

        // chat client -> server
        addPacket(0x01, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.STRING // Chat Message
        });

        // use entity
        addPacket(0x02, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.INT ,// entity id
                BaseProtocol.BYTE // action
        });

        // player
        addPacket(0x03, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE //on ground
        });

        // player position
        addPacket(0x04, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.BYTE //on ground
        });

        // player look
        addPacket(0x05, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.FLOAT, // yaw
                BaseProtocol.FLOAT, // pitch
                BaseProtocol.BYTE //on ground
        });

        // player pos look
        addPacket(0x06, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.FLOAT, // yaw
                BaseProtocol.FLOAT, // pitch
                BaseProtocol.BYTE //on ground
        });

        // player digging
        addPacket(0x07, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.UNSIGNED_BYTE,
                BaseProtocol.INT,
                BaseProtocol.UNSIGNED_BYTE,
                BaseProtocol.INT,
                BaseProtocol.UNSIGNED_BYTE,
        });

        // block placement
        addPacket(0x08, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.UNSIGNED_BYTE,
                BaseProtocol.INT,
                BaseProtocol.UNSIGNED_BYTE,
                V1_3_1RProtocol.ITEM,
                BaseProtocol.UNSIGNED_BYTE,
                BaseProtocol.UNSIGNED_BYTE,
                BaseProtocol.UNSIGNED_BYTE,
        });

        // held slot
        addPacket(0x09, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.SHORT // slot
        });

        // animation
        addPacket(0x0A, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.BYTE
        });

        // entity action
        addPacket(0x0B, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.BYTE,
                BaseProtocol.INT
        });

        // player input
        addPacket(0x0C, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT,
                BaseProtocol.BOOLEAN,
                BaseProtocol.BOOLEAN
        });

        // close window
        addPacket(0x0D, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE, // window id
        });

        // click window
        addPacket(0x0E, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE, // window id
                BaseProtocol.SHORT,
                BaseProtocol.BYTE,
                BaseProtocol.SHORT,
                BaseProtocol.BYTE,
                V1_3_1RProtocol.ITEM
        });

        // confirm transaction
        addPacket(0x0F, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE,
                BaseProtocol.SHORT,
                BaseProtocol.BYTE
        });

        // enchant slot selection
        addPacket(0x11, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE, // id
                BaseProtocol.BYTE // button
        });

        // player abilities
        addPacket(0x13, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE,
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT
        });

        // creative item get
        addPacket(0x10, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.SHORT, // slot
                V1_3_1RProtocol.ITEM
        });

        // tab complete
        addPacket(0x14, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[] {
                V1_7_2RProtocol.STRING
        });

        // client settings
        addPacket(0x15, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.STRING,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE, // chat visibility
                BaseProtocol.BOOLEAN,
                BaseProtocol.BYTE, // difficulty
                BaseProtocol.BOOLEAN,
        });

        // client status
        addPacket(0x16, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE // action
        });

        // custom payload
        addPacket(0x17, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.STRING, // Channel name
                BaseProtocol.SHORT_BYTE_ARRAY // payload
        });
    }
}
