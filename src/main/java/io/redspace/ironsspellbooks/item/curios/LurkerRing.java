package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.item.ItemStack;

import java.util.List;

import static io.redspace.ironsspellbooks.api.registry.AttributeRegistry.COOLDOWN_REDUCTION;

import net.minecraft.item.Item.Properties;

public class LurkerRing extends SimpleDescriptiveCurio {
    public static final int COOLDOWN_IN_TICKS = 15 * 20;
    public static final float MULTIPLIER = 1.5f;

    public LurkerRing() {
        super(new Properties().stacksTo(1), "ring");
    }


    @Override
    public List<ITextComponent> getDescriptionLines(ItemStack stack) {
        double playerCooldownModifier = Minecraft.getInstance().player == null ? 1 : Minecraft.getInstance().player.getAttributeValue(COOLDOWN_REDUCTION.get());

        return List.of(
                new TranslationTextComponent("tooltip.irons_spellbooks.passive_ability", Utils.timeFromTicks((float) (COOLDOWN_IN_TICKS * (2 - Utils.softCapFormula(playerCooldownModifier))), 1)).withStyle(TextFormatting.GREEN),
                getDescription(stack)
        );
    }

    @Override
    public ITextComponent getDescription(ItemStack stack) {
        return new StringTextComponent(" ").append(new TranslationTextComponent(this.getDescriptionId() + ".desc",
                (int) ((MULTIPLIER - 1) * 100)
        )).withStyle(descriptionStyle);
    }
}
