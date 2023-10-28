package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.item.Item.Properties;

public class PyromancerArmorItem extends ExtendedArmorItem{
    public PyromancerArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.PYROMANCER, slot, settings);
    }
}
