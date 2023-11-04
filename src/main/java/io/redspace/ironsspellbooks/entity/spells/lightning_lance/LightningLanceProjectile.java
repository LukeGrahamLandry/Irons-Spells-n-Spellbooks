package io.redspace.ironsspellbooks.entity.spells.lightning_lance;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;

public class LightningLanceProjectile extends AbstractMagicProjectile {

    @Override
    public void trailParticles() {
        Vector3d vec3 = this.position().subtract(getDeltaMovement());
        level.addParticle(ParticleHelper.ELECTRICITY, vec3.x, vec3.y, vec3.z, 0, 0, 0);
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(level, ParticleHelper.ELECTRICITY, x, y, z, 75, .1, .1, .1, 2, true);
        MagicManager.spawnParticles(level, ParticleHelper.ELECTRICITY, x, y, z, 75, .1, .1, .1, .5, false);
    }

    @Override
    public float getSpeed() {
        return 3f;
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.empty();
    }

    public LightningLanceProjectile(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(false);
    }

    public LightningLanceProjectile(World levelIn, LivingEntity shooter) {
        this(EntityRegistry.LIGHTNING_LANCE_PROJECTILE.get(), levelIn);
        setOwner(shooter);
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult pResult) {

    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        DamageSources.applyDamage(entityHitResult.getEntity(), damage, SpellRegistry.LIGHTNING_LANCE_SPELL.get().getDamageSource(this, getOwner()), SpellRegistry.LIGHTNING_LANCE_SPELL.get().getSchoolType());

    }

    @Override
    protected void onHit(RayTraceResult pResult) {
        //irons_spellbooks.LOGGER.debug("Boom");

        if (!level.isClientSide) {
            this.playSound(SoundEvents.TRIDENT_THUNDER, 6, .65f);
//            irons_spellbooks.LOGGER.debug("{}",pos);
//            //Beam
//            for (int i = 0; i < 40; i++) {
//                Vec3 randomVec = new Vec3(
//                        Utils.random.nextDouble() * .25 - .125,
//                        Utils.random.nextDouble() * .25 - .125,
//                        Utils.random.nextDouble() * .25 - .125
//                );
//                //level.addParticle(ParticleHelper.ELECTRICITY, pos.x + randomVec.x, pos.y + randomVec.y + i * .25, pos.z + randomVec.z, randomVec.x * .2, randomVec.y * .2, randomVec.z * .2);
//                level.addParticle(ParticleHelper.ELECTRICITY, pos.x, pos.y, pos.z, 0,0,0);
//            }
        }
        super.onHit(pResult);
        this.remove();
    }

    public int getAge(){
        return age;
    }

    @Override
    public boolean respectsGravity() {
        return true;
    }
}
