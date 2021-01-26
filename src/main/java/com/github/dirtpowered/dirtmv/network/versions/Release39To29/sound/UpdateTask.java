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
package com.github.dirtpowered.dirtmv.network.versions.Release39To29.sound;

import com.github.dirtpowered.dirtmv.data.interfaces.Tickable;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.EntityTracker;
import com.github.dirtpowered.dirtmv.network.versions.Release39To29.entity.WorldEntityEvent;

import java.util.Random;

public class UpdateTask implements Tickable {

    private final ServerSession serverSession;
    private int soundTime;

    public UpdateTask(ServerSession session) {
        this.serverSession = session;
    }

    @Override
    public void tick() {
        ProtocolStorage storage = serverSession.getStorage();
        if (storage.hasObject(EntityTracker.class)) {
            EntityTracker tracker = storage.get(EntityTracker.class);

            Random shared = serverSession.getMain().getSharedRandom();

            for (Integer eId : tracker.getTrackedEntities().keySet()) {
                if (shared.nextInt(1000) < this.soundTime++) {
                    WorldEntityEvent.onUpdate(serverSession, eId);

                    this.soundTime = -80;
                }
            }
        }
    }
}
