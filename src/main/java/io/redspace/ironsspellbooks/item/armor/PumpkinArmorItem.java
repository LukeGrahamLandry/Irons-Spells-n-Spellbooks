package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Item.Properties;

public class PumpkinArmorItem extends ExtendedArmorItem {
    public PumpkinArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.PUMPKIN, slot, settings);
    }

    @Override
    public boolean isEnderMask(ItemStack stack, PlayerEntity player, EndermanEntity endermanEntity) {
        return player.getItemBySlot(EquipmentSlotType.HEAD).is(this);
    }
}
