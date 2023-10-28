package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.item.Item.Properties;

public class PlaguedArmorItem extends ExtendedArmorItem{
    public PlaguedArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.PLAGUED, slot, settings);
    }
}
