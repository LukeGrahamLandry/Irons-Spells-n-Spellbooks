package io.redspace.ironsspellbooks.entity.spells.magma_ball;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;

public class FireBomb extends AbstractMagicProjectile {
    public FireBomb(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    public FireBomb(World level, LivingEntity shooter) {
        this(EntityRegistry.FIRE_BOMB.get(), level);
        setOwner(shooter);
    }

    @Override
    public void trailParticles() {
        Vector3d vec3 = getDeltaMovement();
        double d0 = this.getX() - vec3.x;
        double d1 = this.getY() - vec3.y;
        double d2 = this.getZ() - vec3.z;
        for (int i = 0; i < 4; i++) {
            Vector3d random = Utils.getRandomVec3(.2);
            this.level.addParticle(ParticleTypes.SMOKE, d0 - random.x, d1 + 0.5D - random.y, d2 - random.z, random.x * .5f, random.y * .5f, random.z * .5f);
        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(level, ParticleTypes.LAVA, x, y, z, 30, 1.5, .1, 1.5, 1, false);
    }

    @Override
    public boolean respectsGravity() {
        return true;
    }

    @Override
    public float getSpeed() {
        return .65f;
    }

    @Override
    protected void onHit(RayTraceResult hitresult) {
        super.onHit(hitresult);
        createFireField(hitresult.getLocation());
        float explosionRadius = getExplosionRadius();
        var entities = level.getEntities(this, this.getBoundingBox().inflate(explosionRadius));
        for (Entity entity : entities) {
            double distance = entity.distanceToSqr(hitresult.getLocation());
            if (distance < explosionRadius * explosionRadius && canHitEntity(entity)) {
                if (Utils.hasLineOfSight(level, hitresult.getLocation(), entity.position().add(0, entity.getEyeHeight() * .5f, 0), true)) {
                    double p = (1 - Math.pow(Math.sqrt(distance) / (explosionRadius), 3));
                    float damage = (float) (this.damage * p);
                    DamageSources.applyDamage(entity, damage, SpellRegistry.MAGMA_BOMB_SPELL.get().getDamageSource(this, getOwner()), SpellRegistry.MAGMA_BOMB_SPELL.get().getSchoolType());
                }
            }
        }
        discard();
    }

    public void createFireField(Vector3d location) {
        if (!level.isClientSide) {
            FireField fire = new FireField(level);
            fire.setOwner(getOwner());
            fire.setDuration(200);
            fire.setDamage(damage / 5);
            fire.setRadius(getExplosionRadius());
            fire.setCircular();
            fire.moveTo(location);
            level.addFreshEntity(fire);
        }
    }

    @Override
    protected void doImpactSound(SoundEvent sound) {
        level.playSound(null, getX(), getY(), getZ(), sound, SoundCategory.NEUTRAL, 2, 1.2f + Utils.random.nextFloat() * .2f);
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.of(SoundEvents.GENERIC_EXPLODE);
    }

}
