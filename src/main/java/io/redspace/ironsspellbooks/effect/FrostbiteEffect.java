package io.redspace.ironsspellbooks.effect;

import net.minecraft.util.DamageSource;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;

public class FrostbiteEffect extends Effect {

    public FrostbiteEffect(EffectType mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(livingEntity, pAttributeMap, pAmplifier);
        //livingEntity.getMobType()
        //PlayerMagicData.getPlayerMagicData(livingEntity).getSyncedData().setHasAbyssalShroud(false);
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        //PlayerMagicData.getPlayerMagicData(pLivingEntity).getSyncedData().setHasAbyssalShroud(true);
    }


    public static boolean doEffect(LivingEntity livingEntity, DamageSource damageSource) {

        return true;
    }
}
