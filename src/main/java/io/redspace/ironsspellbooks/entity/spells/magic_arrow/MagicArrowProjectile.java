package io.redspace.ironsspellbooks.entity.spells.magic_arrow;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
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
            DamageSources.applyDamage(entity, damage, SpellRegistry.MAGIC_ARROW_SPELL.get().getDamageSource(this, getOwner()), SpellRegistry.MAGIC_ARROW_SPELL.get().getSchoolType());
            victims.add(entity);
        }
    }

    BlockPos lastHitBlock;

    @Override
    protected void onHit(RayTraceResult result) {
        //IronsSpellbooks.LOGGER.debug("onHit ({})", result.getType());

        if (!level.isClientSide) {
            BlockPos blockPos = new BlockPos(result.getLocation());
            if (result.getType() == RayTraceResult.Type.BLOCK && !blockPos.equals(lastHitBlock)) {
                penetration++;
                lastHitBlock = blockPos;
            } else if (result.getType() == RayTraceResult.Type.ENTITY) {
                penetration++;
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
