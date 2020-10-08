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

package com.github.dirtpowered.dirtmv.network.data.registry;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.versions.Beta14To13.ProtocolBeta14To13;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.ProtocolBeta17to14;
import com.github.dirtpowered.dirtmv.network.versions.ProtocolPassthrough;
import com.github.dirtpowered.dirtmv.network.versions.ProtocolPassthroughEncrypted;
import com.github.dirtpowered.dirtmv.network.versions.Release22To17.ProtocolRelease22To17;
import com.github.dirtpowered.dirtmv.network.versions.Release23To22.ProtocolRelease23To22;
import com.github.dirtpowered.dirtmv.network.versions.Release28To23.ProtocolRelease28To23;
import com.github.dirtpowered.dirtmv.network.versions.Release29To28.ProtocolRelease29To28;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranslatorRegistry {

    @Getter
    private Map<Integer, ServerProtocol> protocols = new ConcurrentHashMap<>();

    public TranslatorRegistry() {
        registerProtocol(new ProtocolRelease29To28());
        registerProtocol(new ProtocolRelease28To23());
        registerProtocol(new ProtocolRelease22To17());
        registerProtocol(new ProtocolRelease23To22());
        registerProtocol(new ProtocolBeta17to14());
        registerProtocol(new ProtocolBeta14To13());
    }

    private void registerProtocol(ServerProtocol serverProtocol) {
        int clientProtocol = serverProtocol.getFrom().getProtocolId();

        protocols.put(clientProtocol, serverProtocol);
    }

    /**
     * Returns all protocols between client and server version
     *
     * @param from      Client version
     * @param versionTo Server version
     * @return {@link List<ServerProtocol> List} with ordered protocol pipeline classes
     */
    public List<ServerProtocol> findProtocol(MinecraftVersion from, MinecraftVersion versionTo) {
        List<ServerProtocol> serverProtocols = new LinkedList<>();

        // check if translating is needed
        if (from == versionTo) {
            ServerProtocol serverProtocol;

            // starting from r1.3 the whole connections is encrypted
            if (from.getProtocolId() >= 39) {
                serverProtocol = new ProtocolPassthroughEncrypted(from, versionTo);
            } else {
                serverProtocol = new ProtocolPassthrough(from, versionTo);
            }
            return Collections.singletonList(serverProtocol);
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
