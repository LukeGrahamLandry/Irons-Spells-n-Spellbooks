package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.util.LazyValue;
import net.minecraft.item.Items;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ExtendedWeaponTiers implements IItemTier {
    KEEPER_FLAMBERGE(1000, 4, () -> Ingredient.of(Items.NETHERITE_SCRAP)),
    METAL_MAGEHUNTER(1561, 12, () -> Ingredient.of(ItemRegistry.ARCANE_INGOT.get())),
    CRYSTAL_MAGEHUNTER(1561, 12, () -> Ingredient.of(Items.DIAMOND)),
    TRUTHSEEKER(2031, 10, () -> Ingredient.of(ItemRegistry.ARCANE_INGOT.get())),
    CLAYMORE(1000, 8, () -> Ingredient.of(Items.IRON_INGOT)),
    ;

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyValue<Ingredient> repairIngredient;

    private ExtendedWeaponTiers(int pLevel, int pUses, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
        this.level = pLevel;
        this.uses = pUses;
        this.speed = 0;
        this.damage = 0;
        this.enchantmentValue = pEnchantmentValue;
        this.repairIngredient = new LazyValue<>(pRepairIngredient);
    }

    private ExtendedWeaponTiers(int pUses, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
        this(0, pUses, pEnchantmentValue, pRepairIngredient);
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public int getLevel() {
        return this.level;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }


}
