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

package com.github.dirtpowered.dirtmv.data.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

public class ChatUtils {

    public final static String LEGACY_COLOR_CHAR = "§";

    /**
     * Converts old legacy (pre1.6) text messages to JSON format
     *
     * @param message Legacy text
     * @return JSON String
     */
    public static String legacyToJsonString(String message) {
        JsonObject jsonElement = new JsonObject();
        jsonElement.add("text", new JsonPrimitive(message));

        return jsonElement.toString();
    }

    /**
     * Fixes translation chat components between r1.6 and r1.7
     *
     * @param message r1.6 text message
     * @return corrected r1.7 text message
     */
    public static String fixTranslationComponent(String message) {
        JsonElement jsonElement = JsonParser.parseString(message);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("using")) {
            JsonElement element = jsonObject.get("using");

            jsonObject.remove("using");
            jsonObject.add("with", element);
        }

        return jsonObject.toString();
    }

    /**
     * Converts 1.6 text to 1.7 chat component
     *
     * @param message Legacy text
     * @return corrected r1.7+ chat component in JSON format
     */
    public static String jsonToNewChatComponent(String message) {
        String result = message;

        JsonElement jsonElement = JsonParser.parseString(message);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("text") && !jsonObject.has("with")) {
            message = jsonElement.getAsJsonObject().get("text").getAsString();
            Component component = LegacyComponentSerializer.legacySection().deserialize(message);

            result = GsonComponentSerializer.gson().serialize(component);
        }

        return result;
    }

    /**
     * Converts JSON text to plain chat message
     *
     * @param message JSON string
     * @return Legacy text
     */
    public static String jsonToLegacy(String message) {
        JsonElement jsonElement = JsonParser.parseString(message);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("text")) {
            return jsonObject.get("text").getAsString();
        }

        return "";
    }

    public static String createChatComponentFromInvalidJson(String message) {
        if (message.startsWith("\"") && message.endsWith("\"")) {
            message = "{\"text\":" + message + "}";

            JsonElement element = JsonParser.parseString(message);
            return element.toString();
        }

        return legacyToJsonString("");
    }

    /**
     * Removes colors from message (&, §)
     *
     * @param message colored message
     * @return message without colors
     */
    public static String stripColor(String message) {
        message = message.replaceAll("&", "§");
        return Pattern.compile("(?i)" + "§" + "[0-9A-FK-OR]").matcher(message).replaceAll("");
    }
}
