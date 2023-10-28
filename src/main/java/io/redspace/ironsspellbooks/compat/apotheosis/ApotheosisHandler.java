package io.redspace.ironsspellbooks.compat.apotheosis;

import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import shadows.apotheosis.adventure.loot.LootCategory;

public class ApotheosisHandler {

    public static final LootCategory SPELLBOOK = LootCategory.register(LootCategory.SWORD, "spellbook", s-> s.getItem() instanceof SpellBook, arr(EquipmentSlotType.MAINHAND));

    private static EquipmentSlotType[] arr(EquipmentSlotType... s) {
        return s;
    }

    public static boolean isSpellbook(ItemStack stack) { return LootCategory.forItem(stack).equals(SPELLBOOK); }

    public static void init() {}
}
