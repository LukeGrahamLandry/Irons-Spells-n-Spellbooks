package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.item.Item.Properties;

public class CultistArmorItem extends ExtendedArmorItem{
    public CultistArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.CULTIST, slot, settings);
    }
}
