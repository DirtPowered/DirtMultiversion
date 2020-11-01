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

package com.github.dirtpowered.dirtmv.data.registry;

import com.github.dirtpowered.dirtmv.data.Constants;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.network.versions.ProtocolPassthrough;
import com.github.dirtpowered.dirtmv.network.versions.ProtocolPassthroughEncrypted;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranslatorRegistry {

    @Getter
    private Map<Integer, ServerProtocol> protocols = new ConcurrentHashMap<>();

    public void registerProtocol(ServerProtocol serverProtocol) {
        int clientProtocol = serverProtocol.getFrom().getProtocolId();

        protocols.put(clientProtocol, serverProtocol);
    }

    /**
     * Returns all protocols between client and server version
     * @param data      User data
     * @param versionTo Server version
     * @return {@link List<ServerProtocol> List} with ordered protocol pipeline classes
     */
    public List<ServerProtocol> findProtocol(UserData data, MinecraftVersion versionTo) {
        List<ServerProtocol> serverProtocols = new LinkedList<>();

        MinecraftVersion from = data.getClientVersion();

        // check if translating is needed
        if (from == Constants.REMOTE_SERVER_VERSION) {
            ServerProtocol serverProtocol;

            // starting from r1.3 the whole connections is encrypted
            if (from.getProtocolId() >= MinecraftVersion.R1_3_1.getProtocolId()) {
                serverProtocol = new ProtocolPassthroughEncrypted(from, versionTo);
            } else {
                serverProtocol = new ProtocolPassthrough(from, versionTo);
            }

            return Collections.singletonList(serverProtocol);
        } else {
            if (from.getProtocolId() >= 39 && Constants.REMOTE_SERVER_VERSION.getProtocolId() >= 39) {
                // add encryption translators to pipeline
                serverProtocols.add(new ProtocolPassthroughEncrypted(from, versionTo));
            }
        }

        int clientProtocol = from.getProtocolId();
        int serverProtocol = versionTo.getProtocolId();

        for (int i = serverProtocol; i <= clientProtocol; i++) {
            if (MinecraftVersion.fromProtocolVersion(i) != null) {

                ServerProtocol target = protocols.get(i);

                if (target != null && !(i <= serverProtocol)) {
                    serverProtocols.add(target);
                }
            }
        }

        return serverProtocols;
    }
}
