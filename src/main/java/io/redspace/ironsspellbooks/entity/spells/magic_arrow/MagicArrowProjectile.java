package io.redspace.ironsspellbooks.entity.spells.magic_arrow;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.SchoolType;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MagicArrowProjectile extends AbstractMagicProjectile {
    private final List<Entity> victims = new ArrayList<>();
    private int penetration;

    @Override
    public void trailParticles() {
        Vector3d vec3 = this.position().subtract(getDeltaMovement());
        level.addParticle(ParticleHelper.UNSTABLE_ENDER, vec3.x, vec3.y, vec3.z, 0, 0, 0);
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(level, ParticleHelper.UNSTABLE_ENDER, x, y, z, 15, .1, .1, .1, .5, false);
    }

    @Override
    public float getSpeed() {
        return 2.7f;
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.empty();
    }

    public MagicArrowProjectile(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    public MagicArrowProjectile(World levelIn, LivingEntity shooter) {
        this(EntityRegistry.MAGIC_ARROW_PROJECTILE.get(), levelIn);
        setOwner(shooter);
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult pResult) {

    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (!victims.contains(entity)) {
            DamageSources.applyDamage(entity, damage, SpellType.MAGIC_ARROW_SPELL.getDamageSource(this, getOwner()), SchoolType.ENDER);
            victims.add(entity);
        }
    }


    @Override
    protected void onHit(RayTraceResult result) {
        //IronsSpellbooks.LOGGER.debug("onHit ({})", result.getType());

        penetration++;
        if (!level.isClientSide) {
            if (result.getType() == RayTraceResult.Type.ENTITY) {
                level.playSound(null, new BlockPos(position()), SoundRegistry.FORCE_IMPACT.get(), SoundCategory.NEUTRAL, 2, .65f);
                //IronsSpellbooks.LOGGER.debug("Playing Sound");
            }
        }

        super.onHit(result);
    }

    @Override
    protected boolean shouldPierceShields() {
        return true;
    }
}
