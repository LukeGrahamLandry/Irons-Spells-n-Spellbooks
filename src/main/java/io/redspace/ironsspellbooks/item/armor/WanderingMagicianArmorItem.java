package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.item.Item.Properties;

public class WanderingMagicianArmorItem extends ExtendedArmorItem {
    public WanderingMagicianArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.WANDERING_MAGICIAN, slot, settings);
    }
}