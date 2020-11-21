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
import com.github.dirtpowered.dirtmv.data.protocol.definitions.B1_3.V1_3BProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_2.V1_2_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_3.V1_3_1RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_4.V1_4_6RProtocol;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;

public class V1_7_2ProtocolDefinitions extends StateDependedProtocol {

    @Override
    public void registerPackets() {
        // server info response
        addPacket(0x00, ProtocolState.STATUS, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING // JSON Message
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

        // keep-alive
        addPacket(0x00, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT // keep-alive id
        });

        // join game
        addPacket(0x01, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.UNSIGNED_BYTE, // dimension
                BaseProtocol.BYTE, // hardcore
                BaseProtocol.UNSIGNED_BYTE, // difficulty
                BaseProtocol.BYTE, // max players
                V1_7_2RProtocol.STRING // world type
        });

        // chat server -> client
        addPacket(0x02, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING // JSON Message
        });

        // update time
        addPacket(0x03, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.LONG, // time
                BaseProtocol.LONG // age
        });

        // entity equipment
        addPacket(0x04, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.SHORT, // slot
                V1_3_1RProtocol.ITEM // item
        });

        // spawn position
        addPacket(0x05, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.INT, // Pos X
                V1_7_2RProtocol.INT, // Pos Y
                V1_7_2RProtocol.INT // Pos Z
        });

        // update health
        addPacket(0x06, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.FLOAT,
                BaseProtocol.SHORT,
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
                BaseProtocol.BOOLEAN, // on ground
        });

        // set held slot
        addPacket(0x09, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.BYTE, // slot
        });

        // animation
        addPacket(0x0B, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                BaseProtocol.UNSIGNED_BYTE // animation
        });

        // collect item
        addPacket(0x0D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.INT
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

        // spawn player
        addPacket(0x0C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                V1_7_2RProtocol.STRING, // uuid
                V1_7_2RProtocol.STRING, // name
                BaseProtocol.INT, // x
                BaseProtocol.INT, // y
                BaseProtocol.INT, // z
                BaseProtocol.BYTE, // yaw
                BaseProtocol.BYTE, // pitch
                BaseProtocol.SHORT, // held item
                V1_7_2RProtocol.METADATA
        });

        // spawn painting
        addPacket(0x10, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT, // entity id
                V1_7_2RProtocol.STRING, // painting name
                BaseProtocol.INT,
                BaseProtocol.INT,
                BaseProtocol.INT,
                BaseProtocol.INT,
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
                BaseProtocol.INT, // entity id
                BaseProtocol.SHORT, // velocity X
                BaseProtocol.SHORT, // velocity Y
                BaseProtocol.SHORT, // velocity Z
        });

        // entity destroy
        addPacket(0x13, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.BYTE_INT_ARRAY, // entity id array
        });

        // entity
        addPacket(0x14, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
        });

        // entity relative move
        addPacket(0x15, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
        });

        // entity look
        addPacket(0x16, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
        });

        // entity look move
        addPacket(0x17, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
        });

        // entity teleport
        addPacket(0x18, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.INT, // pos X
                BaseProtocol.INT, // pos Y
                BaseProtocol.INT, // pos Z
                BaseProtocol.BYTE, // yaw
                BaseProtocol.BYTE // pitch
        });

        // entity head look
        addPacket(0x19, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                BaseProtocol.BYTE, // yaw
        });

        // entity attributes
        addPacket(0x20, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.ENTITY_ATTRIBUTES
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

        // entity metadata
        addPacket(0x1C, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // entity id
                V1_7_2RProtocol.METADATA // metadata
        });

        // set experience
        addPacket(0x1F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.FLOAT,
                BaseProtocol.SHORT,
                BaseProtocol.SHORT,
        });

        // chunk
        addPacket(0x21, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_3_1RProtocol.CHUNK, // chunk
        });

        // multi block change
        addPacket(0x22, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT, // x
                BaseProtocol.INT, // z
                V1_2_1RProtocol.MULTIBLOCK_ARRAY
        });

        // block change
        addPacket(0x23, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.UNSIGNED_BYTE,
                BaseProtocol.INT,
                V1_7_2RProtocol.VAR_INT,
                BaseProtocol.UNSIGNED_BYTE
        });

        // block break animation
        addPacket(0x25, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.VAR_INT,
                BaseProtocol.INT,
                BaseProtocol.INT,
                BaseProtocol.INT,
                BaseProtocol.UNSIGNED_BYTE
        });

        // chunk bulk
        addPacket(0x26, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_4_6RProtocol.CHUNK_BULK, // chunk bulk
        });

        // effect
        addPacket(0x28, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.INT,
                BaseProtocol.BYTE,
                BaseProtocol.INT,
                BaseProtocol.INT,
                BaseProtocol.BOOLEAN,
        });

        // entity effect
        addPacket(0x1D, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.BYTE,
                BaseProtocol.BYTE,
                BaseProtocol.SHORT
        });

        // remove entity effect
        addPacket(0x1E, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.UNSIGNED_BYTE
        });

        // sound effect
        addPacket(0x29, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // sound name
                BaseProtocol.INT, // pos x
                BaseProtocol.INT, // pos y
                BaseProtocol.INT, // pos z
                BaseProtocol.FLOAT,
                BaseProtocol.UNSIGNED_BYTE
        });

        // spawn particle
        addPacket(0x2A, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // particle name
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT,
                BaseProtocol.FLOAT,
                BaseProtocol.INT,
        });

        // change game state
        addPacket(0x2B, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.UNSIGNED_BYTE,
                BaseProtocol.FLOAT
        });

        // set slot
        addPacket(0x2F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.UNSIGNED_BYTE, // window id
                BaseProtocol.SHORT, // slot
                V1_3_1RProtocol.ITEM // item
        });

        // window items
        addPacket(0x30, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.UNSIGNED_BYTE, // window id
                V1_3_1RProtocol.ITEM_ARRAY // item array
        });

        // update tile entity
        addPacket(0x35, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.INT,
                BaseProtocol.SHORT,
                BaseProtocol.INT,
                BaseProtocol.BYTE,
                BaseProtocol.COMPOUND_TAG
        });

        // tab list item
        addPacket(0x38, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // name
                BaseProtocol.BOOLEAN, // online ?
                BaseProtocol.SHORT // ping
        });

        // player abilities
        addPacket(0x39, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                BaseProtocol.BYTE, // settings bitmask
                BaseProtocol.FLOAT, // fly speed
                BaseProtocol.FLOAT, // walk speed
        });

        // custom payload
        addPacket(0x3F, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // Channel name
                BaseProtocol.SHORT_BYTE_ARRAY // payload
        });

        // play disconnect
        addPacket(0x40, ProtocolState.PLAY, PacketDirection.SERVER_TO_CLIENT, new DataType[]{
                V1_7_2RProtocol.STRING, // disconnect message
        });

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

        // set sign text
        addPacket(0x12, ProtocolState.PLAY, PacketDirection.CLIENT_TO_SERVER, new DataType[]{
                BaseProtocol.INT, // x
                BaseProtocol.SHORT, // y
                BaseProtocol.INT, // z
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
