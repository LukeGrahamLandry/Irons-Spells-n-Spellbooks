package io.redspace.ironsspellbooks.entity.spells.devour_jaw;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.particles.IParticleData;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkHooks;

public class DevourJaw extends AoeEntity {
    public DevourJaw(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    LivingEntity target;

    public DevourJaw(World level, LivingEntity owner, LivingEntity target) {
        this(EntityRegistry.DEVOUR_JAW.get(), level);
        setOwner(owner);
        this.target = target;
    }

    //dont need to serialize, dont need it only client either
    public int vigorLevel;

    @Override
    public void applyEffect(LivingEntity target) {
        if (target == this.target) {
            if (DamageSources.applyDamage(target, getDamage(), SpellRegistry.DEVOUR_SPELL.get().getDamageSource(this, getOwner()), SpellRegistry.DEVOUR_SPELL.get().getSchoolType()) && getOwner() instanceof LivingEntity) {
                LivingEntity livingOwner = (LivingEntity) getOwner();
                target.setDeltaMovement(target.getDeltaMovement().add(0, .5f, 0));
                target.hurtMarked = true;
                if (target.isDeadOrDying()) {
                    EffectInstance oldVigor = livingOwner.getEffect(MobEffectRegistry.VIGOR.get());
                    int addition = 0;
                    if (oldVigor != null)
                        addition = oldVigor.getAmplifier() + 1;
                    livingOwner.addEffect(new EffectInstance(MobEffectRegistry.VIGOR.get(), 20 * 60, Math.min(vigorLevel + addition, 9), false, false, true));
                    livingOwner.heal((vigorLevel + 1) * 2);
                }
            }
        }
    }

    public final int waitTime = 5;
    public final int warmupTime = waitTime + 8;
    public final int deathTime = warmupTime + 8;

    @Override
    public void tick() {
        if (tickCount < waitTime) {
            if (this.target != null)
                setPos(this.target.position());
        } else if (tickCount == waitTime) {
            this.playSound(SoundRegistry.DEVOUR_BITE.get(), 2, 1);
        } else if (tickCount == warmupTime) {
            if (level.isClientSide) {
                float y = this.yRot;
                int countPerSide = 25;
                //These particles were not at all what I intended. But they're cooler. no clue how it works
                for (int i = -countPerSide; i < countPerSide; i++) {
                    Vector3d motion = new Vector3d(0, Math.abs(countPerSide) - i, countPerSide * .5f).yRot(y).normalize().multiply(.4f, .8f, .4f);
                    level.addParticle(ParticleHelper.BLOOD, getX(), getY() + .5f, getZ(), motion.x, motion.y, motion.z);
                }
            } else {
                checkHits();
            }
        } else if (tickCount > deathTime)
            this.remove();
    }

    @Override
    protected Vector3d getInflation() {
        return new Vector3d(2, 2, 2);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
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
    public float getParticleCount() {
        return 0;
    }

    @Override
    public IParticleData getParticle() {
        return ParticleHelper.BLOOD;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
