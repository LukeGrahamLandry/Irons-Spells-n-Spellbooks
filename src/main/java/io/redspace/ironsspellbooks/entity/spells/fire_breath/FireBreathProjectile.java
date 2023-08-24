package io.redspace.ironsspellbooks.entity.spells.fire_breath;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.spells.SchoolType;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class FireBreathProjectile extends AbstractConeProjectile {
    public FireBreathProjectile(EntityType<? extends AbstractConeProjectile> entityType, World level) {
        super(entityType, level);
    }

    public FireBreathProjectile(World level, LivingEntity entity) {
        super(EntityRegistry.FIRE_BREATH_PROJECTILE.get(), level, entity);
    }

    @Override
    public void tick() {
        if (!level.isClientSide && getOwner() != null)
            if (dealDamageActive) {
                //Set Fire Blocks
                boolean doFire = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner());

                if(doFire){
                    float range = 15 * MathHelper.DEG_TO_RAD;
                    for (int i = 0; i < 3; i++) {
                        Vector3d cast = getOwner().getLookAngle().normalize().xRot(level.random.nextFloat() * range * 2 - range).yRot(level.random.nextFloat() * range * 2 - range);
                        RayTraceResult hitResult = level.clip(new RayTraceContext(getOwner().getEyePosition(), getOwner().getEyePosition().add(cast.scale(10)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                        if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
                            RayTraceResult shieldResult = Utils.raycastForEntityOfClass(level, this, getOwner().getEyePosition(), hitResult.getLocation(), false, AbstractShieldEntity.class);
                            if (shieldResult.getType() == RayTraceResult.Type.MISS) {
                                Vector3d pos = hitResult.getLocation().subtract(cast.scale(.5));
                                BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);
                                if (level.getBlockState(blockPos).isAir())
                                    level.setBlockAndUpdate(blockPos, AbstractFireBlock.getState(this.level, blockPos));
                            }

                        }
                    }
                }

            }
        super.tick();
    }


    @Override
    public void spawnParticles() {
        var owner = getOwner();
        if (!level.isClientSide || owner == null) {
            return;
        }
        Vector3d rotation = owner.getLookAngle().normalize();
        var pos = owner.position().add(rotation.scale(1.6));

        double x = pos.x;
        double y = pos.y + owner.getEyeHeight() * .9f;
        double z = pos.z;

        double speed = random.nextDouble() * .35 + .35;
        for (int i = 0; i < 10; i++) {
            double offset = .15;
            double ox = Math.random() * 2 * offset - offset;
            double oy = Math.random() * 2 * offset - offset;
            double oz = Math.random() * 2 * offset - offset;

            double angularness = .5;
            Vector3d randomVec = new Vector3d(Math.random() * 2 * angularness - angularness, Math.random() * 2 * angularness - angularness, Math.random() * 2 * angularness - angularness).normalize();
            Vector3d result = (rotation.scale(3).add(randomVec)).normalize().scale(speed);
            level.addParticle(ParticleHelper.FIRE, x + ox, y + oy, z + oz, result.x, result.y, result.z);
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        var entity = entityHitResult.getEntity();
        if (DamageSources.applyDamage(entity, damage, SpellType.FIRE_BREATH_SPELL.getDamageSource(this, getOwner()), SchoolType.FIRE))
            entity.setSecondsOnFire(4);
    }
}
