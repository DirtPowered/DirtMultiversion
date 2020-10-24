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

package com.github.dirtpowered.dirtmv.data.protocol.types.entity;

import com.github.dirtpowered.dirtmv.data.protocol.DataType;
import com.github.dirtpowered.dirtmv.data.protocol.Type;
import com.github.dirtpowered.dirtmv.data.protocol.TypeHolder;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketInput;
import com.github.dirtpowered.dirtmv.data.protocol.io.model.PacketOutput;
import com.github.dirtpowered.dirtmv.data.protocol.objects.Motion;

public class VehicleMotionDataType extends DataType<Motion> {

    public VehicleMotionDataType() {
        super(Type.MOTION);
    }

    @Override
    public Motion read(PacketInput packetInput) {
        int throwerId = packetInput.readInt();

        short motionX = 0;
        short motionY = 0;
        short motionZ = 0;

        if (throwerId > 0) {
            motionX = packetInput.readShort();
            motionY = packetInput.readShort();
            motionZ = packetInput.readShort();
        }

        return new Motion(throwerId, motionX, motionY, motionZ);
    }

    @Override
    public void write(TypeHolder typeHolder, PacketOutput packetOutput) {
        Motion motion = (Motion) typeHolder.getObject();

        packetOutput.writeInt(motion.getThrowerId());

        if (motion.getThrowerId() > 0) {
            packetOutput.writeShort(motion.getMotionX());
            packetOutput.writeShort(motion.getMotionY());
            packetOutput.writeShort(motion.getMotionZ());
        }
    }
}