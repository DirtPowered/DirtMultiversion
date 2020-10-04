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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.types;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class UTF8StringDataType extends DataType<String> {

    public UTF8StringDataType() {
        super(Type.UTF8_STRING);
    }

    @Override
    public String read(ByteBuf buffer) {
        int stringLength = buffer.readShort();
        Preconditions.checkArgument(stringLength < 32767, "String too big");

        byte[] bytes = new byte[stringLength];
        buffer.readBytes(bytes);

        return new String(bytes, Charset.forName("UTF-8"));
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) {
        String string = (String) typeHolder.getObject();

        byte[] message = string.getBytes(Charset.forName("UTF-8"));
        buffer.writeShort(message.length);
        buffer.writeBytes(message);
    }
}
