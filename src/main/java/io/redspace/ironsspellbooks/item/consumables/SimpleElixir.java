package io.redspace.ironsspellbooks.item.consumables;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.redspace.ironsspellbooks.effect.CustomDescriptionMobEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.Item.Properties;

public class SimpleElixir extends DrinkableItem {
    private final Supplier<EffectInstance> potionEffect;

    boolean foilOverride;
    public SimpleElixir(Properties pProperties, Supplier<EffectInstance> potionEffect) {
        super(pProperties, SimpleElixir::applyEffect, Items.GLASS_BOTTLE, true);
        this.potionEffect = potionEffect;
    }

    public SimpleElixir(Properties pProperties, Supplier<EffectInstance> potionEffect, boolean foil){
        this(pProperties,potionEffect);
        this.foilOverride = foil;
    }

    public EffectInstance getMobEffect(){
        return this.potionEffect.get();
    }

    private static void applyEffect(ItemStack itemStack, LivingEntity livingEntity) {
        if (itemStack.getItem() instanceof SimpleElixir && ((SimpleElixir) itemStack.getItem()).potionEffect.get() != null) {
            SimpleElixir elixir = (SimpleElixir) itemStack.getItem();
            livingEntity.addEffect(elixir.potionEffect.get());
        }
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return super.isFoil(pStack) || foilOverride;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> pTooltipComponents, ITooltipFlag pIsAdvanced) {
        addPotionTooltip(this.potionEffect.get(), pTooltipComponents, 1f);
        if (this.potionEffect.get().getEffect() instanceof CustomDescriptionMobEffect) {
            CustomDescriptionMobEffect customDescriptionMobEffect = (CustomDescriptionMobEffect) this.potionEffect.get().getEffect();
            CustomDescriptionMobEffect.handleCustomPotionTooltip(pStack, pTooltipComponents, false, this.potionEffect.get(), customDescriptionMobEffect);
        }
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Items.POTION.getMaxStackSize();
    }

    public static void addPotionTooltip(EffectInstance mobeffectinstance, List<ITextComponent> pTooltips, float pDurationFactor) {
        /**
         * adapted from PotionUtils.addPotionTooltip
         */
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        IFormattableTextComponent mutablecomponent = ITextComponent.translatable(mobeffectinstance.getDescriptionId());
        Effect mobeffect = mobeffectinstance.getEffect();
        Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
        if (!map.isEmpty()) {
            for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                AttributeModifier attributemodifier = entry.getValue();
                AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(mobeffectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                list1.add(new Pair<>(entry.getKey(), attributemodifier1));
            }
        }

        if (mobeffectinstance.getAmplifier() > 0) {
            mutablecomponent = ITextComponent.translatable("potion.withAmplifier", mutablecomponent, ITextComponent.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
        }

        if (mobeffectinstance.getDuration() > 20) {
            mutablecomponent = ITextComponent.translatable("potion.withDuration", mutablecomponent, EffectUtils.formatDuration(mobeffectinstance, pDurationFactor));
        }

        pTooltips.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));

        if (!list1.isEmpty()) {
            pTooltips.add(DialogTexts.EMPTY);
            pTooltips.add(ITextComponent.translatable("potion.whenDrank").withStyle(TextFormatting.DARK_PURPLE));

            for (Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    pTooltips.add(ITextComponent.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), ITextComponent.translatable(pair.getFirst().getDescriptionId())).withStyle(TextFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    pTooltips.add(ITextComponent.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), ITextComponent.translatable(pair.getFirst().getDescriptionId())).withStyle(TextFormatting.RED));
                }
            }
        }

    }

}
