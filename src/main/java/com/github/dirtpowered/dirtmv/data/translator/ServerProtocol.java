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

package com.github.dirtpowered.dirtmv.data.translator;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.TypeObject;
import com.github.dirtpowered.dirtmv.data.utils.CommonUtils;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;

public abstract class ServerProtocol implements ConnectionHandler {
    private final Long2ObjectMap<PacketTranslator> registeredTranslators;

    @Getter
    private final MinecraftVersion from;

    @Getter
    private final MinecraftVersion to;

    public ServerProtocol(MinecraftVersion from, MinecraftVersion to) {
        this.registeredTranslators = new Long2ObjectOpenHashMap<>();
        this.from = from;
        this.to = to;

        registerTranslators();
    }

    public abstract void registerTranslators();

    protected void addTranslator(int opCode, PacketDirection direction, PacketTranslator packetTranslator) {
        long key = CommonUtils.toLongKey(opCode, ProtocolState.PRE_NETTY.getStateId(), direction.getDirectionId());
        registeredTranslators.put(key, packetTranslator);
    }

    protected void addTranslator(int opCode, ProtocolState state, PacketDirection direction, PacketTranslator packetTranslator) {
        long key = CommonUtils.toLongKey(opCode, state.getStateId(), direction.getDirectionId());
        registeredTranslators.put(key, packetTranslator);
    }

    protected void addTranslator(int opCode, int opCodeTo, ProtocolState state, PacketDirection direction) {
        long key = CommonUtils.toLongKey(opCode, state.getStateId(), direction.getDirectionId());

        registeredTranslators.put(key, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketData data) {
                return PacketUtil.createPacket(opCodeTo, data.getObjects());
            }
        });
    }

    public PacketTranslator getTranslatorFor(int opCode, ProtocolState state, PacketDirection direction) {
        long key = CommonUtils.toLongKey(opCode, state.getStateId(), direction.getDirectionId());

        return registeredTranslators.get(key);
    }

    public TypeHolder set(TypeObject type, Object obj) {
        return new TypeHolder(type, obj);
    }

    public void addGroup(ServerProtocol translators) {
        registeredTranslators.putAll(translators.registeredTranslators);
    }

    @Override
    public void onConnect(ServerSession session) {
        // it will be overridden when needed
    }

    @Override
    public void onDisconnect(ServerSession session) {
        // it will be overridden when needed
    }

    public static PacketData cancel() {
        return new PacketData(-1);
    }
}
