package io.redspace.ironsspellbooks.item.curios;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.redspace.ironsspellbooks.api.registry.AttributeRegistry.COOLDOWN_REDUCTION;

import net.minecraft.item.Item.Properties;

public class SimpleDescriptiveCurio extends CurioBaseItem {
    final @Nullable String slotIdentifier;
    Style descriptionStyle;
    boolean showHeader;

    public SimpleDescriptiveCurio(Properties properties, String slotIdentifier) {
        super(properties);
        this.slotIdentifier = slotIdentifier;
        this.showHeader = true;
        descriptionStyle = Style.EMPTY.withColor(TextFormatting.YELLOW);
    }

    public SimpleDescriptiveCurio(Properties properties) {
        this(properties, null);
    }

    @Override
    public List<ITextComponent> getSlotsTooltip(List<ITextComponent> tooltips, ItemStack stack) {
        if (slotIdentifier != null) {
            var title = new TranslationTextComponent("curios.modifiers." + this.slotIdentifier).withStyle(TextFormatting.GOLD);
            if (showHeader) {
                tooltips.add(ITextComponent.empty());
                tooltips.add(title);
            }
            tooltips.addAll(getDescriptionLines(stack));
        }

        return super.getSlotsTooltip(tooltips, stack);
    }

    public List<ITextComponent> getDescriptionLines(ItemStack stack) {
        return List.of(getDescription(stack));
    }

    public ITextComponent getDescription(ItemStack stack) {
        return new StringTextComponent(" ").append(new TranslationTextComponent(this.getDescriptionId() + ".desc")).withStyle(descriptionStyle);
    }
}
