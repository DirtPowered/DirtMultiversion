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

package com.github.dirtpowered.dirtmv.data.user;

import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolStorage {
    private final Map<Class, Object> savedObjects = new ConcurrentHashMap<>();

    public boolean hasObject(Class key) {
        return savedObjects.containsKey(key);
    }

    // it can't be null, because it will throw exception first
    @NonNull
    public <T> T get(Class<T> key) {
        if (!hasObject(key)) {
            String prefix = getClass().getSimpleName();
            throw new IllegalArgumentException("[" + prefix + "] key '" + key.getSimpleName() + "' doesn't exist");
        }

        return (T) savedObjects.get(key);
    }

    public void set(Class key, Object o) {
        savedObjects.put(key, o);
    }

    public Map<Class, Object> getSavedObjects() {
        return savedObjects;
    }
}
