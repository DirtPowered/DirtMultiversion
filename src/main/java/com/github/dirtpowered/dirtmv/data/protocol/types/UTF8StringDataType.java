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

package com.github.dirtpowered.dirtmv.data.protocol.types;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.google.common.base.Preconditions;

import java.nio.charset.Charset;

public class UTF8StringDataType extends DataType<String> {

    public UTF8StringDataType() {
        super(Type.UTF8_STRING);
    }

    @Override
    public String read(PacketInput packetInput) {
        int stringLength = packetInput.readShort();
        Preconditions.checkArgument(stringLength < 32767, "String too big");

        byte[] bytes = packetInput.readBytes(stringLength);

        return new String(bytes, Charset.forName("UTF-8"));
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) {
        String string = (String) typeHolder.getObject();

        byte[] message = string.getBytes(Charset.forName("UTF-8"));
        packetOutput.writeShort(message.length);
        packetOutput.writeBytes(message);
    }
}
