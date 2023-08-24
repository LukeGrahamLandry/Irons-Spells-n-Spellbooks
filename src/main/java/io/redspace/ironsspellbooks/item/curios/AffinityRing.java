package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AffinityRing extends SimpleDescriptiveCurio {
    public static final String nbtKey = "ISBEnhance";

    public AffinityRing(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> tooltip, ITooltipFlag pIsAdvanced) {
        var spell = RingData.getRingData(pStack).getSpell();
        if (spell != SpellType.NONE_SPELL) {
            tooltip.add(StringTextComponent.EMPTY);
            tooltip.add(new TranslationTextComponent("curios.modifiers.ring").withStyle(TextFormatting.GOLD));
            tooltip.add(new TranslationTextComponent("tooltip.irons_spellbooks.enhance_spell_level", spell.getDisplayName().withStyle(spell.getSchoolType().getDisplayName().getStyle())).withStyle(TextFormatting.YELLOW));
        }
    }

    @Override
    public ITextComponent getName(ItemStack pStack) {
        return new TranslationTextComponent(this.getDescriptionId(pStack), RingData.getRingData(pStack).getSpell().getSchoolType().getDisplayName().getString());
    }
}
