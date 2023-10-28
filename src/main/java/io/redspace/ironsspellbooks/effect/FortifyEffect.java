package io.redspace.ironsspellbooks.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;

public class FortifyEffect extends Effect {
    public FortifyEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        pLivingEntity.setAbsorptionAmount(pLivingEntity.getAbsorptionAmount() - (float)(pAmplifier + 1));
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        pLivingEntity.setAbsorptionAmount(pLivingEntity.getAbsorptionAmount() + (float)(pAmplifier + 1));
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }
}
