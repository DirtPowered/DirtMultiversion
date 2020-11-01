package com.github.dirtpowered.dirtmv.data.protocol.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AttributeModifier {
    private UUID uuid;
    private double amount;
    private int operation;
}