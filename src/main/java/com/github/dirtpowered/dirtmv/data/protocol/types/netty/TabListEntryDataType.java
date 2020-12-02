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

package com.github.dirtpowered.dirtmv.data.protocol.types.netty;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_7.V1_7_2RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.definitions.R1_8.V1_8RProtocol;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.profile.GameProfile;
import com.github.dirtpowered.dirtmv.data.protocol.objects.profile.Property;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.PlayerListEntry;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.TabListAction;
import com.github.dirtpowered.dirtmv.data.protocol.objects.tablist.TabListEntry;

import java.io.IOException;
import java.util.UUID;

public class TabListEntryDataType extends DataType<TabListEntry> {

    public TabListEntryDataType() {
        super(Type.TAB_LIST_ENTRY);
    }

    @Override
    public TabListEntry read(PacketInput packetInput) throws IOException {
        TabListAction action = TabListAction.fromId(packetInput.readVarInt());
        PlayerListEntry[] entries = new PlayerListEntry[packetInput.readVarInt()];

        for (int i = 0; i < entries.length; i++) {
            UUID uuid = (UUID) V1_8RProtocol.UUID.read(packetInput);
            String username = (String) V1_7_2RProtocol.STRING.read(packetInput);

            GameProfile profile = action == TabListAction.ADD_PLAYER ? new GameProfile(uuid, username) : new GameProfile(uuid, null);

            PlayerListEntry listEntry = null;
            String displayName = null;

            switch (action) {
                case ADD_PLAYER:
                    Property[] properties = new Property[packetInput.readVarInt()];
                    for (int j = 0; j < properties.length; j++) {
                        String property = (String) V1_7_2RProtocol.STRING.read(packetInput);
                        String value = (String) V1_7_2RProtocol.STRING.read(packetInput);

                        String signature = null;
                        if (packetInput.readBoolean()) {
                            signature = (String) V1_7_2RProtocol.STRING.read(packetInput);
                        }

                        properties[j] = new Property(property, value, signature);
                    }

                    int gameMode = packetInput.readVarInt();
                    int ping = packetInput.readVarInt();

                    if (packetInput.readBoolean()) {
                        displayName = (String) V1_7_2RProtocol.STRING.read(packetInput);
                    }

                    listEntry = new PlayerListEntry(profile, properties, gameMode, ping, displayName);
                    break;
                case UPDATE_GAMEMODE:
                    listEntry = new PlayerListEntry(profile,  packetInput.readVarInt());
                    break;
                case UPDATE_LATENCY:
                    listEntry = new PlayerListEntry(profile, packetInput.readVarInt(), true);
                    break;
                case UPDATE_DISPLAY_NAME:
                    if (packetInput.readBoolean()) {
                        displayName = (String) V1_7_2RProtocol.STRING.read(packetInput);
                    }

                    listEntry = new PlayerListEntry(profile, displayName);
                    break;
                case REMOVE_PLAYER:
                    listEntry = new PlayerListEntry(profile);
                    break;
            }

            entries[i] = listEntry;
        }

        return new TabListEntry(action, entries);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) throws IOException {
        TabListEntry tabListEntry = (TabListEntry) typeHolder.getObject();

        TabListAction action = tabListEntry.getAction();
        packetOutput.writeVarInt(action.getAction());

        PlayerListEntry[] entries = tabListEntry.getEntries();

        packetOutput.writeVarInt(entries.length);

        for(PlayerListEntry entry : entries) {
            V1_8RProtocol.UUID.write(new TypeHolder(Type.UUID, entry.getProfile().getId()), packetOutput);

            switch(action) {
                case ADD_PLAYER:
                    V1_7_2RProtocol.STRING.write(new TypeHolder(Type.V1_7_STRING, entry.getProfile().getName()), packetOutput);
                    packetOutput.writeVarInt(entry.getProperties().length);

                    for(Property property : entry.getProperties()) {
                        V1_7_2RProtocol.STRING.write(new TypeHolder(Type.V1_7_STRING, property.getName()), packetOutput);
                        V1_7_2RProtocol.STRING.write(new TypeHolder(Type.V1_7_STRING, property.getValue()), packetOutput);

                        packetOutput.writeBoolean(property.hasSignature());

                        if(property.hasSignature()) {
                            V1_7_2RProtocol.STRING.write(new TypeHolder(Type.V1_7_STRING, property.getSignature()), packetOutput);
                        }
                    }

                    packetOutput.writeVarInt(entry.getGameMode());
                    packetOutput.writeVarInt(entry.getPing());
                    packetOutput.writeBoolean(entry.getDisplayName() != null);

                    if(entry.getDisplayName() != null) {
                        V1_7_2RProtocol.STRING.write(new TypeHolder(Type.V1_7_STRING, entry.getDisplayName()), packetOutput);
                    }
                    break;
                case UPDATE_GAMEMODE:
                    packetOutput.writeVarInt(entry.getGameMode());
                    break;
                case UPDATE_LATENCY:
                    packetOutput.writeVarInt(entry.getPing());
                    break;
                case UPDATE_DISPLAY_NAME:
                    packetOutput.writeBoolean(entry.getDisplayName() != null);

                    if(entry.getDisplayName() != null) {
                        V1_7_2RProtocol.STRING.write(new TypeHolder(Type.V1_7_STRING, entry.getDisplayName()), packetOutput);
                    }
                    break;
                case REMOVE_PLAYER:
                    break;
            }
        }
    }
}
