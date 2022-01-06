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

package com.github.dirtpowered.dirtmv.data.protocol;

import com.github.dirtpowered.dirtmv.data.protocol.types.BooleanDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.ByteArrayDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.ByteDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.CompoundTagDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.DoubleDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.FloatDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.IntArrayDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.IntDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.LongDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.ShortDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.StringDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.UTF8StringDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.UnsignedByteDataType;
import com.github.dirtpowered.dirtmv.data.protocol.types.UnsignedShortDataType;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.nbt.CompoundBinaryTag;

public abstract class BaseProtocol {
    public static final DataType<Integer> INT = new IntDataType();
    public static final DataType<Byte> BYTE = new ByteDataType();
    public static final DataType<Short> UNSIGNED_BYTE = new UnsignedByteDataType();
    public static final DataType<Short> SHORT = new ShortDataType();
    public static final DataType<Float> FLOAT = new FloatDataType();
    public static final DataType<String> STRING = new StringDataType();
    public static final DataType<Long> LONG = new LongDataType();
    public static final DataType<Double> DOUBLE = new DoubleDataType();
    public static final DataType<String> UTF8_STRING = new UTF8StringDataType();
    public static final DataType<byte[]> SHORT_BYTE_ARRAY = new ByteArrayDataType(Type.SHORT_BYTE_ARRAY);
    public static final DataType<byte[]> UNSIGNED_SHORT_BYTE_ARRAY = new ByteArrayDataType(Type.UNSIGNED_SHORT_BYTE_ARRAY);
    public static final DataType<byte[]> BYTE_BYTE_ARRAY = new ByteArrayDataType(Type.BYTE_BYTE_ARRAY);
    public static final DataType<byte[]> INT_BYTE_ARRAY = new ByteArrayDataType(Type.INT_BYTE_ARRAY);
    public static final DataType<int[]> BYTE_INT_ARRAY = new IntArrayDataType();
    public static final DataType<CompoundBinaryTag> COMPOUND_TAG = new CompoundTagDataType();
    public static final DataType<Boolean> BOOLEAN = new BooleanDataType();
    public static final DataType<Integer> UNSIGNED_SHORT = new UnsignedShortDataType();

    public DataType[][] dataTypes = new DataType[256][];

    @Getter
    @Setter
    public StateDependedProtocol stateDependedProtocol;

    public BaseProtocol() {
        registerPackets();
    }

    public abstract void registerPackets();
}
