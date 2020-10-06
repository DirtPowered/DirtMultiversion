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

import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.ByteArrayDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.ByteDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.DoubleDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.FloatDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.IntDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.LongDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.ShortDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.StringDataType;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.type.UTF8StringDataType;

public abstract class Protocol {
    // data type
    public static final DataType INT = new IntDataType();
    public static final DataType BYTE = new ByteDataType();
    public static final DataType SHORT = new ShortDataType();
    public static final DataType FLOAT = new FloatDataType();
    public static final DataType STRING = new StringDataType();
    protected static final DataType LONG = new LongDataType();
    protected static final DataType DOUBLE = new DoubleDataType();
    protected static final DataType UTF8_STRING = new UTF8StringDataType();
    protected static final DataType SHORT_BYTE_ARRAY = new ByteArrayDataType(Type.SHORT_BYTE_ARRAY);
    protected static final DataType BYTE_BYTE_ARRAY = new ByteArrayDataType(Type.BYTE_BYTE_ARRAY);
    protected DataType[][] dataTypes = new DataType[256][];

    public abstract void registerPackets();
}