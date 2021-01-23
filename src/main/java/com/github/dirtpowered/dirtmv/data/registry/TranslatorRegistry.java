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

package com.github.dirtpowered.dirtmv.data.registry;

import com.github.dirtpowered.dirtmv.DirtMultiVersion;
import com.github.dirtpowered.dirtmv.api.Configuration;
import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.translator.ServerProtocol;
import com.github.dirtpowered.dirtmv.data.user.UserData;
import com.github.dirtpowered.dirtmv.network.versions.ProtocolPassthrough;
import com.github.dirtpowered.dirtmv.network.versions.ProtocolPassthroughEncrypted;
import com.github.dirtpowered.dirtmv.network.versions.ProtocolStateHandler;
import com.github.dirtpowered.dirtmv.network.versions.handler.GlobalProtocolHandler;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranslatorRegistry {

    @Getter
    private final Map<Integer, ServerProtocol> protocols = new ConcurrentHashMap<>();

    private final DirtMultiVersion main;

    public TranslatorRegistry(DirtMultiVersion main) {
        this.main = main;
    }

    public void registerTranslator(ServerProtocol serverProtocol) {
        int clientProtocol = serverProtocol.getFrom().getRegistryId();

        protocols.put(clientProtocol, serverProtocol);
    }

    /**
     * Returns all protocols translators between client and server version
     *
     * @param data      User data
     * @param versionTo Server version
     * @return {@link List<ServerProtocol> List} with ordered protocol pipeline classes
     */
    public List<ServerProtocol> findProtocol(UserData data, MinecraftVersion versionTo) {
        List<ServerProtocol> serverProtocols = new LinkedList<>();

        MinecraftVersion from = data.getClientVersion();
        Configuration c = main.getConfiguration();

        GlobalProtocolHandler globalProtocolHandler = new GlobalProtocolHandler(from, versionTo);

        // check if translating is needed
        if (from == c.getServerVersion()) {
            ServerProtocol serverProtocol;

            // starting from r1.3 the whole connections is encrypted
            if (from.getRegistryId() >= MinecraftVersion.R1_3_1.getRegistryId() && !from.isNettyProtocol()) {
                serverProtocol = new ProtocolPassthroughEncrypted(from, versionTo);
            } else if (from.isNettyProtocol()) {
                serverProtocol = new ProtocolStateHandler(from, versionTo);
            } else {
                serverProtocol = new ProtocolPassthrough(from, versionTo);
            }

            return Arrays.asList(serverProtocol, globalProtocolHandler);
        } else {
            if (from.getRegistryId() < 39) {
                serverProtocols.add(new ProtocolPassthrough(from, versionTo));
            }

            if ((from.getRegistryId() >= 39 && c.getServerVersion().getRegistryId() >= 39) && !from.isNettyProtocol()) {
                // add encryption translators to pipeline
                serverProtocols.add(new ProtocolPassthroughEncrypted(from, versionTo));
            }
        }

        int clientProtocol = from.getRegistryId();
        int serverProtocol = versionTo.getRegistryId();

        for (int i = serverProtocol; i <= clientProtocol; i++) {
            if (MinecraftVersion.fromRegistryId(i) != null) {

                ServerProtocol target = protocols.get(i);

                if (target != null && !(i <= serverProtocol)) {
                    serverProtocols.add(target);
                }
            }
        }

        if (from.isNettyProtocol()) {
            serverProtocols.add(new ProtocolStateHandler(from, versionTo));
        }

        // track packets in all protocols
        serverProtocols.add(globalProtocolHandler);

        return serverProtocols;
    }

    /**
     * Gets all protocols between given versions
     *
     * @param server - server version
     * @param client - client version
     *
     * @return List of {@link ServerProtocol protocol translators}
     */
    public List<ServerProtocol> getAllProtocolsBetween(MinecraftVersion server, MinecraftVersion client) {
        int serverProtocol = server.getRegistryId();
        int clientProtocol = client.getRegistryId();

        List<ServerProtocol> serverProtocols = new LinkedList<>();

        for (int i = serverProtocol; i <= clientProtocol; i++) {
            if (MinecraftVersion.fromRegistryId(i) != null) {
                ServerProtocol target = protocols.get(i);

                if (target != null && !(i <= serverProtocol)) {
                    serverProtocols.add(target);
                }
            }
        }
        return serverProtocols;
    }
}
