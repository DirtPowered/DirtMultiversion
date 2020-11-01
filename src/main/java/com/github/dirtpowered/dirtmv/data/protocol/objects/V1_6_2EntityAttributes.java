package com.github.dirtpowered.dirtmv.data.protocol.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class V1_6_2EntityAttributes {
    private int entityId;
    private List<EntityAttribute> entityAttributes;
}
