package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.api.item.curios.RingData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.render.AffinityRingRenderer;
import io.redspace.ironsspellbooks.render.SpecialItemRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.item.Item.Properties;

public class AffinityRing extends SimpleDescriptiveCurio {

    public AffinityRing(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> tooltip, ITooltipFlag pIsAdvanced) {
        AbstractSpell spell = RingData.getRingData(pStack).getSpell();
        if (!spell.equals(SpellRegistry.none())) {
            tooltip.add(ITextComponent.empty());
            tooltip.add(new TranslationTextComponent("curios.modifiers.ring").withStyle(TextFormatting.GOLD));
            tooltip.add(new TranslationTextComponent("tooltip.irons_spellbooks.enhance_spell_level", spell.getDisplayName().withStyle(spell.getSchoolType().getDisplayName().getStyle())).withStyle(TextFormatting.YELLOW));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.irons_spellbooks.empty_affinity_ring").withStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
        }
    }

    @Override
    public ITextComponent getName(ItemStack pStack) {
        return new TranslationTextComponent(this.getDescriptionId(pStack), RingData.getRingData(pStack).getNameForItem());
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public ItemStackTileEntityRenderer getCustomRenderer() {
                return new AffinityRingRenderer(Minecraft.getInstance().getItemRenderer(),
                        Minecraft.getInstance().getEntityModels());
            }
        });
    }
}
