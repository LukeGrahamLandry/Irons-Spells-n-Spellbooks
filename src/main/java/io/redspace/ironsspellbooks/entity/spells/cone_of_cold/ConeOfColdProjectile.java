package io.redspace.ironsspellbooks.entity.spells.cone_of_cold;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class ConeOfColdProjectile extends AbstractConeProjectile {
    public ConeOfColdProjectile(EntityType<? extends AbstractConeProjectile> entityType, World level){
        super(entityType,level);
    }

    public ConeOfColdProjectile(World level, LivingEntity entity) {
        super(EntityRegistry.CONE_OF_COLD_PROJECTILE.get(), level, entity);
    }


    @Override
    public void spawnParticles() {
        Entity owner = getOwner();
        if (!level.isClientSide || owner == null) {
            return;
        }
        Vector3d rotation = owner.getLookAngle().normalize();
        Vector3d pos = owner.position().add(rotation.scale(1.6));

        double x = pos.x;
        double y = pos.y + owner.getEyeHeight() * .9f;
        double z = pos.z;

        double speed = random.nextDouble() * .4 + .45;
        for (int i = 0; i < 25; i++) {
            double offset = .25;
            double ox = Math.random() * 2 * offset - offset;
            double oy = Math.random() * 2 * offset - offset;
            double oz = Math.random() * 2 * offset - offset;

            double angularness = .8;
            Vector3d randomVec = new Vector3d(Math.random() * 2 * angularness - angularness, Math.random()  * 2 * angularness - angularness, Math.random()  * 2 * angularness - angularness).normalize();
            Vector3d result = (rotation.scale(3).add(randomVec)).normalize().scale(speed);
            level.addParticle(Math.random() > .05 ? ParticleTypes.SNOWFLAKE : ParticleHelper.SNOWFLAKE, x + ox, y + oy, z + oz, result.x, result.y, result.z);
        }


    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        //irons_spellbooks.LOGGER.debug("ConeOfColdProjectile.onHitEntity: {}", entityHitResult.getEntity().getName().getString());
        Entity entity = entityHitResult.getEntity();
        DamageSources.applyDamage(entity, damage, SpellRegistry.CONE_OF_COLD_SPELL.get().getDamageSource(this, getOwner()));
    }

}
