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

package com.github.dirtpowered.dirtmv.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.io.IOException;

public class PacketData {

    @Getter
    private int opCode;

    @Getter
    private TypeHolder[] objects;

    public PacketData(int opCode, TypeHolder... objects) {
        this.opCode = opCode;
        this.objects = objects;
    }

    public TypeHolder read(int index) {
        return objects[index];
    }

    public <T> T read(TypeObject<T> type, int index) {
        return type.getType().cast(objects[index].getObject());
    }

    public ByteBuf toMessage() throws IOException {
        ByteBuf buffer = Unpooled.buffer();

        for (TypeHolder typeHolder : objects) {
            typeHolder.getType().getTypeHandler().handle(typeHolder, buffer);
        }

        return buffer;
    }
}
