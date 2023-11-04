package io.redspace.ironsspellbooks.entity.spells.blood_needle;

import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

//https://github.com/TobyNguyen710/kyomod/blob/56d3a9dc6b45f7bc5ecdb0d6de9d201cea2603f5/Mod/build/tmp/expandedArchives/forge-1.19.2-43.1.7_mapped_official_1.19.2-sources.jar_b6309abf8a7e6a853ce50598293fb2e7/net/minecraft/world/entity/projectile/ShulkerBullet.java
//https://github.com/maximumpower55/Aura/blob/1.18/src/main/java/me/maximumpower55/aura/entity/SpellProjectileEntity.java
//https://github.com/CammiePone/Arcanus/blob/1.18-dev/src/main/java/dev/cammiescorner/arcanus/common/entities/MagicMissileEntity.java#L51
//https://github.com/maximumpower55/Aura

public class BloodNeedle extends AbstractMagicProjectile {
    private static final DataParameter<Float> DATA_Z_ROT = EntityDataManager.defineId(BloodNeedle.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> DATA_SCALE = EntityDataManager.defineId(BloodNeedle.class, DataSerializers.FLOAT);

    public BloodNeedle(EntityType<? extends BloodNeedle> entityType, World level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public BloodNeedle(World levelIn, LivingEntity shooter) {
        super(EntityRegistry.BLOOD_NEEDLE.get(), levelIn);
        setOwner(shooter);
    }

    public void setZRot(float zRot) {
        if (!level.isClientSide)
            entityData.set(DATA_Z_ROT, zRot);
    }

    public void setScale(float scale) {
        if (!level.isClientSide)
            entityData.set(DATA_SCALE, scale);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_Z_ROT, 0f);
        entityData.define(DATA_SCALE, 1f);
        super.defineSynchedData();
    }

    public float getZRot() {
        return entityData.get(DATA_Z_ROT);
    }

    public float getScale() {
        return entityData.get(DATA_SCALE);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("zRot", getZRot());
        if (getScale() != 1)
            pCompound.putFloat("Scale", getScale());
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        setZRot(pCompound.getFloat("zRot"));
        if (pCompound.contains("Scale"))
            setScale(pCompound.getFloat("Scale"));
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        DamageSources.applyDamage(entityHitResult.getEntity(), getDamage(), SpellRegistry.BLOOD_NEEDLES_SPELL.get().getDamageSource(this, getOwner()));
        entityHitResult.getEntity().invulnerableTime = 0;
    }

    @Override
    protected void onHit(RayTraceResult hitresult) {
        super.onHit(hitresult);
        this.remove();
    }

    private static int soundTimestamp;

    @Override
    protected void doImpactSound(SoundEvent sound) {
        if (soundTimestamp != this.tickCount) {
            super.doImpactSound(sound);
            soundTimestamp = this.tickCount;
        }
    }

    @Override
    public void trailParticles() {

        for (int i = 0; i < 2; i++) {
            double speed = .05;
            double dx = Utils.random.nextDouble() * 2 * speed - speed;
            double dy = Utils.random.nextDouble() * 2 * speed - speed;
            double dz = Utils.random.nextDouble() * 2 * speed - speed;
            level.addParticle(ParticleHelper.BLOOD, this.getX() + dx, this.getY() + dy, this.getZ() + dz, dx, dy, dz);

        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(level, ParticleHelper.BLOOD, x, y, z, 15, .1, .1, .1, .18, true);
    }

    @Override
    public float getSpeed() {
        return 2.5f;
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.of(SoundRegistry.BLOOD_NEEDLE_IMPACT.get());
    }

    @Override
    public boolean respectsGravity() {
        return true;
    }
}
