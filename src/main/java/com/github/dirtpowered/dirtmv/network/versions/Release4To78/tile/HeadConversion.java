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

package com.github.dirtpowered.dirtmv.network.versions.Release4To78.tile;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.other.GameProfileFetcher;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;

import java.util.Map;

public class HeadConversion {

    public static void convert(ServerSession session, PacketData data, String nickname, MinecraftVersion from) {
        CompoundBinaryTag binaryTag = data.read(Type.COMPOUND_TAG, 4);
        CompoundBinaryTag.Builder rootTag = CompoundBinaryTag.builder();
        CompoundBinaryTag.Builder ownerTag = CompoundBinaryTag.builder();
        CompoundBinaryTag.Builder profileTag = CompoundBinaryTag.builder();

        for (Map.Entry<String, ? extends BinaryTag> stringEntry : binaryTag) {
            rootTag.put(stringEntry.getKey(), stringEntry.getValue());
        }

        GameProfileFetcher.getProfile(nickname).thenAccept(profile -> {
            serializeGameProfile(profileTag, profile);
            ownerTag.put("Owner", profileTag.build());
            for (Map.Entry<String, ? extends BinaryTag> stringEntry : ownerTag.build()) {
                rootTag.put(stringEntry.getKey(), stringEntry.getValue());
            }
            data.modify(4, new TypeHolder<>(Type.COMPOUND_TAG, rootTag.build()));

            session.sendPacket(data, PacketDirection.TO_CLIENT, from);
        });
    }

    private static void serializeGameProfile(CompoundBinaryTag.Builder rootTag, GameProfile profile) {
        rootTag.put("Name", StringBinaryTag.of(profile.getName()));
        if (profile.getId() != null) {
            StringBinaryTag uuidTag = StringBinaryTag.of(profile.getId().toString());
            rootTag.put("Id", uuidTag);
        }
        if (!profile.getProperties().isEmpty()) {
            CompoundBinaryTag.Builder parent = CompoundBinaryTag.builder();

            for (String property : profile.getProperties().keySet()) {
                ListBinaryTag.Builder<BinaryTag> propertyList = ListBinaryTag.builder();
                CompoundBinaryTag.Builder childTag;
                for (Property next : profile.getProperties().get(property)) {
                    childTag = CompoundBinaryTag.builder();
                    childTag.put("Value", StringBinaryTag.of(next.getValue()));
                    if (next.hasSignature()) {
                        childTag.put("Signature", StringBinaryTag.of(next.getSignature()));
                    }
                    propertyList.add(childTag.build());
                }

                parent.put(property, propertyList.build());
            }

            rootTag.put("Properties", parent.build());
        }
    }
}