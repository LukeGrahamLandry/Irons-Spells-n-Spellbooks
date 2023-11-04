package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;

public abstract class AbstractMagicProjectile extends ProjectileEntity implements AntiMagicSusceptible {
    protected static final int EXPIRE_TIME = 15 * 20;

    protected int age;
    protected float damage;
    protected float explosionRadius;

    /**
     * Client Side, called every tick
     */
    public abstract void trailParticles();
    /**
     * Server Side, called alongside onHit()
     */
    public abstract void impactParticles(double x, double y, double z);
    public abstract float getSpeed();
    public abstract Optional<SoundEvent> getImpactSound();

    public AbstractMagicProjectile(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    public void shoot(Vector3d rotation) {
        setDeltaMovement(rotation.scale(getSpeed()));
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public float getExplosionRadius() {
        return explosionRadius;
    }

    public void setExplosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    public boolean respectsGravity() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (age > EXPIRE_TIME) {
            this.remove();
            return;
        }
        if (level.isClientSide) {
            trailParticles();


        }
        RayTraceResult hitresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
        if (hitresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            onHit(hitresult);
        }
        setPos(position().add(getDeltaMovement()));
        ProjectileHelper.rotateTowardsMovement(this, 1);
        if (!this.isNoGravity() && respectsGravity()) {
            Vector3d vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
        }

        age++;
    }

    @Override
    protected void onHit(RayTraceResult hitresult) {
        super.onHit(hitresult);
        double x = xOld;
        double y = yOld;
        double z = zOld;

        if (!level.isClientSide) {
            impactParticles(x, y, z);
            getImpactSound().ifPresent(this::doImpactSound);
        }
    }

    protected void doImpactSound(SoundEvent sound) {
        level.playSound(null, getX(), getY(), getZ(), sound, SoundCategory.NEUTRAL, 2, .9f + Utils.random.nextFloat() * .2f);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.impactParticles(getX(), getY(), getZ());
        this.remove();
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putFloat("ExplosionRadius", explosionRadius);


    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.damage = pCompound.getFloat("Damage");
        this.explosionRadius = pCompound.getFloat("ExplosionRadius");

    }

    @Override
    protected void onHitEntity(EntityRayTraceResult pResult) {
        super.onHitEntity(pResult);
        if (!shouldPierceShields() && (pResult.getEntity() instanceof ShieldPart || pResult.getEntity() instanceof AbstractShieldEntity))
            this.onHitBlock(new BlockRayTraceResult(pResult.getEntity().position(), Direction.fromYRot(this.yRot), pResult.getEntity().blockPosition(), false));
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    protected boolean shouldPierceShields() {
        return false;
    }
}
