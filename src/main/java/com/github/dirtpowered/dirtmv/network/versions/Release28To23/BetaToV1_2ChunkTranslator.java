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

package com.github.dirtpowered.dirtmv.network.versions.Release28To23;

import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_2RChunkStorage;
import com.github.dirtpowered.dirtmv.data.chunk.storage.V1_3BChunkStorage;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_3BChunk;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

import java.util.Arrays;

public class BetaToV1_2ChunkTranslator extends PacketTranslator {

    @Override
    public PacketData translate(ServerSession session, PacketData data) {
        V1_3BChunk oldChunk = (V1_3BChunk) data.read(0).getObject();

        if (oldChunk.getXSize() * oldChunk.getYSize() * oldChunk.getZSize() != 32768) {
            return new PacketData(-1);
            // TODO: Chunk cache, non-full chunks
        }

        int chunkX = oldChunk.getX() / 16;
        int chunkZ = oldChunk.getZ() / 16;

        byte[] biomes = new byte[256];

        boolean groundUpContinuous = true;

        V1_3BChunkStorage betaChunkStorage = new V1_3BChunkStorage(chunkX, chunkZ);
        V1_2RChunkStorage v1_2ChunkStorage = new V1_2RChunkStorage(chunkX, chunkZ);

        int startPosition = (oldChunk.getX() + oldChunk.getXSize() - 1) / 16;
        int endPosition = (oldChunk.getZ() + oldChunk.getZSize() - 1) / 16;

        int y = Math.max(oldChunk.getY(), 0);
        int newYSize = Math.min(oldChunk.getY() + oldChunk.getYSize(), 128);

        int offset = 0;
        int bitmap = 0;

        for (int i = chunkX; i <= startPosition; ++i) {
            int x = Math.max(oldChunk.getX() - i * 16, 0);
            int newXSize = Math.min(oldChunk.getX() + oldChunk.getXSize() - i * 16, 16);

            for (int j = chunkZ; j <= endPosition; ++j) {
                int z = Math.max(oldChunk.getZ() - j * 16, 0);
                int newZSize = Math.min(oldChunk.getZ() + oldChunk.getZSize() - j * 16, 16);

                offset = betaChunkStorage.setChunkData(oldChunk.getChunk(), x, y, z, newXSize, newYSize, newZSize, offset);

                for (int posX = x; posX < newXSize; posX++) {
                    for (int posY = y; posY < newYSize; posY++) {
                        for (int posZ = z; posZ < newZSize; posZ++) {

                            if (newXSize * newYSize * newZSize != 32768) {
                                bitmap |= 1 << (posY >> 4);
                                groundUpContinuous = false;
                            }

                            v1_2ChunkStorage.setBlockId(posX, posY, posZ, betaChunkStorage.getBlockId(posX, posY, posZ));
                            v1_2ChunkStorage.setBlockMetadata(posX, posY, posZ, betaChunkStorage.getBlockData(posX, posY, posZ));
                            v1_2ChunkStorage.setBlockLight(posX, posY, posZ, betaChunkStorage.getBlockLight(posX, posY, posZ));
                            v1_2ChunkStorage.setSkyLight(posX, posY, posZ, betaChunkStorage.getSkyLight(posX, posY, posZ));
                        }
                    }
                }
            }
        }

        Arrays.fill(biomes, (byte) 0x04 /* Forest */);
        v1_2ChunkStorage.setBiomeData(biomes);

        byte[] compressedData = v1_2ChunkStorage.getCompressedData(groundUpContinuous, !groundUpContinuous ? bitmap : 0);

        return PacketUtil.createPacket(0x33, new TypeHolder[]{
                new TypeHolder(Type.V1_2_CHUNK, new V1_2Chunk(
                        chunkX,
                        chunkZ,
                        groundUpContinuous,
                        (short) v1_2ChunkStorage.getPrimaryBitmap(),
                        (short) 0,
                        v1_2ChunkStorage.getCompressedSize(),
                        compressedData
                ))
        });
    }
}
