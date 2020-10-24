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

package com.github.dirtpowered.dirtmv.data.translator;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.TypeObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class ServerProtocol {
    private Map<Integer, PacketTranslator> registeredTranslators = new HashMap<>();
    private MinecraftVersion from;
    private MinecraftVersion to;

    public ServerProtocol(MinecraftVersion from, MinecraftVersion to) {
        this.from = from;
        this.to = to;

        registerTranslators();
    }

    public abstract void registerTranslators();

    protected void addTranslator(int opCode, PacketTranslator packetTranslator) {
        registeredTranslators.put(opCode, packetTranslator);
    }

    public PacketTranslator getTranslatorFor(int opCode) {
        return registeredTranslators.get(opCode);
    }

    public TypeHolder set(TypeObject type, Object obj) {
        return new TypeHolder(type, obj);
    }
}
