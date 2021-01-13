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

package com.github.dirtpowered.dirtmv.data.utils;

import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.pmw.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class NBTUtils {

    /**
     * Reads compressed binary tag from network
     *
     * @param packetInput Network input
     * @return CompoundBinaryTag - NBT Tag
     */
    public static CompoundBinaryTag readNBT(PacketInput packetInput) {
        try {
            short size = packetInput.readShort();

            if (size < 0) {
                return null;
            } else {
                byte[] data = packetInput.readBytes(size);
                CompoundBinaryTag tag = null;
                try {
                    tag = BinaryTagIO.readCompressedInputStream(new ByteArrayInputStream(data));
                } catch (IllegalArgumentException e) {
                    Logger.error("error while parsing NBT data: {}", e.getMessage());
                }
                return tag;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads uncompressed binary tag from network
     *
     * @param packetInput Network input
     * @return CompoundBinaryTag - NBT Tag
     */
    public static CompoundBinaryTag readNBTUncompressed(PacketInput packetInput) {
        ByteBuf buf = packetInput.getBuffer();

        int readerIndex = buf.readerIndex();
        byte tagId = packetInput.readByte();

        if (tagId == 0) {
            return null;
        } else {
            buf.readerIndex(readerIndex);
            try {
                CompoundBinaryTag tag = null;
                try {
                    tag = BinaryTagIO.readInputStream(new ByteBufInputStream(buf));
                } catch (IllegalArgumentException e) {
                    Logger.error("error while parsing NBT data: {}", e.getMessage());
                }
                return tag;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Writes compressed binary tag to network
     *
     * @param tag          NBT tag
     * @param packetOutput Network output
     */
    public static void writeNBT(CompoundBinaryTag tag, PacketOutput packetOutput) {
        try {
            if (tag == null) {
                packetOutput.writeShort(-1);
            } else {
                ByteArrayOutputStream var1 = new ByteArrayOutputStream();

                try (DataOutputStream var2 = new DataOutputStream(new GZIPOutputStream(var1))) {
                    BinaryTagIO.writeDataOutput(tag, var2);
                }

                byte[] data = var1.toByteArray();

                packetOutput.writeShort((short) data.length);
                packetOutput.writeBytes(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Writes uncompressed binary tag to network
     *
     * @param tag          NBT tag
     * @param packetOutput Network output
     */
    public static void writeNBTUncompressed(CompoundBinaryTag tag, PacketOutput packetOutput) {
        try {
            if (tag == null) {
                packetOutput.writeByte(0);
            } else {
                BinaryTagIO.writeDataOutput(tag, new ByteBufOutputStream(packetOutput.getBuffer()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
