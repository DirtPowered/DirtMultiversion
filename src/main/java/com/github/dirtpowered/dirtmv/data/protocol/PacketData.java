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

package com.github.dirtpowered.dirtmv.data.protocol;

import com.github.dirtpowered.dirtmv.data.protocol.io.NettyOutputWrapper;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.translator.ProtocolState;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class PacketData {

    @Getter
    private final int opCode;

    @Getter
    private final TypeHolder<?>[] objects;

    @Getter
    @Setter
    private ProtocolState nettyState;

    public PacketData(int opCode, TypeHolder<?>... objects) {
        this.opCode = opCode;
        this.objects = objects;
    }

    public PacketData(int opCode, ProtocolState state, TypeHolder<?>... objects) {
        this.opCode = opCode;
        this.nettyState = state;
        this.objects = objects;
    }

    public TypeHolder<?> read(int index) {
        try {
            return objects[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public void modify(int index, TypeHolder<?> holder) {
        try {
            objects[index] = holder;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public <T> T read(TypeObject<T> type, int index) {
        try {
            return type.getType().cast(objects[index].getObject());
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public PacketOutput toMessage() throws IOException {
        PacketOutput packetOutput = new NettyOutputWrapper(Unpooled.buffer());

        for (TypeHolder<?> typeHolder : objects) {
            typeHolder.getType().getTypeHandler().handle(typeHolder, packetOutput);
        }

        return packetOutput;
    }
}
