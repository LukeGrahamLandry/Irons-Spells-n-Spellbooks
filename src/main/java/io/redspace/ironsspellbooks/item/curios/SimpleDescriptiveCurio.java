package io.redspace.ironsspellbooks.item.curios;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public void appendHoverText(ItemStack pStack, @javax.annotation.Nullable World pLevel, List<ITextComponent> tooltips, ITooltipFlag pFlag) {
        if (slotIdentifier != null) {
            var title = new TranslationTextComponent("curios.modifiers." + this.slotIdentifier).withStyle(TextFormatting.GOLD);
            var description = new StringTextComponent(" ").append(new TranslationTextComponent(this.getDescriptionId() + ".desc")).withStyle(descriptionStyle);
            if(showHeader){
                tooltips.add(StringTextComponent.EMPTY);
                tooltips.add(title);
            }
            tooltips.add(description);
        }
        super.appendHoverText(pStack, pLevel, tooltips, pFlag);
    }
}
