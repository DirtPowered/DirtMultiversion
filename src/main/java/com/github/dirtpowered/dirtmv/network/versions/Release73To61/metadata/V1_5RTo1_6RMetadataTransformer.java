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

package com.github.dirtpowered.dirtmv.network.versions.Release73To61.metadata;

import com.github.dirtpowered.dirtmv.data.entity.EntityType;
import com.github.dirtpowered.dirtmv.data.entity.SpawnableObject;
import com.github.dirtpowered.dirtmv.data.protocol.objects.MetadataType;
import com.github.dirtpowered.dirtmv.data.protocol.objects.WatchableObject;
import com.github.dirtpowered.dirtmv.data.transformers.MetadataTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class V1_5RTo1_6RMetadataTransformer implements MetadataTransformer {

    @Override
    public WatchableObject[] transformMetadata(SpawnableObject entityType, WatchableObject[] watchableObjects) {
        List<WatchableObject> newMetaData = new ArrayList<>();

        for (WatchableObject watchableObject : watchableObjects) {
            MetadataType type = watchableObject.getType();
            Object value = watchableObject.getValue();

            int index = watchableObject.getIndex();

            if (entityType.isLivingEntity()) {
                if (index == 8) {
                    // potion color
                    newMetaData.add(new WatchableObject(type, 7, value));
                } else if (index == 9) {
                    // potion ambient
                    newMetaData.add(new WatchableObject(type, 8, value));
                } else if (index == 10) {
                    // arrow count in entity
                    newMetaData.add(new WatchableObject(type, 9, value));
                } else if (index == 5) {
                    // name tag
                    newMetaData.add(new WatchableObject(type, 10, value));
                } else if (index == 6) {
                    // name tag visibility
                    newMetaData.add(new WatchableObject(type, 11, value));

                } else if (entityType == EntityType.WITHER | entityType == EntityType.ENDER_DRAGON) {
                    if (index == 16) {
                        newMetaData.add(new WatchableObject(MetadataType.FLOAT, 6, ((Integer)value).floatValue()));
                    } else {
                        newMetaData.add(watchableObject);
                    }
                } else if (entityType == EntityType.WOLF) {
                    if (index == 18) {
                        newMetaData.add(new WatchableObject(MetadataType.FLOAT, 18, ((Integer) value).floatValue()));
                    } else {
                        newMetaData.add(watchableObject);
                    }
                } else if (entityType == EntityType.HUMAN) {
                    if (index == 17) {
                        // absorption amount
                        if (value instanceof Byte) {
                            newMetaData.add(new WatchableObject(MetadataType.FLOAT, 17, ((Byte) value).floatValue()));
                        } else if (value instanceof Integer) {
                            newMetaData.add(new WatchableObject(MetadataType.FLOAT, 17, ((Integer) value).floatValue()));
                        }
                    } else {
                        newMetaData.add(watchableObject);
                    }
                } else {
                    newMetaData.add(watchableObject);
                }
            } else {
                newMetaData.add(watchableObject);
            }
        }

        if (newMetaData.isEmpty()) {
            List<WatchableObject> defaultMetadata = Arrays.asList(
                    new WatchableObject(MetadataType.BYTE, 0, 0),
                    new WatchableObject(MetadataType.SHORT, 1, 300)
            );

            newMetaData.addAll(defaultMetadata);
        }

        return newMetaData.toArray(new WatchableObject[0]);
    }
}
