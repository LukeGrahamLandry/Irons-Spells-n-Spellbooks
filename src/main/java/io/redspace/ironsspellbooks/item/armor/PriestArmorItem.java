package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.item.Item.Properties;

public class PriestArmorItem extends ExtendedArmorItem{
    public PriestArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.PRIEST, slot, settings);
    }
}
