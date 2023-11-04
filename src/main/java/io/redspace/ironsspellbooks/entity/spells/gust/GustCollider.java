package io.redspace.ironsspellbooks.entity.spells.gust;


import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class GustCollider extends AbstractConeProjectile {

    public GustCollider(World level, LivingEntity owner) {
        this(EntityRegistry.GUST_COLLIDER.get(), level);
        this.setOwner(owner);
        this.setRot(owner.yRot, owner.xRot);
    }

    public GustCollider(EntityType<GustCollider> gustColliderEntityType, World level) {
        super(gustColliderEntityType, level);
    }

    @Override
    public void spawnParticles() {
        if (!level.isClientSide || tickCount > 2) {
            return;
        }
        Vector3d rotation = this.getLookAngle().normalize();
        Vector3d pos = this.position().add(rotation.scale(1.6));

        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        double speed = random.nextDouble() * .4 + .45;
        for (int i = 0; i < 5; i++) {
            double offset = .25;
            double ox = Math.random() * 2 * offset - offset;
            double oy = Math.random() * 2 * offset - offset;
            double oz = Math.random() * 2 * offset - offset;
            double angularness = .8;
            Vector3d randomVec = new Vector3d(Math.random() * 2 * angularness - angularness, Math.random() * 2 * angularness - angularness, Math.random() * 2 * angularness - angularness).normalize();
            Vector3d result = (rotation.scale(3).add(randomVec)).normalize().scale(speed);
            level.addParticle(ParticleTypes.POOF, x + ox, y + oy, z + oz, result.x, result.y, result.z);
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        Entity entity = getOwner();
        Entity resultEntity = entityHitResult.getEntity();
        if (entity != null && resultEntity instanceof LivingEntity && ((LivingEntity) resultEntity).distanceToSqr(entity) < range * range) {
            LivingEntity target = (LivingEntity) resultEntity;
            if (!DamageSources.isFriendlyFireBetween(entity, target)) {
                target.knockback(strength, entity.getX() - target.getX(), entity.getZ() - target.getZ());
                target.hurtMarked = true;
                target.addEffect(new EffectInstance(MobEffectRegistry.AIRBORNE.get(), 60, amplifier));
            }
        }

    }

    @Override
    public void tick() {
        if (tickCount > 8)
            this.remove();
        else
            super.tick();
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (tickCount >= 1)
            return null;
        return super.getOwner();
    }

    public float strength;
    public float range;
    public int amplifier;
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
