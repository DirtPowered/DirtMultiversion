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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5.chunk;

import com.github.dirtpowered.dirtmv.data.chunk.ChunkUtils;
import com.github.dirtpowered.dirtmv.data.chunk.storage.ExtendedBlockStorage;
import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_2RChunkStorage;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8Chunk;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

import java.util.ArrayList;
import java.util.List;

public class V1_3ToV1_8ChunkTranslator extends PacketTranslator {

    @Override
    public PacketData translate(ServerSession session, PacketData data) {
        V1_2Chunk chunk = data.read(Type.V1_3_CHUNK, 0);

        int chunkX = chunk.getChunkX();
        int chunkZ = chunk.getChunkZ();

        short bitmap = chunk.getPrimaryBitmap();
        boolean groundUp = chunk.isGroundUp();

        byte[] finalChunkData;

        // use existing chunk storage (pre 1.2 servers)
        if (chunk.getStorage() != null) {
            finalChunkData = getChunkData(chunk.getStorage(), groundUp, bitmap);
        } else {
            finalChunkData = new byte[0];
            // TODO: deserialize
        }

        V1_8Chunk newChunk = new V1_8Chunk(chunkX, chunkZ, groundUp, bitmap, finalChunkData);

        return PacketUtil.createPacket(0x21, new TypeHolder[] {
                new TypeHolder(Type.V1_8R_CHUNK, newChunk)
        });
    }

    private byte[] getChunkData(V1_2RChunkStorage storage, boolean groundUp, int bitmapValue) {
        boolean skylight = storage.isSkylight();
        byte[] biomes = storage.getBiomeData();

        ExtendedBlockStorage[] columnStorage = storage.getColumnStorage();

        List<ExtendedBlockStorage> blockStorages = new ArrayList<>();
        int columnBits = 0;

        for (int i = 0; i < columnStorage.length; ++i) {
            ExtendedBlockStorage extendedblockstorage = columnStorage[i];
            if (extendedblockstorage != null && (!groundUp || !extendedblockstorage.isEmpty()) && (bitmapValue & 1 << i) != 0) {
                columnBits |= 1 << i;

                blockStorages.add(extendedblockstorage);
            }
        }

        byte[] data = new byte[ChunkUtils.calculateDataSize(Integer.bitCount(columnBits), skylight)];

        int totalSize = 0;

        for (ExtendedBlockStorage blockStorage : blockStorages) {
            byte[] blockArray = blockStorage.getBlockLSBArray();

            for (int j = 0; j < blockArray.length; ++j) {
                int x = j & 15;
                int y = j >> 8 & 15;
                int z = j >> 4 & 15;

                int blockData = blockStorage.getBlockMetadataArray().getNibble(x, y, z);

                char c = (char) (blockArray[j] << 4 | blockData);
                data[totalSize++] = (byte) (c & 255);
                data[totalSize++] = (byte) (c >> 8 & 255);
            }
        }

        for (ExtendedBlockStorage blockStorage : blockStorages) {
            totalSize = writeData(blockStorage.getBlockLightArray().getData(), data, totalSize);
        }

        if (skylight) {
            for (ExtendedBlockStorage blockStorage : blockStorages) {
                totalSize = writeData(blockStorage.getSkylightArray().getData(), data, totalSize);
            }
        }

        if (groundUp) {
            totalSize = writeData(biomes, data, totalSize);
        }

        byte[] chunk = new byte[totalSize];
        System.arraycopy(data, 0, chunk, 0, totalSize);

        return chunk;
    }

    private int writeData(byte[] bytes, byte[] original, int saveOffset) {
        System.arraycopy(bytes, 0, original, saveOffset, bytes.length);

        return saveOffset + bytes.length;
    }
}