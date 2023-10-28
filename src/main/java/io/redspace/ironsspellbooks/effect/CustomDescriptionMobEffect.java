package io.redspace.ironsspellbooks.effect;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomDescriptionMobEffect extends Effect {
    protected CustomDescriptionMobEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static void handleCustomPotionTooltip(ItemStack itemStack, List<ITextComponent> tooltipLines, boolean isAdvanced, EffectInstance mobEffectInstance, CustomDescriptionMobEffect customDescriptionMobEffect) {
        var description = customDescriptionMobEffect.getDescriptionLine(mobEffectInstance);

        var header = net.minecraft.util.text.ITextComponent.translatable("potion.whenDrank").withStyle(TextFormatting.DARK_PURPLE);
        var newLines = new ArrayList<ITextComponent>();
        int i = tooltipLines.indexOf(header);

        if (i < 0) {
            newLines.add(net.minecraft.util.text.ITextComponent.empty());
            newLines.add(header);
            newLines.add(description);
            i = isAdvanced ? tooltipLines.size() - (itemStack.hasTag() ? 2 : 1) : tooltipLines.size();
        } else {
            newLines.add(description);
            i++;
        }
        tooltipLines.addAll(i, newLines);
    }

    public abstract ITextComponent getDescriptionLine(EffectInstance instance);
}
