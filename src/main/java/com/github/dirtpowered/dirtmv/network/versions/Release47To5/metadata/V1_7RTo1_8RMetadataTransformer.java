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

package com.github.dirtpowered.dirtmv.network.versions.Release47To5.metadata;

import com.github.dirtpowered.dirtmv.data.entity.EntityType;
import com.github.dirtpowered.dirtmv.data.entity.ObjectType;
import com.github.dirtpowered.dirtmv.data.entity.SpawnableObject;
import com.github.dirtpowered.dirtmv.data.protocol.objects.MetadataType;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.data.transformers.MetadataTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class V1_7RTo1_8RMetadataTransformer implements MetadataTransformer {

    @Override
    public WatchableObject[] transformMetadata(SpawnableObject entityType, WatchableObject[] watchableObjects) {
        List<WatchableObject> newMetaData = new ArrayList<>();

        for (WatchableObject watchableObject : watchableObjects) {
            MetadataType type = watchableObject.getType();
            Object value = watchableObject.getValue();

            int index = watchableObject.getIndex();

            if (entityType.isLivingEntity()) {
                if (type == MetadataType.BYTE && index == 11) {
                    // nametag visibility
                    newMetaData.add(new WatchableObject(MetadataType.BYTE, 3, value));
                } else if (type == MetadataType.INT && index == 12) {
                    // ageable entities
                    int age = (int) value;
                    newMetaData.add(new WatchableObject(MetadataType.BYTE, 12, (byte) (age < 0 ? -1 : 0)));
                } else if (entityType == EntityType.ENDER_MAN && type == MetadataType.BYTE && index == 16) {
                    // enderman carried item
                    newMetaData.add(new WatchableObject(MetadataType.SHORT, 16, ((Byte) value).shortValue()));
                } else if (entityType == EntityType.HUMAN) {
                    if (type == MetadataType.BYTE && index == 16) {
                        // TODO: check for metadata changes
                    } else {
                        newMetaData.add(watchableObject);
                    }
                } else if (type == MetadataType.STRING && index == 10) {
                    // custom display name
                    newMetaData.add(new WatchableObject(MetadataType.STRING, 2, value));
                } else {
                    newMetaData.add(watchableObject);
                }
            } else {
                if (entityType == ObjectType.ITEM_FRAME) {
                    if (type == MetadataType.BYTE && index == 3) {
                        newMetaData.add(new WatchableObject(MetadataType.BYTE, 9, value));
                    } else if (type == MetadataType.ITEM && index == 2) {
                        newMetaData.add(new WatchableObject(MetadataType.ITEM, 8, value));
                    }
                } else {
                    newMetaData.add(watchableObject);
                }
            }
        }

        if (newMetaData.isEmpty()) {
            List<WatchableObject> defaultMetadata = Arrays.asList(
                    new WatchableObject(MetadataType.BYTE, 0, (byte) 0),
                    new WatchableObject(MetadataType.SHORT, 1, 300),
                    new WatchableObject(MetadataType.BYTE, 4, (byte) 0)
            );

            newMetaData.addAll(defaultMetadata);
        }

        return newMetaData.toArray(new WatchableObject[0]);
    }
}
