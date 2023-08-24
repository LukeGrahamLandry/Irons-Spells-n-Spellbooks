package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Set;

public class EvasionEffect extends Effect {

    public static Set<DamageSource> excludeDamageSources = Set.of(
            DamageSource.ON_FIRE,
            DamageSource.WITHER,
            DamageSource.FREEZE,
            DamageSources.CAULDRON,
            DamageSources.BLEED_DAMAGE,
            DamageSource.STARVE,
            DamageSource.DROWN,
            DamageSource.STALAGMITE,
            DamageSource.OUT_OF_WORLD);

    public EvasionEffect(EffectType mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        PlayerMagicData.getPlayerMagicData(pLivingEntity).getSyncedData().removeEffects(SyncedSpellData.EVASION);
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        PlayerMagicData.getPlayerMagicData(pLivingEntity).getSyncedData().addEffects(SyncedSpellData.EVASION);
        PlayerMagicData.getPlayerMagicData(pLivingEntity).getSyncedData().setEvasionHitsRemaining(pAmplifier);

    }

    public static boolean doEffect(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity.level.isClientSide || excludeDamageSources.contains(damageSource) || damageSource.isFall() || damageSource.isBypassMagic() || damageSource.isBypassInvul()) {
            return false;
        }

        var data = PlayerMagicData.getPlayerMagicData(livingEntity).getSyncedData();
        data.subtractEvasionHit();
        if (data.getEvasionHitsRemaining() == 0)
            livingEntity.removeEffect(MobEffectRegistry.EVASION.get());

        double d0 = livingEntity.getX();
        double d1 = livingEntity.getY();
        double d2 = livingEntity.getZ();
        double maxRadius = 18d;
        var level = livingEntity.level;
        var random = livingEntity.getRandom();

        for (int i = 0; i < 16; ++i) {
            var minRadius = maxRadius / 2;
            Vector3d vec = new Vector3d((double) random.nextInt((int) minRadius, (int) maxRadius), 0, 0);
            int degrees = random.nextInt(360);
            vec = vec.yRot(degrees);

            double x = d0 + vec.x;
            double y = MathHelper.clamp(livingEntity.getY() + (double) (livingEntity.getRandom().nextInt((int) maxRadius) - maxRadius / 2), (double) level.getMinBuildHeight(), (double) (level.getMinBuildHeight() + ((ServerWorld) level).getLogicalHeight() - 1));
            double z = d2 + vec.z;

            if (livingEntity.isPassenger()) {
                livingEntity.stopRiding();
            }

            if (livingEntity.randomTeleport(x, y, z, true)) {
                if (damageSource.getEntity() != null) {
                    livingEntity.lookAt(EntityAnchorArgument.Type.EYES, damageSource.getEntity().getEyePosition());
                }
                level.playSound((PlayerEntity) null, d0, d1, d2, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                livingEntity.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 1.0F);
                break;
            }

            if (maxRadius > 2) {
                maxRadius--;
            }
        }
        //Vanilla teleport only spawns particles from the original location, not at the destination
        particleCloud(livingEntity);
        return true;
    }

    private static void particleCloud(LivingEntity entity) {
        Vector3d pos = entity.position().add(0, entity.getBbHeight() / 2, 0);
        MagicManager.spawnParticles(entity.level, ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 70, entity.getBbWidth() / 4, entity.getBbHeight() / 5, entity.getBbWidth() / 4, .035, false);
    }

}
