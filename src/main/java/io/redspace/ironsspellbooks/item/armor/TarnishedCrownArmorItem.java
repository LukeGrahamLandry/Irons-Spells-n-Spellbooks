package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.item.Item.Properties;

public class TarnishedCrownArmorItem extends ExtendedArmorItem {
    public TarnishedCrownArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.TARNISHED, slot, settings);
    }
}
