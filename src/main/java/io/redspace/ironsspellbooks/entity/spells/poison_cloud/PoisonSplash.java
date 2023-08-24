package io.redspace.ironsspellbooks.entity.spells.poison_cloud;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.spells.SchoolType;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

public class PoisonSplash extends AoeEntity {

    public PoisonSplash(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        this.setRadius((float) (this.getBoundingBox().getXsize() * .5f));
    }

    public PoisonSplash(World level) {
        this(EntityRegistry.POISON_SPLASH.get(), level);
    }

    boolean playedParticles;

    @Override
    public void tick() {
        if (!playedParticles) {
            playedParticles = true;
            if (level.isClientSide) {
                for (int i = 0; i < 150; i++) {
                    Vector3d pos = new Vector3d(Utils.getRandomScaled(.5f), Utils.getRandomScaled(.2f), this.random.nextFloat() * getRadius()).yRot(this.random.nextFloat() * 360);
                    Vector3d motion = new Vector3d(
                            Utils.getRandomScaled(.06f),
                            this.random.nextDouble() * -.8 - .5,
                            Utils.getRandomScaled(.06f)
                    );

                    level.addParticle(ParticleHelper.ACID, getX() + pos.x, getY() + pos.y + getBoundingBox().getYsize(), getZ() + pos.z, motion.x, motion.y, motion.z);
                }
            }else{
                MagicManager.spawnParticles(level, ParticleHelper.POISON_CLOUD, getX(), getY()  + getBoundingBox().getYsize(), getZ(), 9, getRadius() * .7f, .2f, getRadius() * .7f, 1, true);

            }
        }

        if (tickCount == 4) {
            checkHits();
            if (!level.isClientSide)
                MagicManager.spawnParticles(level, ParticleHelper.POISON_CLOUD, getX(), getY(), getZ(), 9, getRadius() * .7f, .2f, getRadius() * .7f, 1, true);
            createPoisonCloud();
        }

        if (this.tickCount > 6) {
            discard();
        }
    }

    public void createPoisonCloud() {
        if (!level.isClientSide) {
            PoisonCloud cloud = new PoisonCloud(level);
            cloud.setOwner(getOwner());
            cloud.setDuration(getEffectDuration());
            cloud.setDamage(getDamage() * .1f);
            cloud.moveTo(this.position());
            level.addFreshEntity(cloud);
        }
    }

    @Override
    public void applyEffect(LivingEntity target) {
        if (DamageSources.applyDamage(target, getDamage(), SpellType.POISON_SPLASH_SPELL.getDamageSource(this, getOwner()), SchoolType.POISON))
            target.addEffect(new EffectInstance(Effects.POISON, getEffectDuration(), 0));
    }


    @Override
    public float getParticleCount() {
        return 0f;
    }

    @Override
    public void refreshDimensions() {
        return;
    }

    @Override
    public void ambientParticles() {
        return;
    }

    @Override
    public IParticleData getParticle() {
        return ParticleHelper.ACID_BUBBLE;
    }
}
