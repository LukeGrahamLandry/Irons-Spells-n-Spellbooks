package io.redspace.ironsspellbooks.entity.spells.black_hole;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;

public class BlackHole extends ProjectileEntity implements AntiMagicSusceptible {
    private static final DataParameter<Float> DATA_RADIUS = EntityDataManager.defineId(BlackHole.class, DataSerializers.FLOAT);

    public BlackHole(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    public BlackHole(World pLevel, LivingEntity owner) {
        this(EntityRegistry.BLACK_HOLE.get(), pLevel);
        setOwner(owner);
    }

    List<Entity> trackingEntities = new ArrayList<>();

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    private float damage;

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public EntitySize getDimensions(Pose pPose) {
        return EntitySize.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 5F);
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
            this.getEntityData().set(DATA_RADIUS, Math.min(pRadius, 48));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putInt("Age", this.tickCount);
        pCompound.putFloat("Damage", this.getDamage());

        super.addAdditionalSaveData(pCompound);
    }
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        this.tickCount = pCompound.getInt("Age");
        this.damage = pCompound.getFloat("Damage");
        if (damage == 0)
            damage = 1;
        if (pCompound.getInt("Radius") > 0)
            this.setRadius(pCompound.getFloat("Radius"));

        super.readAdditionalSaveData(pCompound);

    }
    @Override
    public void tick() {
        super.tick();
        int update = Math.max((int) (getRadius() / 2), 2);
        //prevent lag from giagantic black holes
        if (tickCount % update == 0)
            updateTrackingEntities();
        for (Entity entity : trackingEntities) {
            if (entity != getOwner() && !DamageSources.isFriendlyFireBetween(getOwner(), entity)) {
                Vector3d center = this.position().add(0, this.getBoundingBox().getYsize() / 2, 0);
                float distance = (float) center.distanceTo(entity.position());
                float radius = (float) (this.getBoundingBox().getXsize());
                float f = 1 - distance / radius;
                float scale = f * f * f * f * .25f;
                Vector3d diff = center.subtract(entity.position()).scale(scale);
                entity.push(diff.x, diff.y, diff.z);
                if (this.tickCount % 10 == 0) {
                    if (distance < 3f && canHitEntity(entity))
                        DamageSources.applyDamage(entity, damage, SpellRegistry.BLACK_HOLE_SPELL.get().getDamageSource(this, getOwner()), SpellRegistry.BLACK_HOLE_SPELL.get().getSchoolType());
                }
                entity.resetFallDistance();
            }

        }
        if (!level.isClientSide) {
            if (tickCount > 20 * 16 * 2) {
                this.remove();
                this.playSound(SoundRegistry.BLACK_HOLE_CAST.get(), getRadius() / 2f, 1);
                MagicManager.spawnParticles(level, ParticleHelper.UNSTABLE_ENDER, getX(), getY() + getRadius(), getZ(), 200, 1, 1, 1, 1, true);
            } else if ((tickCount - 1) % loopSoundDurationInTicks == 0) {
                //TODO: stop sound
                this.playSound(SoundRegistry.BLACK_HOLE_LOOP.get(), getRadius() / 3f, 1);
            }

        }
    }

    private void updateTrackingEntities() {
        trackingEntities = level.getEntities(this, this.getBoundingBox().inflate(1));
    }

    private static final int loopSoundDurationInTicks = 320;

    @Override
    public boolean displayFireAnimation() {
        return false;
    }
}
