package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class AbyssalShroudEffect extends Effect {

    public AbyssalShroudEffect(EffectType mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(pLivingEntity).getSyncedData().removeEffects(SyncedSpellData.ABYSSAL_SHROUD);
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(pLivingEntity).getSyncedData().addEffects(SyncedSpellData.ABYSSAL_SHROUD);
    }


    public static boolean doEffect(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity.level.isClientSide || EvasionEffect.excludeDamageSources.contains(damageSource) || damageSource.isFall() || damageSource.isBypassMagic() || damageSource.isBypassInvul()) {
            return false;
        }
        Random random = livingEntity.getRandom();
        World level = livingEntity.level;


        Vector3d sideStep = new Vector3d(random.nextBoolean() ? 1 : -1, 0, -.25);
        sideStep.yRot(livingEntity.yRot);

        particleCloud(livingEntity);

        Vector3d ground = livingEntity.position().add(sideStep);
        ground = level.clip(new RayTraceContext(ground.add(0, 3.5, 0), ground.add(0, -3.5, 0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null)).getLocation();

        if (livingEntity.isPassenger()) {
            livingEntity.stopRiding();
        }
        if (!level.getBlockState(new BlockPos(ground).below()).isAir()) {
            livingEntity.teleportTo(ground.x, ground.y, ground.z);
            particleCloud(livingEntity);
        }
        if (damageSource.getEntity() != null) {
            livingEntity.lookAt(EntityAnchorArgument.Type.EYES, damageSource.getEntity().getEyePosition(0).subtract(0, .15, 0));
        }
        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundRegistry.ABYSSAL_TELEPORT.get(), SoundCategory.AMBIENT, 1.0F, .9F + random.nextFloat() * .2f);
        return true;
    }

    private static void particleCloud(LivingEntity entity) {
        Vector3d pos = entity.position().add(0, entity.getBbHeight() / 2, 0);
        MagicManager.spawnParticles(entity.level, ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 70, entity.getBbWidth() / 4, entity.getBbHeight() / 5, entity.getBbWidth() / 4, .035, false);
    }

    public static void ambientParticles(ClientWorld level, LivingEntity entity) {
        Vector3d backwards = entity.getForward().scale(.003).reverse().add(0, 0.02, 0);
        Random random = entity.getRandom();
        for (int i = 0; i < 2; i++) {
            Vector3d motion = new Vector3d(
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1
            );
            motion = motion.scale(.04f).add(backwards);
            level.addParticle(ParticleTypes.SMOKE, entity.getRandomX(.4f), entity.getRandomY(), entity.getRandomZ(.4f), motion.x, motion.y, motion.z);
        }
    }
}
