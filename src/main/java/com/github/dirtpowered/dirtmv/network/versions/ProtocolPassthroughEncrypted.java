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

package com.github.dirtpowered.dirtmv.network.versions;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.utils.encryption.EncryptionUtils;
import lombok.SneakyThrows;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class ProtocolPassthroughEncrypted extends ServerProtocol {

    public ProtocolPassthroughEncrypted(MinecraftVersion from, MinecraftVersion to) {
        super(from, to);
    }

    @Override
    public void registerTranslators() {

        addTranslator(0xFD /* SERVER AUTH DATA */, new PacketTranslator() {

            @SneakyThrows
            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                PublicKey key = EncryptionUtils.keyPair.getPublic();
                byte[] verify = new byte[4];

                session.getMain().getSharedRandom().nextBytes(verify);

                PacketData encryptRequest = PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0xFD, new TypeHolder[]{
                        new TypeHolder(Type.STRING, "-"),
                        new TypeHolder(Type.SHORT_BYTE_ARRAY, key.getEncoded()),
                        new TypeHolder(Type.SHORT_BYTE_ARRAY, verify)
                });

                SecretKey secretKey = EncryptionUtils.getSecretKey();

                session.getUserData().setProxyRequest(encryptRequest); // proxy -> client request
                session.getUserData().setSecretKey(secretKey);

                byte[] publicKeyBytes = (byte[]) data.read(1).getObject();

                X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey;

                try {
                    KeyFactory keyFactory;

                    keyFactory = KeyFactory.getInstance("RSA");
                    publicKey = keyFactory.generatePublic(spec);

                    // fake proxy client response
                    byte[] token = (byte[]) data.read(2).getObject();
                    byte[] sharedKey = EncryptionUtils.getSharedKey(secretKey, publicKey);
                    byte[] encryptedData = EncryptionUtils.encrypt(publicKey, token);

                    PacketData response = PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0xFC, new TypeHolder[]{
                            new TypeHolder(Type.SHORT_BYTE_ARRAY, sharedKey),
                            new TypeHolder(Type.SHORT_BYTE_ARRAY, encryptedData)
                    });

                    session.sendPacket(response, PacketDirection.CLIENT_TO_SERVER, ProtocolPassthroughEncrypted.class);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                return encryptRequest;
            }
        });

        addTranslator(0xFC /* CLIENT SHARED KEY */, new PacketTranslator() {

            @SneakyThrows
            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {
                if (dir == PacketDirection.CLIENT_TO_SERVER) {
                    SecretKey shared = EncryptionUtils.getSecret(data, session.getUserData().getProxyRequest());

                    // server -> client
                    PacketData response = PacketUtil.createPacket(MinecraftVersion.R1_3_1, 0xFC, new TypeHolder[]{
                            new TypeHolder(Type.SHORT_BYTE_ARRAY, new byte[0]),
                            new TypeHolder(Type.SHORT_BYTE_ARRAY, new byte[0])
                    });

                    session.sendPacket(response, PacketDirection.SERVER_TO_CLIENT, ProtocolPassthroughEncrypted.class);

                    // enable encryption
                    EncryptionUtils.setEncryption(session.getChannel(), shared);
                    return new PacketData(-1); // cancel packet
                } else {
                    // enable client connection encryption
                    EncryptionUtils.setEncryption(session.getClientSession().getChannel(), session.getUserData().getSecretKey());

                    return new PacketData(-1); // cancel packet
                }
            }
        });
    }
}
