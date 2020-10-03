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

package com.github.dirtpowered.dirtmv.utils.nbt;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.NbtIo;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class NBTUtils {

    public static CompoundTag readNBT(ByteBuf buffer) {
        try {
            short size = buffer.readShort();

            if (size < 0) {
                return null;
            } else {
                byte[] data = new byte[size];
                buffer.readBytes(data);
                return NbtIo.decompress(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeNBT(CompoundTag tag, ByteBuf buffer) {
        try {
            if (tag == null) {
                buffer.writeShort(-1);
            } else {
                byte[] data = NbtIo.compress(tag);
                buffer.writeShort((short) data.length);
                buffer.writeBytes(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
