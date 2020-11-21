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

package com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_8;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.StateDependedProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7.V1_7_2RProtocol;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;

public class V1_8ProtocolDefinitions extends StateDependedProtocol {

    @Override
    public void registerPackets() {
        // handshake
        addPacket(0x00, ProtocolState.HANDSHAKE, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // protocol version
                V1_7_2RProtocol.STRING, // address
                BaseProtocol.UNSIGNED_SHORT, // port
                V1_7_2RProtocol.VAR_INT, // next state
        });

        // server info request
        addPacket(0x00, ProtocolState.STATUS, PacketDirection.CLIENT_TO_SERVER, new DataType[0]);

        // server info response
        addPacket(0x00, ProtocolState.STATUS, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING // JSON Message
        });

        // ping
        addPacket(0x01, ProtocolState.STATUS, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.LONG // time
        });

        // pong
        addPacket(0x01, ProtocolState.STATUS, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.LONG // time
        });

        // login disconnect
        addPacket(0x00, ProtocolState.LOGIN, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // disconnect reason
        });

        // encryption request
        addPacket(0x01, ProtocolState.LOGIN, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // server id
                V1_7_2RProtocol.VAR_INT_BYTE_ARRAY, // public key
                V1_7_2RProtocol.VAR_INT_BYTE_ARRAY, // token
        });

        // login success
        addPacket(0x02, ProtocolState.LOGIN, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // uuid
                V1_7_2RProtocol.STRING, // player name
        });

        // enable compression
        addPacket(0x03, ProtocolState.LOGIN, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT // compression threshold
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

        // keep alive
        addPacket(0x00, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT // id
        });

        // join game
        addPacket(0x01, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.UNSIGNED_BYTE, // dimension
                BaseProtocol.BYTE, // hardcore
                BaseProtocol.UNSIGNED_BYTE, // difficulty
                BaseProtocol.BYTE, // max players
                V1_7_2RProtocol.STRING, // world type
                BaseProtocol.BOOLEAN // reduced debug info
        });

        // chat
        addPacket(0x02, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // JSON Message
                BaseProtocol.BYTE // type
        });

        // update time
        addPacket(0x03, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.LONG, // time
                BaseProtocol.LONG // age
        });

        // entity equipment
        /*addPacket(0x04, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.SHORT, // slot
                V1_3_1RProtocol.ITEM // item
        }); */

        // spawn position
        addPacket(0x05, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.LONG // encoded block position
        });

        // update health
        addPacket(0x06, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.FLOAT,
                V1_7_2RProtocol.VAR_INT,
                BaseProtocol.FLOAT
        });

        // respawn
        addPacket(0x07, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.UNSIGNED_BYTE, // difficulty
                BaseProtocol.UNSIGNED_BYTE, // dimension
                V1_7_2RProtocol.STRING // world type
        });

        // position and look
        addPacket(0x08, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.DOUBLE, // pos X
                BaseProtocol.DOUBLE, // pos Y
                BaseProtocol.DOUBLE, // pos Z
                BaseProtocol.FLOAT, // yaw
                BaseProtocol.FLOAT, // pitch
                BaseProtocol.BYTE // flags
        });

        // set held slot
        addPacket(0x09, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.BYTE, // slot
        });

        // use bed
        addPacket(0x0A, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.LONG // encoded block position
        });

        // animation
        addPacket(0x0B, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.UNSIGNED_BYTE // animation
        });

        // player spawn
        addPacket(0x0C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                V1_8RProtocol.UUID, // uuid
                BaseProtocol.INT, // x
                BaseProtocol.INT, // y
                BaseProtocol.INT, // z
                BaseProtocol.BYTE, // yaw
                BaseProtocol.BYTE, // pitch
                BaseProtocol.SHORT, // held item
                V1_7_2RProtocol.METADATA
        });

        // collect item
        addPacket(0x0D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT,
                V1_7_2RProtocol.VAR_INT
        });

        // spawn object
        addPacket(0x0E, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entityId
                BaseProtocol.BYTE, // type
                BaseProtocol.INT, // pos X
                BaseProtocol.INT, // pos Y
                BaseProtocol.INT, // pos Z
                BaseProtocol.BYTE, // yaw
                BaseProtocol.BYTE, // pitch
                V1_3BProtocol.MOTION // optional motion
        });

        // spawn mob
        addPacket(0x0F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entityId
                BaseProtocol.BYTE, // entity type
                BaseProtocol.INT, // pos X
                BaseProtocol.INT, // pos Y
                BaseProtocol.INT, // pos Z
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.SHORT,
                BaseProtocol.SHORT,
                BaseProtocol.SHORT,
                V1_7_2RProtocol.METADATA // metadata
        });

        // spawn painting
        addPacket(0x10, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                V1_7_2RProtocol.STRING, // painting name
                BaseProtocol.LONG, // encoded block position
                BaseProtocol.UNSIGNED_BYTE,
        });

        // spawn experience orb
        addPacket(0x11, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.INT,
                BaseProtocol.INT,
                BaseProtocol.INT,
                BaseProtocol.SHORT
        });

        // entity velocity
        addPacket(0x12, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.SHORT, // velocity X
                BaseProtocol.SHORT, // velocity Y
                BaseProtocol.SHORT, // velocity Z
        });

        // entity destroy

        // entity
        addPacket(0x14, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
        });

        // entity relative move
        addPacket(0x15, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BOOLEAN
        });

        // entity look
        addPacket(0x16, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BOOLEAN
        });

        // entity look move
        addPacket(0x17, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BOOLEAN
        });

        // entity teleport
        addPacket(0x18, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.INT, // pos X
                BaseProtocol.INT, // pos Y
                BaseProtocol.INT, // pos Z
                BaseProtocol.BYTE, // yaw
                BaseProtocol.BYTE, // pitch
                BaseProtocol.BOOLEAN
        });

        // entity head look
        addPacket(0x19, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.BYTE, // yaw
        });

        // entity status
        addPacket(0x1A, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.BYTE, // status
        });

        // entity attach
        addPacket(0x1B, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.INT, // attached entity id
                BaseProtocol.UNSIGNED_BYTE, // leash
        });

        /*// entity metadata
        addPacket(0x1C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                V1_7_2RProtocol.METADATA // metadata
        });*/

        // entity effect
        addPacket(0x1D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                V1_7_2RProtocol.VAR_INT,
                BaseProtocol.BYTE
        });

        // remove entity effect
        addPacket(0x1E, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.UNSIGNED_BYTE
        });

        // set experience
        addPacket(0x1F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.FLOAT,
                V1_7_2RProtocol.VAR_INT,
                V1_7_2RProtocol.VAR_INT
        });

        // entity properties

        // block change
        addPacket(0x23, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.LONG,
                V1_7_2RProtocol.VAR_INT,
        });

        // chunk data
        addPacket(0x21, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_8RProtocol.CHUNK
        });

        // chunk bulk
        addPacket(0x26, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_8RProtocol.CHUNK_BULK
        });

        // client packets

        // keep-alive
        addPacket(0x00, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.VAR_INT,
        });

        // chat
        addPacket(0x01, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.STRING // Chat Message
        });

        // player
        addPacket(0x03, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.UNSIGNED_BYTE //on ground
        });

        // player position
        addPacket(0x04, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.UNSIGNED_BYTE //on ground
        });

        // player look
        addPacket(0x05, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.FLOAT, // yaw
                BaseProtocol.FLOAT, // pitch
                BaseProtocol.UNSIGNED_BYTE //on ground
        });

        // player pos look
        addPacket(0x06, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.DOUBLE,
                BaseProtocol.FLOAT, // yaw
                BaseProtocol.FLOAT, // pitch
                BaseProtocol.UNSIGNED_BYTE //on ground
        });

        // player digging
        addPacket(0x07, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.UNSIGNED_BYTE,
                BaseProtocol.LONG,
                BaseProtocol.BYTE
        });

        // 0x08 - place block

        // held slot change
        addPacket(0x09, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.SHORT, // slot
        });

        // animation
        addPacket(0x0A, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[0]);

        // entity action
        addPacket(0x0B, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                V1_7_2RProtocol.VAR_INT, // action id
                V1_7_2RProtocol.VAR_INT, // param
        });

        // player input
        addPacket(0x0C, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT,
                BaseProtocol.BYTE
        });

        // close window
        addPacket(0x0D, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE, // window id
        });

        // click window

        // confirm transaction
        addPacket(0x0F, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE,
                BaseProtocol.SHORT,
                BaseProtocol.BYTE
        });

        // creative item get

        // enchant slot selection
        addPacket(0x11, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE, // id
                BaseProtocol.BYTE // button
        });

        // set sign text
        addPacket(0x12, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.LONG, // encoded block position
                V1_7_2RProtocol.STRING,
                V1_7_2RProtocol.STRING,
                V1_7_2RProtocol.STRING,
                V1_7_2RProtocol.STRING,
        });

        // player abilities
        addPacket(0x13, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE,
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT
        });

        // tab complete

        // client settings
        addPacket(0x15, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.STRING,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE, // chat visibility
                BaseProtocol.BOOLEAN,
                BaseProtocol.UNSIGNED_BYTE
        });

        // client status
        addPacket(0x16, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.BYTE // action
        });

        // custom payload

        // spectate
        addPacket(0x18, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_8RProtocol.UUID
        });

        // resource pack status
        addPacket(0x19, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                V1_7_2RProtocol.STRING,
                V1_7_2RProtocol.VAR_INT // action
        });
    }
}
