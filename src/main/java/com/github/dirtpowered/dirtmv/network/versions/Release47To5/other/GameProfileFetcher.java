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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5.other;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import org.pmw.tinylog.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GameProfileFetcher {
    private static final String SKIN_API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String UUID_API_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .registerTypeAdapter(GameProfile.class, new GameProfileSerializer())
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
            .create();

    private static final AsyncLoadingCache<String, GameProfile> skinCache = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .buildAsync(GameProfileFetcher::fetchBlocking);

    public static CompletableFuture<GameProfile> getSkinFor(String username) {
        return skinCache.get(ChatUtils.stripColor(username));
    }

    private static GameProfile fetchBlocking(String username) {
        String uuidData = fetchDataFrom(String.format(UUID_API_URL, username));
        if (uuidData != null) {
            try {
                JsonObject jsonObject = JsonParser.parseString(uuidData).getAsJsonObject();
                String skinData = fetchDataFrom(String.format(SKIN_API_URL, jsonObject.get("id").getAsString()));

                if (skinData != null) {
                    return gson.fromJson(skinData, GameProfile.class);
                }
            } catch (IllegalStateException e) {
                Logger.warn("unable to fetch skin for {}", username);
            }
        }

        return new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8)), username);
    }

    private static String fetchDataFrom(String serviceUrl) {
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(serviceUrl);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            bufferedReader.close();
            return builder.toString();
        } catch (IOException e) {
            Logger.warn("Error while fetching data from Mojang API: {}", e.getMessage());
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

        public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = (JsonObject) json;
            UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
            String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            GameProfile profile = new GameProfile(id, name);

            if (object.has("properties")) {
                for (Entry<String, Property> prop : ((PropertyMap) context.deserialize(object.get("properties"), PropertyMap.class)).entries()) {
                    profile.getProperties().put(prop.getKey(), prop.getValue());
                }
            }
            return profile;
        }

        public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            if (profile.getId() != null)
                result.add("id", context.serialize(profile.getId()));
            if (profile.getName() != null)
                result.addProperty("name", profile.getName());
            if (!profile.getProperties().isEmpty())
                result.add("properties", context.serialize(profile.getProperties()));
            return result;
        }
    }
}