package io.redspace.ironsspellbooks.util;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class SpellbookModCreativeTabs {
    public static final ItemGroup SPELL_MATERIALS_TAB = new ItemGroup("spell_materials_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistry.DIVINE_PEARL.get());
        }
    };
    public static final ItemGroup SPELL_EQUIPMENT_TAB = new ItemGroup("spell_equipment_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistry.IRON_SPELL_BOOK.get());
        }
    };
}
