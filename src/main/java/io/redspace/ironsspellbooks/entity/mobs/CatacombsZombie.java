package io.redspace.ironsspellbooks.entity.mobs;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class CatacombsZombie extends ZombieEntity {
    //This is a wrapper class that in reality creates a vanilla zombie, but with some cool stuff thrown on top
    public CatacombsZombie(EntityType<? extends ZombieEntity> pEntityType, World pLevel) {
        super(EntityType.ZOMBIE, pLevel);
        if (this.random.nextFloat() < .2f) {
            switch (this.random.nextIntBetweenInclusive(1, 4)) {

                case 1 -> addEffect(new EffectInstance(Effects.INVISIBILITY, Integer.MAX_VALUE));
                case 2 -> addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1));
                case 3 -> addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                case 4 -> addEffect(new EffectInstance(Effects.DAMAGE_BOOST, Integer.MAX_VALUE));
            }

        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(random, pDifficulty);
        Item[] leather = {Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET};
        Item[] chain = {Items.CHAINMAIL_BOOTS, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_HELMET};
        Item[] iron = {Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET};

        float power = random.nextFloat();
        ItemStack[] equipment = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            if (random.nextFloat() > .60f) {
                equipment[i] = ItemStack.EMPTY;
            } else {
                float stray = (random.nextFloat() - .5f) / 3;
                if (power + stray > .85)
                    equipment[i] = new ItemStack(iron[i]);
                else if (power + stray > .45)
                    equipment[i] = new ItemStack(chain[i]);
                else
                    equipment[i] = new ItemStack(leather[i]);
            }

        }
        setItemSlot(EquipmentSlotType.FEET, equipment[0]);
        setItemSlot(EquipmentSlotType.LEGS, equipment[1]);
        setItemSlot(EquipmentSlotType.CHEST, equipment[2]);
        setItemSlot(EquipmentSlotType.HEAD, equipment[3]);
        if (random.nextFloat() < .01f)
            setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Items.CYAN_BANNER));

        setDropChance(EquipmentSlotType.FEET, 0.0F);
        setDropChance(EquipmentSlotType.LEGS, 0.0F);
        setDropChance(EquipmentSlotType.CHEST, 0.0F);
        setDropChance(EquipmentSlotType.HEAD, 0.0F);
    }
}
