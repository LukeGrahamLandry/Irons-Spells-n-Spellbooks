package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.item.Item.Properties;

public class ShrivingStoneItem extends Item {
    private static final ITextComponent description = ITextComponent.translatable("item.irons_spellbooks.shriving_stone_desc").withStyle(TextFormatting.GRAY);
    public ShrivingStoneItem() {
        super(new Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> lines, ITooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, lines, pIsAdvanced);
        lines.add(description);
    }
}
