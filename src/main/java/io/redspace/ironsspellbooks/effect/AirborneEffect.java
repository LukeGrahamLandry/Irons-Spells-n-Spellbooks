package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;

public class AirborneEffect extends Effect {
    public AirborneEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    public static final float damage_per_amp = 0.5f;
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int pAmplifier) {
        if (!livingEntity.level.isClientSide) {
            if (livingEntity.horizontalCollision) {
                //IronsSpellbooks.LOGGER.debug("{}", livingEntity.getDeltaMovement());
                //double d11 = livingEntity.getDeltaMovement().horizontalDistance();
                //float f1 = (float) (d11 * 10.0D - 3.0D);
                //if (f1 > 0.0F) {
                livingEntity.playSound(SoundEvents.HOSTILE_BIG_FALL, 2.0F, 1.5F);
                livingEntity.hurt(DamageSource.FLY_INTO_WALL, getDamageFromLevel(pAmplifier + 1));
                livingEntity.removeEffect(MobEffectRegistry.AIRBORNE.get());
                //}
            }

        }

    }

    public static float getDamageFromLevel(int level) {
        return 4 + level * damage_per_amp;
    }
}
