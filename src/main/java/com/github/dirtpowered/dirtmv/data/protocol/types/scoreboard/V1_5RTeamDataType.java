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

package com.github.dirtpowered.dirtmv.data.protocol.types.scoreboard;

import com.github.dirtpowered.dirtmv.data.protocol.BaseProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_5Team;

import java.io.IOException;

public class V1_5RTeamDataType extends DataType<V1_5Team> {

    public V1_5RTeamDataType() {
        super(Type.V1_5_TEAM);
    }

    @Override
    public V1_5Team read(PacketInput packetInput) throws IOException {
        String name = (String) BaseProtocol.STRING.read(packetInput);
        int action = packetInput.readByte();

        String displayName = null;
        String prefix = null;
        String suffix = null;
        int friendlyFire = 0;

        String[] players = new String[0];

        switch (action) {
            case 0:
                displayName = (String) BaseProtocol.STRING.read(packetInput);
                prefix = (String) BaseProtocol.STRING.read(packetInput);
                suffix = (String) BaseProtocol.STRING.read(packetInput);
                friendlyFire = packetInput.readByte();
                players = new String[packetInput.readShort()];

                for (int i = 0; i < players.length; i++) players[i] = (String) BaseProtocol.STRING.read(packetInput);
                break;
            case 2:
                displayName = (String) BaseProtocol.STRING.read(packetInput);
                prefix = (String) BaseProtocol.STRING.read(packetInput);
                suffix = (String) BaseProtocol.STRING.read(packetInput);
                friendlyFire = packetInput.readByte();
                break;
            case 3:
            case 4:
                players = new String[packetInput.readShort()];

                for (int i = 0; i < players.length; i++) players[i] = (String) BaseProtocol.STRING.read(packetInput);
                break;
        }

        return new V1_5Team(name, action, displayName, prefix, suffix, friendlyFire, players);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) throws IOException {
        V1_5Team teamObject = (V1_5Team) typeHolder.getObject();

        BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getName()), packetOutput);
        packetOutput.writeByte(teamObject.getAction());

        switch (teamObject.getAction()) {
            case 0:
                BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getDisplayName()), packetOutput);
                BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getPrefix()), packetOutput);
                BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getSuffix()), packetOutput);
                packetOutput.writeByte(teamObject.getFriendlyFire());

                packetOutput.writeShort(teamObject.getPlayers().length);

                for (int i = 0; i < teamObject.getPlayers().length; i++)
                    BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getPlayers()[i]), packetOutput);
                break;
            case 2:
                BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getDisplayName()), packetOutput);
                BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getPrefix()), packetOutput);
                BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getSuffix()), packetOutput);
                packetOutput.writeByte(teamObject.getFriendlyFire());
                break;
            case 3:
            case 4:
                packetOutput.writeShort(teamObject.getPlayers().length);
                for (int i = 0; i < teamObject.getPlayers().length; i++)
                    BaseProtocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getPlayers()[i]), packetOutput);
                break;
        }
    }
}
