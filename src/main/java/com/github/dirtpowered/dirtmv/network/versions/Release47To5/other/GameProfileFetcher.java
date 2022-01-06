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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5.other;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.dirtpowered.dirtmv.data.utils.ChatUtils;
import com.google.common.base.Charsets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.pmw.tinylog.Logger;

import java.net.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GameProfileFetcher {
    private static final AsyncLoadingCache<String, GameProfile> skinCache = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .buildAsync(GameProfileFetcher::fetchBlocking);

    public static CompletableFuture<GameProfile> getProfile(String username) {
        return skinCache.get(ChatUtils.stripColor(username));
    }

    private static GameProfile fetchBlocking(String username) throws ExecutionException, InterruptedException {
        HttpAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY);

        MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
        GameProfileRepository service = authenticationService.createProfileRepository();

        CompletableFuture<GameProfile> completableFuture = new CompletableFuture<>();

        service.findProfilesByNames(new String[]{username}, Agent.MINECRAFT, new ProfileLookupCallback() {

            @Override
            public void onProfileLookupSucceeded(GameProfile gameProfile) {
                sessionService.fillProfileProperties(gameProfile, true);
                completableFuture.complete(gameProfile);
            }

            @Override
            public void onProfileLookupFailed(GameProfile gameProfile, Exception e) {
                Logger.warn("unable to fetch profile for {}, e: {}", username, e.getMessage());
                GameProfile offlineProfile = createOfflineProfile(username);

                completableFuture.complete(offlineProfile);
            }
        });

        return completableFuture.get();
    }

    private static GameProfile createOfflineProfile(String username) {
        return new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8)), username);
    }
}