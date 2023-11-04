package io.redspace.ironsspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.world.World;

public class MagehunterVindicator extends VindicatorEntity {
    //This is a wrapper class that in reality creates a vanilla Vindicator, but with the Magehunter sword
    public MagehunterVindicator(EntityType<? extends VindicatorEntity> pEntityType, World pLevel) {
        super(EntityType.VINDICATOR, pLevel);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(pDifficulty);
        ItemStack magehunter = new ItemStack(ItemRegistry.MAGEHUNTER.get());

        magehunter.enchant(Enchantments.SHARPNESS, 5);

        setItemSlot(EquipmentSlotType.MAINHAND, magehunter);
    }
}
