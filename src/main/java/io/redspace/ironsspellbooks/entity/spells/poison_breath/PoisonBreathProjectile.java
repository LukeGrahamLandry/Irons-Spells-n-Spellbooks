package io.redspace.ironsspellbooks.entity.spells.poison_breath;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class PoisonBreathProjectile extends AbstractConeProjectile {
    public PoisonBreathProjectile(EntityType<? extends AbstractConeProjectile> entityType, World level) {
        super(entityType, level);
    }

    public PoisonBreathProjectile(World level, LivingEntity entity) {
        super(EntityRegistry.POISON_BREATH_PROJECTILE.get(), level, entity);
    }


    @Override
    public void spawnParticles() {
        var owner = getOwner();
        if (!level.isClientSide || owner == null) {
            return;
        }
        Vector3d rotation = owner.getLookAngle().normalize();
        var pos = owner.position().add(rotation.scale(1.6));

        double x = pos.x;
        double y = pos.y + owner.getEyeHeight() * .9f;
        double z = pos.z;

        double speed = random.nextDouble() * .4 + .45;
        for (int i = 0; i < 20; i++) {
            double offset = .25;
            double ox = Math.random() * 2 * offset - offset;
            double oy = Math.random() * 2 * offset - offset;
            double oz = Math.random() * 2 * offset - offset;

            double angularness = .8;
            Vector3d randomVec = new Vector3d(Math.random() * 2 * angularness - angularness, Math.random() * 2 * angularness - angularness, Math.random() * 2 * angularness - angularness).normalize();
            Vector3d result = (rotation.scale(3).add(randomVec)).normalize().scale(speed);
            level.addParticle(random.nextFloat() < .25f ? ParticleHelper.ACID_BUBBLE : ParticleHelper.ACID, x + ox, y + oy, z + oz, result.x, result.y, result.z);
        }


    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        //irons_spellbooks.LOGGER.debug("ConeOfColdProjectile.onHitEntity: {}", entityHitResult.getEntity().getName().getString());
        var entity = entityHitResult.getEntity();
        if (DamageSources.applyDamage(entity, damage, SpellRegistry.POISON_BREATH_SPELL.get().getDamageSource(this, getOwner()), SpellRegistry.POISON_BREATH_SPELL.get().getSchoolType()) && entity instanceof LivingEntity livingEntity)
            livingEntity.addEffect(new EffectInstance(Effects.POISON, 100, 0));
    }

}
