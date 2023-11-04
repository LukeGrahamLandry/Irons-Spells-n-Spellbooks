package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.particles.IParticleData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.world.phys.Vec2;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fluids.FluidType;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;

public abstract class AoeEntity extends ProjectileEntity implements NoKnockbackProjectile {
    private static final DataParameter<Float> DATA_RADIUS = EntityDataManager.defineId(AoeEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> DATA_CIRCULAR = EntityDataManager.defineId(AoeEntity.class, DataSerializers.BOOLEAN);

    protected float damage;
    protected int duration = 600;
    protected int reapplicationDelay = 10;
    protected int durationOnUse;
    protected float radiusOnUse;
    protected float radiusPerTick;
    protected int effectDuration;

    public AoeEntity(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.blocksBuilding = false;
    }

    protected float particleYOffset(){
        return 0f;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public void setEffectDuration(int effectDuration) {
        this.effectDuration = effectDuration;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > duration) {
            //IronsSpellbooks.LOGGER.debug("AOEProjectile.discarding ({}/{})", tickCount, duration);
            this.remove();
            return;
        }
        if (!level.isClientSide) {
            if (tickCount % reapplicationDelay == 1) {
                checkHits();
            }
            if (tickCount % 5 == 0)
                this.setRadius(getRadius() + radiusPerTick);
        } else {
            ambientParticles();
        }
        moveTo(position().add(getDeltaMovement()));
    }

    protected void checkHits() {
        if (level.isClientSide)
            return;
        List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.getInflation().x, this.getInflation().y, this.getInflation().z));
        boolean hit = false;
        for (LivingEntity target : targets) {
            if (canHitEntity(target) && (!isCircular() || target.distanceTo(this) < getRadius())) {
                if (canHitTargetForGroundContext(target)) {
                    applyEffect(target);
                    hit = true;
                }
            }
        }
        if (hit) {
            this.setRadius(getRadius() + radiusOnUse);
            this.duration += durationOnUse;
            onPostHit();
        }
    }

    protected Vector3d getInflation() {
        return Vector3d.ZERO;
    }

    /**
     * Little bit of logic to fix the area effect cloud issue of not hitting mobs unless they're on the exact same Y. can be overridden for Aoe's with weird-shaped hitboxes.
     */
    protected boolean canHitTargetForGroundContext(LivingEntity target) {
        return target.isOnGround() || target.getY() - getY() < .5;
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return (getOwner() != null && pTarget != getOwner() && !getOwner().isAlliedTo(pTarget)) && super.canHitEntity(pTarget);
    }

    /**
     * Server Side, called if any entities were hit
     */
    public void onPostHit() {

    }

    public abstract void applyEffect(LivingEntity target);

    public void ambientParticles() {
        if (!level.isClientSide)
            return;

        float f = getParticleCount();
        f = MathHelper.clamp(f * getRadius(), f / 4, f * 10);
        for (int i = 0; i < f; i++) {
            if (f - i < 1 && random.nextFloat() > f - i)
                return;
            float r = getRadius();
            Vector3d pos;
            if (isCircular()) {
                float distance = r * (1 - this.random.nextFloat() * this.random.nextFloat());
                float theta = this.random.nextFloat() * 6.282f; // two pi :nerd:
                pos = new Vector3d(
                        distance * MathHelper.cos(theta),
                        .2f,
                        distance * MathHelper.sin(theta)
                );
            } else {
                pos = new Vector3d(
                        Utils.getRandomScaled(r * .85f),
                        .2f,
                        Utils.getRandomScaled(r * .85f)
                );
            }
            Vector3d motion = new Vector3d(
                    Utils.getRandomScaled(.03f),
                    this.random.nextDouble() * .01f,
                    Utils.getRandomScaled(.03f)
            ).scale(this.getParticleSpeedModifier());

            level.addParticle(getParticle(), getX() + pos.x, getY() + pos.y + particleYOffset(), getZ() + pos.z, motion.x, motion.y, motion.z);
        }
    }

    protected float getParticleSpeedModifier() {
        return 1f;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 2F);
        this.getEntityData().define(DATA_CIRCULAR, false);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> pKey) {
        if (DATA_RADIUS.equals(pKey)) {
            this.refreshDimensions();
            if (getRadius() < .1f)
                this.remove();
        }

        super.onSyncedDataUpdated(pKey);
    }

    public void setRadius(float pRadius) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, MathHelper.clamp(pRadius, 0.0F, 32.0F));
        }
    }

    public void setDuration(int duration){
        if (!this.level.isClientSide) {
            this.duration = duration;
        }
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public Boolean isCircular() {
        return this.getEntityData().get(DATA_CIRCULAR);
    }

    public void setCircular() {
        this.getEntityData().set(DATA_CIRCULAR, true);
    }

    public abstract float getParticleCount();

    public abstract IParticleData getParticle();

    @Override
    public EntitySize getDimensions(Pose pPose) {
        return EntitySize.scalable(this.getRadius() * 2.0F, 0.8F);
    }

    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        pCompound.putInt("Age", this.tickCount);
        pCompound.putInt("Duration", this.duration);
        pCompound.putInt("ReapplicationDelay", this.reapplicationDelay);
        pCompound.putInt("DurationOnUse", this.durationOnUse);
        pCompound.putFloat("RadiusOnUse", this.radiusOnUse);
        pCompound.putFloat("RadiusPerTick", this.radiusPerTick);
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putBoolean("Circular", this.isCircular());
        pCompound.putInt("EffectDuration", this.effectDuration);
        super.addAdditionalSaveData(pCompound);

    }

    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        this.tickCount = pCompound.getInt("Age");
        if (pCompound.getInt("Duration") > 0)
            this.duration = pCompound.getInt("Duration");
        if (pCompound.getInt("ReapplicationDelay") > 0)
            this.reapplicationDelay = pCompound.getInt("ReapplicationDelay");
        if (pCompound.getInt("Radius") > 0)
            this.setRadius(pCompound.getFloat("Radius"));
        if (pCompound.getInt("DurationOnUse") > 0)
            this.durationOnUse = pCompound.getInt("DurationOnUse");
        if (pCompound.getInt("RadiusOnUse") > 0)
            this.radiusOnUse = pCompound.getFloat("RadiusOnUse");
        if (pCompound.getInt("RadiusPerTick") > 0)
            this.radiusPerTick = pCompound.getFloat("RadiusPerTick");
        if (pCompound.getInt("EffectDuration") > 0)
            this.effectDuration = pCompound.getInt("EffectDuration");
        this.setDamage(pCompound.getFloat("Damage"));
        if (pCompound.getBoolean("Circular"))
            setCircular();

        super.readAdditionalSaveData(pCompound);

    }

}
