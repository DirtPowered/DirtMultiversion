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

package com.github.dirtpowered.dirtmv.viaversion.platform;

import com.github.dirtpowered.dirtmv.api.DirtServer;
import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;

import java.util.SortedSet;
import java.util.UUID;

public class DirtViaApi implements ViaAPI<DirtServer> {

    public DirtViaApi(DirtServer api) {

    }

    @Override
    public int getPlayerVersion(DirtServer dirtServer) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public int getPlayerVersion(UUID uuid) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public boolean isInjected(UUID uuid) {
        return true;
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public void sendRawPacket(DirtServer dirtServer, ByteBuf byteBuf) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public void sendRawPacket(UUID uuid, ByteBuf byteBuf) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public BossBar createBossBar(String s, BossColor bossColor, BossStyle bossStyle) {
        return new DirtBossbar(s, 0.1F, bossColor, bossStyle);
    }

    @Override
    public BossBar createBossBar(String s, float v, BossColor bossColor, BossStyle bossStyle) {
        return new DirtBossbar(s, v, bossColor, bossStyle);
    }

    @Override
    public SortedSet<Integer> getSupportedVersions() {
        throw new UnsupportedOperationException("not supported yet");
    }
}
