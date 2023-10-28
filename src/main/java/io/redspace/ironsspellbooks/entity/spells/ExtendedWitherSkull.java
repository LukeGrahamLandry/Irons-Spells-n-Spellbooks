package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.network.IPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

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
    protected void onHit(RayTraceResult hitResult) {

        if (!this.level.isClientSide) {
            float explosionRadius = 2;
            List<Entity> entities = level.getEntities(this, this.getBoundingBox().inflate(explosionRadius));
            for (Entity entity : entities) {
                double distance = entity.distanceToSqr(hitResult.getLocation());
                if (distance < explosionRadius * explosionRadius  && canHitEntity(entity)) {
                    float damage = (float) (this.damage * (1 - distance / (explosionRadius * explosionRadius)));
                    AbstractSpell spell = SpellRegistry.WITHER_SKULL_SPELL.get();
                    DamageSources.applyDamage(entity, damage, spell.getDamageSource(this, getOwner()), spell.getSchoolType());
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
