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

import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_2Chunk;
import com.github.dirtpowered.dirtmv.data.protocol.objects.V1_8Chunk;
import com.github.dirtpowered.dirtmv.data.translator.PacketTranslator;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

public class V1_3ToV1_8ChunkTranslator extends PacketTranslator {

    @Override
    public PacketData translate(ServerSession session, PacketData data) {
        V1_2Chunk chunk = data.read(Type.V1_3_CHUNK, 0);

        int chunkX = chunk.getChunkX();
        int chunkZ = chunk.getChunkZ();

        short bitmap = chunk.getPrimaryBitmap();
        boolean groundUp = chunk.isGroundUp();

        // TODO: translate
        byte[] chunkData = chunk.getData();

        V1_8Chunk newChunk = new V1_8Chunk(chunkX, chunkZ, groundUp, bitmap, new byte[0]);

        return PacketUtil.createPacket(0x21, new TypeHolder[] {
                new TypeHolder(Type.V1_8R_CHUNK, newChunk)
        });
    }
}
