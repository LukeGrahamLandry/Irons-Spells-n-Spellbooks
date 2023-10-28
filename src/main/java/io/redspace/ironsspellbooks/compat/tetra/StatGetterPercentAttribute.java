package io.redspace.ironsspellbooks.compat.tetra;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.gui.stats.getter.StatGetterAttribute;

public class StatGetterPercentAttribute extends StatGetterAttribute {
    public StatGetterPercentAttribute(Attribute attribute) {
        super(attribute);
        withOffset(-1);
    }

    @Override
    public double getValue(PlayerEntity player, ItemStack itemStack) {
        return 100 * (super.getValue(player, itemStack));
    }

    @Override
    public double getValue(PlayerEntity player, ItemStack itemStack, String slot) {
        return 100 * (super.getValue(player, itemStack, slot));
    }

    @Override
    public double getValue(PlayerEntity player, ItemStack itemStack, String slot, String improvement) {
        return 100 * (super.getValue(player, itemStack, slot, improvement));
    }

    @Override
    public boolean shouldShow(PlayerEntity player, ItemStack currentStack, ItemStack previewStack) {
        double baseValue = 0;
        double currentValue = this.getValue(player, currentStack);
        return currentValue != baseValue || this.getValue(player, previewStack) != currentValue;
    }
}
