package com.github.dirtpowered.dirtmv.data.protocol.objects;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EntityAttribute {
    private String name;
    private Double value;
    private List<AttributeModifier> attributeModifiers;

    public EntityAttribute(String name, Double value) {
        this.name = name;
        this.value = value;

        this.attributeModifiers = new ArrayList<>();
    }

    public void addAttribute(AttributeModifier attributeModifier) {
        this.attributeModifiers.add(attributeModifier);
    }
}
