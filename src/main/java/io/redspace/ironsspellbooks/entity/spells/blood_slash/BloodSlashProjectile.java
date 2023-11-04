package io.redspace.ironsspellbooks.entity.spells.blood_slash;

import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.entity.*;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;

public class BloodSlashProjectile extends ProjectileEntity implements AntiMagicSusceptible {
    private static final DataParameter<Float> DATA_RADIUS = EntityDataManager.defineId(BloodSlashProjectile.class, DataSerializers.FLOAT);
    private static final double SPEED = 1d;
    private static final int EXPIRE_TIME = 4 * 20;
    public final int animationSeed;
    private final float maxRadius;
    private EntitySize dimensions;
    public AxisAlignedBB oldBB;
    private int age;
    private float damage;
    public int animationTime;
    private List<Entity> victims;

    public BloodSlashProjectile(EntityType<? extends BloodSlashProjectile> entityType, World level) {
        super(entityType, level);
        animationSeed = Utils.random.nextInt(9999);

        float initialRadius = 2;
        maxRadius = 4;
        dimensions = EntitySize.scalable(initialRadius, 0.5f);

        oldBB = getBoundingBox();
        victims = new ArrayList<>();
        this.setNoGravity(true);
    }

    public BloodSlashProjectile(EntityType<? extends BloodSlashProjectile> entityType, World levelIn, LivingEntity shooter) {
        this(entityType, levelIn);
        setOwner(shooter);
        setYRot(shooter.yRot);
        setXRot(shooter.xRot);
    }

    public BloodSlashProjectile(World levelIn, LivingEntity shooter) {
        this(EntityRegistry.BLOOD_SLASH_PROJECTILE.get(), levelIn, shooter);
    }

    public void shoot(Vector3d rotation) {
        setDeltaMovement(rotation.scale(SPEED));
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    //TODO: override "doWaterSplashEffect"
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 0.5F);

    }

    public void setRadius(float newRadius) {
        if (newRadius <= maxRadius && !this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, MathHelper.clamp(newRadius, 0.0F, maxRadius));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public void tick() {
        super.tick();
        if (++age > EXPIRE_TIME) {
            discard();
            return;
        }
        oldBB = getBoundingBox();
        setRadius(getRadius() + 0.12f);

        if (!level.isClientSide) {
            RayTraceResult hitresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
            if (hitresult.getType() == RayTraceResult.Type.BLOCK) {
                onHitBlock((BlockRayTraceResult) hitresult);
            }
            for (Entity entity : level.getEntities(this, this.getBoundingBox()).stream().filter(target -> canHitEntity(target) && !victims.contains(target)).collect(Collectors.toSet())) {
                damageEntity(entity);
                //IronsSpellbooks.LOGGER.info(entity.getName().getString());
                MagicManager.spawnParticles(level, ParticleHelper.BLOOD, entity.getX(), entity.getY(), entity.getZ(), 50, 0, 0, 0, .5, true);
                if (entity instanceof ShieldPart || entity instanceof AbstractShieldEntity) {
                    discard();
                    return;
                }
            }
            //spawnParticles();
        }
//        List<Entity> collisions = new ArrayList<>();
//        collisions.addAll(level.getEntities(this, this.getBoundingBox()));
//
//        collisions = collisions.stream().filter(target ->
//                target != getOwner() && target instanceof LivingEntity && !victims.contains(target)).collect(Collectors.toList());
//        for (Entity entity : collisions) {
//        }

        setPos(position().add(getDeltaMovement()));
        spawnParticles();
    }

    public EntitySize getDimensions(Pose p_19721_) {
        //irons_spellbooks.LOGGER.info("Accessing Blood Slash Dimensions. Age: {}", age);
        this.getBoundingBox();
        return EntitySize.scalable(this.getRadius() + 2.0F, 0.5F);
    }

    public void onSyncedDataUpdated(DataParameter<?> p_19729_) {
        //irons_spellbooks.LOGGER.info("onSynchedDataUpdated");

        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    //    private void increaseSize(float increase){
//        var bbOld = this.getBoundingBox();
//        double newWidth = (bbOld.getXsize() + increase) * .5;
//        double halfHeight = bbOld.getYsize() * .5;
//        Vec3 from = bbOld.getCenter().subtract(newWidth, halfHeight, newWidth);
//        Vec3 to = bbOld.getCenter().add(newWidth, halfHeight, newWidth);
//        this.setBoundingBox(new AABB(from.x,from.y,from.z,to.x,to.y,to.z));
//    }
//    @Override
//    protected void onHit(HitResult hitresult) {
//        if (hitresult.getType() == HitResult.Type.ENTITY) {
//            onHitEntity((EntityHitResult) hitresult);
//        } else if (hitresult.getType() == HitResult.Type.BLOCK) {
//            onHitBlock((BlockHitResult) hitresult);
//        }
//        double x = hitresult.getLocation().x;
//        double y = hitresult.getLocation().y;
//        double z = hitresult.getLocation().z;
//
//        MagicManager.spawnParticles(level, ParticleHelper.BLOOD, x, y, z, 50, 0, 0, 0, .5, true);
//
//
//    }

    @Override
    protected void onHitBlock(BlockRayTraceResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        discard();
    }

//    @Override
//    protected void onHitEntity(EntityHitResult entityHitResult) {
//        damageEntity(entityHitResult.getEntity());
//
//    }

    private void damageEntity(Entity entity) {
        if (!victims.contains(entity)) {
            DamageSources.applyDamage(entity, damage, SpellRegistry.BLOOD_SLASH_SPELL.get().getDamageSource(this, getOwner()));
            victims.add(entity);
        }
    }

    //https://forge.gemwire.uk/wiki/Particles
    public void spawnParticles() {
        if (level.isClientSide) {

            float width = (float) getBoundingBox().getXsize();
            float step = .25f;
            float radians = Utils.DEG_TO_RAD * getYRot();
            float speed = .1f;
            for (int i = 0; i < width / step; i++) {
                double x = getX();
                double y = getY();
                double z = getZ();
                double offset = step * (i - width / step / 2);
                double rotX = offset * Math.cos(radians);
                double rotZ = -offset * Math.sin(radians);

                double dx = Math.random() * speed * 2 - speed;
                double dy = Math.random() * speed * 2 - speed;
                double dz = Math.random() * speed * 2 - speed;
                level.addParticle(ParticleHelper.BLOOD, false, x + rotX + dx, y + dy, z + rotZ + dz, dx, dy, dz);
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity == getOwner())
            return false;
        return super.canHitEntity(entity);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.damage = pCompound.getFloat("Damage");
    }
}
