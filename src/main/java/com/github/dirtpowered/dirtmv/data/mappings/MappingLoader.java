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

package com.github.dirtpowered.dirtmv.data.mappings;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MappingLoader {
    private final static Map<String, Object> MAPPING_STORAGE = new HashMap<>();

    public static <T> T load(Class<T> rawType, String name) {
        try {
            File file  = getFile(name);
            MAPPING_STORAGE.put(name, DirtMultiVersion.GSON.fromJson(new FileReader(file), rawType));
        } catch (IOException e) {
            throw new RuntimeException("unable to load " + name + " file");
        }

        return rawType.cast(MAPPING_STORAGE.get(name));
    }

    private static File getFile(String name) throws IOException {
        Path path = Paths.get("src/main/resources/" + name + ".json");
        if (!Files.exists(path)) {
            File tempFile = File.createTempFile(name, ".json");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(MappingLoader.class.getResourceAsStream("/" + name + ".json"), out);
            }

            return tempFile;
        } else {
            return path.toFile();
        }
    }
}