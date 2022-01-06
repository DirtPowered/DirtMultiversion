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

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.data.interfaces.Tickable;
import com.github.dirtpowered.dirtmv.data.protocol.PacketData;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.objects.BlockLocation;
import com.github.dirtpowered.dirtmv.data.translator.PacketDirection;
import com.github.dirtpowered.dirtmv.data.user.ProtocolStorage;
import com.github.dirtpowered.dirtmv.data.utils.PacketUtil;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;
import com.github.dirtpowered.dirtmv.network.versions.Beta17To14.storage.BlockStorage;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.ProtocolRelease47To5;
import com.github.dirtpowered.dirtmv.network.versions.Release47To5.inventory.QuickBarTracker;

public class BlockMiningTimeFixer implements Tickable, PlayerBlockAction {
    private final ServerSession session;
    private final ProtocolStorage storage;
    private int currentMiningTicks;
    private BlockLocation pos;
    private int blockId;
    private int lastBreakingStage;
    private int randomEid;

    public BlockMiningTimeFixer(ServerSession session) {
        this.session = session;
        this.storage = session.getStorage();
        this.currentMiningTicks = 0;
    }

    private int getItemInHand() {
        if (storage.hasObject(QuickBarTracker.class)) {
            QuickBarTracker quickBarTracker = storage.get(QuickBarTracker.class);
            return quickBarTracker.getItemInHand().getItemId();
        }
        return 0;
    }

    private void cancelCracks() {
        if (pos != null) {
            sendBreakingEffect(pos, -1);
        }
    }

    private void sendBlockCracks() {
        int originalTime = HardnessTable.getMiningTicks(blockId, getItemInHand());
        int flag = (currentMiningTicks * 10 / originalTime);

        int stage;

        if (flag != lastBreakingStage) {
            stage = 9 - flag;

            if (pos != null) {
                sendBreakingEffect(pos, stage);
            }
        }

        this.lastBreakingStage = flag;
    }

    private void finishBreaking() {
        // it's b1.7.3 packet, so it skips all translators
        PacketData blockDig = PacketUtil.createPacket(0x0E, new TypeHolder[]{
                new TypeHolder<>(Type.BYTE, (byte) 2),
                new TypeHolder<>(Type.INT, pos.getX()),
                new TypeHolder<>(Type.BYTE, (byte) pos.getY()),
                new TypeHolder<>(Type.INT, pos.getZ()),
                new TypeHolder<>(Type.BYTE, (byte) 0)
        });

        // send raw packet
        session.getClientSession().sendPacket(blockDig);

        // send block break effect
        PacketData blockEffect = PacketUtil.createPacket(0x28, new TypeHolder[]{
                new TypeHolder<>(Type.INT, 2001),
                new TypeHolder<>(Type.LONG, ProtocolRelease47To5.toBlockPosition(pos.getX(), pos.getY(), pos.getZ())),
                new TypeHolder<>(Type.INT, blockId),
                new TypeHolder<>(Type.BOOLEAN, false)
        });

        session.sendPacket(blockEffect, PacketDirection.TO_CLIENT, MinecraftVersion.R1_8);
    }

    private void removeSlownessEffect() {
        sendEffect(true);
    }

    private void sendSlownessEffect() {
        sendEffect(false);
    }

    @Override
    public void tick() {
        if (currentMiningTicks != 0) {
            currentMiningTicks--;

            sendBlockCracks();
        } else {
            if (pos != null) {
                cancelCracks();
                finishBreaking();
                removeSlownessEffect();

                this.pos = null;
            }
        }
    }

    @Override
    public void onBlockStartBreaking(BlockLocation position) {
        if (!storage.hasObject(BlockStorage.class)) {
            return;
        }

        BlockStorage blockStorage = storage.get(BlockStorage.class);
        int typeId = blockStorage.getBlockAt(position.getX(), position.getY(), position.getZ());

        if (HardnessTable.exist(typeId)) {
            this.currentMiningTicks = HardnessTable.getMiningTicks(typeId, getItemInHand());
            this.pos = position;
            this.blockId = typeId;
            this.randomEid = session.getMain().getSharedRandom().nextInt(100);

            sendSlownessEffect();
        }
    }

    @Override
    public void onBlockCancelBreaking(BlockLocation position) {
        if (HardnessTable.exist(blockId)) {
            removeSlownessEffect();
            cancelCracks();

            this.currentMiningTicks = 0;
            this.pos = null;
        }
    }

    private void sendBreakingEffect(BlockLocation position, int stage) {

        session.sendPacket(PacketUtil.createPacket(0x25, new TypeHolder[]{
                new TypeHolder<>(Type.VAR_INT, randomEid),
                new TypeHolder<>(Type.LONG, ProtocolRelease47To5.toBlockPosition(pos.getX(), position.getY(), pos.getZ())),
                new TypeHolder(Type.BYTE, stage)
        }), PacketDirection.TO_CLIENT, MinecraftVersion.R1_8);
    }

    private void sendEffect(boolean remove) {
        PacketData addEffect = PacketUtil.createPacket(0x1D /* add */, new TypeHolder[]{
                new TypeHolder<>(Type.VAR_INT, session.getUserData().getEntityId()),
                new TypeHolder<>(Type.BYTE, (byte) 4),
                new TypeHolder<>(Type.BYTE, (byte) -1),
                new TypeHolder<>(Type.VAR_INT, 1),
                new TypeHolder<>(Type.BOOLEAN, true),
        });

        PacketData removeEffect = PacketUtil.createPacket(0x1E /* remove */, new TypeHolder[]{
                new TypeHolder<>(Type.VAR_INT, session.getUserData().getEntityId()),
                new TypeHolder<>(Type.BYTE, (byte) 4)
        });

        if (remove) {
            session.sendPacket(removeEffect, PacketDirection.TO_CLIENT, MinecraftVersion.R1_8);
        } else {
            session.sendPacket(addEffect, PacketDirection.TO_CLIENT, MinecraftVersion.R1_8);
        }
    }
}