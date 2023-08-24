package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.item.armor.UpgradeType;
import net.minecraft.util.text.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;


public class UpgradeOrbItem extends Item {
    private final UpgradeType upgrade;
    private final static ITextComponent TOOLTIP_HEADER = new TranslationTextComponent("tooltip.irons_spellbooks.upgrade_tooltip").withStyle(TextFormatting.GRAY);
    private final ITextComponent TOOLTIP_TEXT;

    public UpgradeOrbItem(UpgradeType upgrade, Properties pProperties) {
        super(pProperties);
        this.upgrade = upgrade;
        TOOLTIP_TEXT = new StringTextComponent(" ").append(new TranslationTextComponent("attribute.modifier.plus." + upgrade.operation.toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(upgrade.amountPerUpgrade * (upgrade.operation == AttributeModifier.Operation.ADDITION ? 1 : 100)), new TranslationTextComponent(upgrade.attribute.getDescriptionId())).withStyle(TextFormatting.BLUE));
    }

    public UpgradeType getUpgradeType() {
        return this.upgrade;
    }

    @Override
    public ITextComponent getName(ItemStack pStack) {
        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> pTooltipComponents, ITooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(StringTextComponent.EMPTY);
        pTooltipComponents.add(TOOLTIP_HEADER);
        pTooltipComponents.add(TOOLTIP_TEXT);
    }
}
