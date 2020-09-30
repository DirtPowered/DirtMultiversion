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

import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.type.arrays.ByteArrayDataType;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public abstract class Protocol {

    public static final DataType INT = new DataType<Integer>(Type.INT) {

        @Override
        public Integer read(ByteBuf buffer) {
            return buffer.readInt();
        }

        @Override
        public void write(TypeHolder typeHolder, ByteBuf buffer) {
            buffer.writeInt((Integer) typeHolder.getObject());
        }
    };

    public static final DataType BYTE = new DataType<Byte>(Type.BYTE) {

        @Override
        public Byte read(ByteBuf buffer) {
            return buffer.readByte();
        }

        @Override
        public void write(TypeHolder typeHolder, ByteBuf buffer) {
            if (typeHolder.getObject() instanceof Integer) {

                buffer.writeByte((((Integer) typeHolder.getObject()).byteValue()));
                return;
            }

            buffer.writeByte((Byte) typeHolder.getObject());
        }
    };

    public static final DataType SHORT = new DataType<Short>(Type.SHORT) {

        @Override
        public Short read(ByteBuf buffer) {
            return buffer.readShort();
        }

        @Override
        public void write(TypeHolder typeHolder, ByteBuf buffer) {
            if (typeHolder.getObject() instanceof Integer) {

                buffer.writeShort(((Integer) typeHolder.getObject()));
                return;
            }

            buffer.writeShort((Short) typeHolder.getObject());
        }
    };

    public static final DataType FLOAT = new DataType<Float>(Type.FLOAT) {

        @Override
        public Float read(ByteBuf buffer) {
            return buffer.readFloat();
        }

        @Override
        public void write(TypeHolder typeHolder, ByteBuf buffer) {
            buffer.writeFloat((Float) typeHolder.getObject());
        }
    };

    public static final DataType STRING = new DataType<String>(Type.STRING) {

        @Override
        public String read(ByteBuf buffer) {
            short stringLength = buffer.readShort();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < stringLength; i++) {
                String s = String.valueOf(buffer.readChar());
                sb.append(s);
            }

            return sb.toString();
        }

        @Override
        public void write(TypeHolder typeHolder, ByteBuf buffer) {
            String string = (String) typeHolder.getObject();
            buffer.writeShort(string.length());

            for (char c : string.toCharArray()) {
                buffer.writeChar(c);
            }
        }
    };

    protected static final DataType LONG = new DataType<Long>(Type.LONG) {

        @Override
        public Long read(ByteBuf buffer) {
            return buffer.readLong();
        }

        @Override
        public void write(TypeHolder typeHolder, ByteBuf buffer) {
            if (typeHolder.getObject() instanceof Integer) {

                buffer.writeLong((((Integer) typeHolder.getObject()).longValue()));
                return;
            }

            buffer.writeLong((Long) typeHolder.getObject());
        }
    };

    protected static final DataType DOUBLE = new DataType<Double>(Type.DOUBLE) {

        @Override
        public Double read(ByteBuf buffer) {
            return buffer.readDouble();
        }

        @Override
        public void write(TypeHolder typeHolder, ByteBuf buffer) {
            buffer.writeDouble((Double) typeHolder.getObject());
        }
    };

    protected static final DataType UTF8_STRING = new DataType<String>(Type.UTF8_STRING) {

        @Override
        public String read(ByteBuf buffer) {
            byte[] bytes = new byte[buffer.readShort()];
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
    };

    protected static final DataType BYTE_BYTE_ARRAY = new ByteArrayDataType(Type.BYTE_BYTE_ARRAY);

    protected DataType[][] dataTypes = new DataType[256][];

    public abstract void registerPackets();
}
