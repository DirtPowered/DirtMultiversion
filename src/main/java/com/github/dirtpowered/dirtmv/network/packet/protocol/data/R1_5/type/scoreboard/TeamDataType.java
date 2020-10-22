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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_5.type.scoreboard;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Protocol;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_5Team;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class TeamDataType extends DataType<V1_5Team> {

    public TeamDataType() {
        super(Type.V1_5_TEAM);
    }

    @Override
    public V1_5Team read(ByteBuf buffer) throws IOException {
        String name = (String) Protocol.STRING.read(buffer);
        int action = buffer.readByte();

        String displayName = null;
        String prefix = null;
        String suffix = null;
        int friendlyFire = 0;

        String[] players = new String[0];

        switch (action) {
            case 0:
                displayName = (String) Protocol.STRING.read(buffer);
                prefix = (String) Protocol.STRING.read(buffer);
                suffix = (String) Protocol.STRING.read(buffer);
                friendlyFire = buffer.readByte();
                players = new String[buffer.readShort()];

                for (int i = 0; i < players.length; i++) players[i] = (String) Protocol.STRING.read(buffer);
                break;
            case 2:
                displayName = (String) Protocol.STRING.read(buffer);
                prefix = (String) Protocol.STRING.read(buffer);
                suffix = (String) Protocol.STRING.read(buffer);
                friendlyFire = buffer.readByte();
                break;
            case 3:
            case 4:
                players = new String[buffer.readShort()];

                for (int i = 0; i < players.length; i++) players[i] = (String) Protocol.STRING.read(buffer);
                break;
        }

        return new V1_5Team(name, action, displayName, prefix, suffix, friendlyFire, players);
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) throws IOException {
        V1_5Team teamObject = (V1_5Team) typeHolder.getObject();

        Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getName()), buffer);
        buffer.writeByte(teamObject.getAction());

        switch(teamObject.getAction()) {
            case 0:
                Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getDisplayName()), buffer);
                Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getPrefix()), buffer);
                Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getSuffix()), buffer);
                buffer.writeByte(teamObject.getFriendlyFire());

                buffer.writeShort(teamObject.getPlayers().length);

                for (int i = 0; i < teamObject.getPlayers().length; i++)
                    Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getPlayers()[i]), buffer);
                break;
            case 2:
                Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getDisplayName()), buffer);
                Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getPrefix()), buffer);
                Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getSuffix()), buffer);
                buffer.writeByte(teamObject.getFriendlyFire());
                break;
            case 3:
            case 4:
                buffer.writeShort(teamObject.getPlayers().length);
                for (int i = 0; i < teamObject.getPlayers().length; i++)
                    Protocol.STRING.write(new TypeHolder(Type.STRING, teamObject.getPlayers()[i]), buffer);
                break;
        }
    }
}
