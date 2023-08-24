package io.redspace.ironsspellbooks.item.curios;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
            var title = ITextComponent.translatable("curios.modifiers." + this.slotIdentifier).withStyle(TextFormatting.GOLD);
            var description = ITextComponent.literal(" ").append(ITextComponent.translatable(this.getDescriptionId() + ".desc")).withStyle(descriptionStyle);
            if(showHeader){
                tooltips.add(ITextComponent.empty());
                tooltips.add(title);
            }
            tooltips.add(description);
        }

        return super.getSlotsTooltip(tooltips, stack);
    }

}
