package io.redspace.ironsspellbooks.entity.spells.sunbeam;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class Sunbeam extends AoeEntity implements AntiMagicSusceptible {

    public Sunbeam(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        this.setRadius((float) (this.getBoundingBox().getXsize() * .5f));
        this.setNoGravity(true);
    }

    public Sunbeam(World level) {
        this(EntityRegistry.SUNBEAM.get(), level);
    }


    @Override
    public void tick() {

        if (tickCount == 4) {
            checkHits();
            if (!level.isClientSide)
                MagicManager.spawnParticles(level, ParticleTypes.FIREWORK, getX(), getY(), getZ(), 9, getRadius() * .7f, .2f, getRadius() * .7f, 1, true);
        }

        if (this.tickCount > 6) {
            this.remove();
        }
    }


    @Override
    public void applyEffect(LivingEntity target) {
        DamageSources.applyDamage(target, getDamage(), SpellRegistry.SUNBEAM_SPELL.get().getDamageSource(this, getOwner()), SpellRegistry.SUNBEAM_SPELL.get().getSchoolType());
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
        return ParticleHelper.SIPHON;
    }

    @Override
    public void onAntiMagic(MagicData magicData) {
        this.remove();
    }
}
