package io.redspace.ironsspellbooks.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.vector.Vector3d;

public class GuidingBoltEffect extends Effect {
    public GuidingBoltEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int pAmplifier) {
        return duration % 2 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int pAmplifier) {
        livingEntity.level.getEntitiesOfClass(ProjectileEntity.class, livingEntity.getBoundingBox().inflate(Math.min(4 + pAmplifier, 10)), (projectile) -> projectile.getOwner() != livingEntity).forEach((projectile) -> {
            Vector3d magnetization = livingEntity.getEyePosition().subtract(projectile.position()).normalize().scale(.25f + .075f).scale(2);
            projectile.setDeltaMovement(projectile.getDeltaMovement().add(magnetization));
        });
    }
}
