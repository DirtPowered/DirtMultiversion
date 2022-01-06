/*
 * Copyright (c) 2020-2022 Dirt Powered
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

package com.github.dirtpowered.dirtmv.data.sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SoundRemapper {
    private static final Map<String, String> SOUND_MAPPINGS = new HashMap<>();

    public SoundRemapper(String fileName) {
        loadFromFile(fileName);
    }

    public String getNewSoundName(String oldSoundName) {
        return SOUND_MAPPINGS.getOrDefault(oldSoundName, oldSoundName);
    }

    private void loadFromFile(String fileName) {
        File f;
        try {

            Path p = Paths.get("src/main/resources/" + fileName + ".json");
            if (!Files.exists(p)) {
                File tempFile = File.createTempFile(fileName, ".json");
                tempFile.deleteOnExit();

                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    IOUtils.copy(SoundRemapper.class.getResourceAsStream("/" + fileName + ".json"), out);
                }

                f = tempFile;
            } else {
                f = p.toFile();
            }

            JsonElement jsonElement = JsonParser.parseReader(new FileReader(f));
            JsonObject json = jsonElement.getAsJsonObject();

            JsonArray soundReplacements = json.getAsJsonArray("sound_replacements");

            int count = 0;

            for (JsonElement sound : soundReplacements) {
                JsonObject oldSound = sound.getAsJsonObject();

                for (Map.Entry<String, JsonElement> legacyEntry : oldSound.entrySet()) {
                    String old = legacyEntry.getKey();
                    String newSound = legacyEntry.getValue().getAsString();

                    SOUND_MAPPINGS.put(old, newSound);
                    count++;
                }
            }

            Logger.info("loaded {} sound replacements from '{}'", count, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
