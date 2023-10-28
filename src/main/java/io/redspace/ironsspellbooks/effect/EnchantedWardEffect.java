package io.redspace.ironsspellbooks.effect;

import net.minecraft.util.DamageSource;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;

public class EnchantedWardEffect extends Effect {
    public EnchantedWardEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 30 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.hurt(DamageSource.MAGIC.bypassEnchantments(), 5);
    }
}
