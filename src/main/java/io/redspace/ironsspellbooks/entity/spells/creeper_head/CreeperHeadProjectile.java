package io.redspace.ironsspellbooks.entity.spells.creeper_head;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.spells.evocation.ChainCreeperSpell;
import net.minecraft.network.IPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.network.NetworkHooks;

public class CreeperHeadProjectile extends WitherSkullEntity implements AntiMagicSusceptible {
    public CreeperHeadProjectile(EntityType<? extends WitherSkullEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        chainOnKill = false;
    }

    protected float damage;
    protected boolean chainOnKill;

    public CreeperHeadProjectile(LivingEntity shooter, World level, float speed, float damage) {
        this(EntityRegistry.CREEPER_HEAD_PROJECTILE.get(), level);
        setOwner(shooter);

        Vector3d power = shooter.getLookAngle().normalize().scale(speed);

        this.xPower = power.x;
        this.yPower = power.y;
        this.zPower = power.z;
        setDeltaMovement(xPower, yPower, zPower);
        this.damage = damage;
    }

    public CreeperHeadProjectile(LivingEntity shooter, World level, Vector3d power, float damage) {
        this(EntityRegistry.CREEPER_HEAD_PROJECTILE.get(), level);
        setOwner(shooter);

        this.xPower = power.x;
        this.yPower = power.y;
        this.zPower = power.z;
        setDeltaMovement(xPower, yPower, zPower);
        this.damage = damage;
    }

    public void setChainOnKill(boolean chain) {
        chainOnKill = chain;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult pResult) {
    }

    @Override
    public void tick() {
        //super.tick();
//        if (!this.isNoGravity()) {
//            Vec3 vec34 = this.getDeltaMovement();
//            this.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
//        }
        if (!level.isClientSide) {
            RayTraceResult hitresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
            if (hitresult.getType() != RayTraceResult.Type.MISS) {
                onHit(hitresult);
            }
        } else {
            this.level.addParticle(this.getTrailParticle(), position().x, position().y + 0.25D, position().z, 0.0D, 0.0D, 0.0D);
        }
        ProjectileHelper.rotateTowardsMovement(this, 1);
        setPos(position().add(getDeltaMovement()));

        if (!this.isNoGravity()) {
            Vector3d vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
        }


        this.baseTick();
    }

    @Override
    protected void onHit(RayTraceResult hitResult) {
        if (!this.level.isClientSide) {
            float explosionRadius = 3.5f;
            var entities = level.getEntities(this, this.getBoundingBox().inflate(explosionRadius));
            for (Entity entity : entities) {
                double distance = entity.position().distanceTo(hitResult.getLocation());
                if (distance < explosionRadius) {
                    //Prevent duplicate chains
                    if (entity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying() && !canHitEntity(entity))
                        break;
                    float damage = (float) (this.damage * (1 - Math.pow(distance / (explosionRadius), 2)));
                    DamageSources.applyDamage(entity, damage, SpellRegistry.LOB_CREEPER_SPELL.get().getDamageSource(this, getOwner()), SpellRegistry.LOB_CREEPER_SPELL.get().getSchoolType());
                    entity.invulnerableTime = 0;
                    if (chainOnKill && entity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying())
                        ChainCreeperSpell.summonCreeperRing(this.level, this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null, livingEntity.getEyePosition(0), this.damage * .85f, 3);
                }
            }

            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, false, Explosion.Mode.NONE);
            this.discard();
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }
}
