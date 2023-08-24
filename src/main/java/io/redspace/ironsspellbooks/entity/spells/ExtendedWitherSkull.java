package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.spells.SchoolType;
import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.network.IPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.network.NetworkHooks;

public class ExtendedWitherSkull extends WitherSkullEntity implements AntiMagicSusceptible {
    public ExtendedWitherSkull(EntityType<? extends WitherSkullEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    protected float damage;

    public ExtendedWitherSkull(LivingEntity shooter, World level, float speed, float damage) {
        super(EntityRegistry.WITHER_SKULL_PROJECTILE.get(), level);
        setOwner(shooter);

        Vector3d power = shooter.getLookAngle().normalize().scale(speed);

        this.xPower = power.x;
        this.yPower = power.y;
        this.zPower = power.z;
        this.damage = damage;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult pResult) {
        Entity entity = pResult.getEntity();
        DamageSources.applyDamage(entity, damage, SpellType.WITHER_SKULL_SPELL.getDamageSource(this, getOwner()), SchoolType.BLOOD);
 //Ironsspellbooks.logger.debug("hmm.");
    }

    @Override
    protected void onHit(RayTraceResult hitResult) {

        if (!this.level.isClientSide) {
            float explosionRadius = 2;
            var entities = level.getEntities(this, this.getBoundingBox().inflate(explosionRadius));
            for (Entity entity : entities) {
                double distance = entity.distanceToSqr(hitResult.getLocation());
                if (distance < explosionRadius * explosionRadius  && canHitEntity(entity)) {
                    float damage = (float) (this.damage * (1 - distance / (explosionRadius * explosionRadius)));
                    DamageSources.applyDamage(entity, damage, SpellType.WITHER_SKULL_SPELL.getDamageSource(this, getOwner()), SchoolType.BLOOD);
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
    public void onAntiMagic(PlayerMagicData playerMagicData) {
        this.discard();
    }
}
