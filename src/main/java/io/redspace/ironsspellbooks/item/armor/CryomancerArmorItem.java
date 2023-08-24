package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.item.Item.Properties;

public class CryomancerArmorItem extends ExtendedArmorItem{
    public CryomancerArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.CRYOMANCER, slot, settings);
    }
}
