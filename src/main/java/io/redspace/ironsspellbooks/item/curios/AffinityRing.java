package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.item.Item.Properties;

public class AffinityRing extends SimpleDescriptiveCurio {
    public static final String nbtKey = "ISBEnhance";

    public AffinityRing(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> tooltip, ITooltipFlag pIsAdvanced) {
        var spell = RingData.getRingData(pStack).getSpell();
        if (spell != SpellType.NONE_SPELL) {
            tooltip.add(ITextComponent.empty());
            tooltip.add(ITextComponent.translatable("curios.modifiers.ring").withStyle(TextFormatting.GOLD));
            tooltip.add(ITextComponent.translatable("tooltip.irons_spellbooks.enhance_spell_level", spell.getDisplayName().withStyle(spell.getSchoolType().getDisplayName().getStyle())).withStyle(TextFormatting.YELLOW));
        }
    }

    @Override
    public ITextComponent getName(ItemStack pStack) {
        return ITextComponent.translatable(this.getDescriptionId(pStack), RingData.getRingData(pStack).getSpell().getSchoolType().getDisplayName().getString());
    }
}
