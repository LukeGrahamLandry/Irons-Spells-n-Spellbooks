package io.redspace.ironsspellbooks.entity.spells.acid_orb;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class AcidOrb extends AbstractMagicProjectile {
    public AcidOrb(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    public AcidOrb(World level, LivingEntity shooter) {
        this(EntityRegistry.ACID_ORB.get(), level);
        setOwner(shooter);
    }

    int rendLevel;
    int rendDuration;


    public int getRendLevel() {
        return rendLevel;
    }

    public void setRendLevel(int rendLevel) {
        this.rendLevel = rendLevel;
    }

    public int getRendDuration() {
        return rendDuration;
    }

    public void setRendDuration(int rendDuration) {
        this.rendDuration = rendDuration;
    }

    @Override
    public void trailParticles() {
        Vector3d vec3 = this.position().subtract(getDeltaMovement().scale(2));
        level.addParticle(ParticleHelper.ACID, vec3.x, vec3.y, vec3.z, 0, 0, 0);
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(level, ParticleHelper.ACID, x, y, z, 55, .08, .08, .08, 0.3, true);
        MagicManager.spawnParticles(level, ParticleHelper.ACID_BUBBLE, x, y, z, 25, .08, .08, .08, 0.3, false);
    }

    @Override
    public boolean respectsGravity() {
        return true;
    }

    @Override
    public float getSpeed() {
        return 1;
    }

    @Override
    protected void onHit(RayTraceResult hitresult) {
        super.onHit(hitresult);
        if (!this.level.isClientSide) {
            float explosionRadius = 3.5f;
            List<Entity> entities = level.getEntities(this, this.getBoundingBox().inflate(explosionRadius));
            for (Entity entity : entities) {
                double distance = entity.position().distanceTo(hitresult.getLocation());
                if (distance < explosionRadius && Utils.hasLineOfSight(level, hitresult.getLocation(), entity.getEyePosition(0), true)) {
                    if (entity instanceof LivingEntity && (LivingEntity) entity != getOwner()) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.addEffect(new EffectInstance(MobEffectRegistry.REND.get(), getRendDuration(), getRendLevel()));
                    }
                }
            }
            this.remove();
        }
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.of(SoundRegistry.ACID_ORB_IMPACT.get());
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("RendLevel", rendLevel);
        pCompound.putInt("RendDuration", rendDuration);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.rendLevel = pCompound.getInt("RendLevel");
        this.rendDuration = pCompound.getInt("RendDuration");
    }
}
