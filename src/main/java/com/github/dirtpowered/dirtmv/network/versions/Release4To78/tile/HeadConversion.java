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

public class HeadConversion {

    public static void convert(ServerSession session, PacketData data, String nickname, MinecraftVersion from) {
        CompoundBinaryTag binaryTag = data.read(Type.COMPOUND_TAG, 4);
        CompoundBinaryTag.Builder rootTag = CompoundBinaryTag.builder();
        CompoundBinaryTag.Builder ownerTag = CompoundBinaryTag.builder();
        CompoundBinaryTag.Builder profileTag = CompoundBinaryTag.builder();
        rootTag.put(binaryTag);

        GameProfileFetcher.getProfile(nickname).thenAccept(profile -> {
            serializeGameProfile(profileTag, profile);
            ownerTag.put("Owner", profileTag.build());

            rootTag.put(ownerTag.build());
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
                    propertyList.add(childTag.build().asBinaryTag());
                }

                parent.put(property, propertyList.build());
            }

            rootTag.put("Properties", parent.build());
        }
    }
}