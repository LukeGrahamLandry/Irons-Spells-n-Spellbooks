package io.redspace.ironsspellbooks.entity.spells.poison_cloud;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class PoisonCloud extends AoeEntity {

    public static final DamageSource DAMAGE_SOURCE = new DamageSource(String.format("%s.%s", IronsSpellbooks.MODID, "poison_cloud"));

    public PoisonCloud(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);

    }

    public PoisonCloud(World level) {
        this(EntityRegistry.POISON_CLOUD.get(), level);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        var damageSource = DamageSources.indirectDamageSource(DAMAGE_SOURCE, this, getOwner());
        DamageSources.ignoreNextKnockback(target);
        target.hurt(damageSource, getDamage());
        target.addEffect(new EffectInstance(Effects.POISON, 120, (int) getDamage()));
    }

    @Override
    public float getParticleCount() {
        return .15f;
    }

    @Override
    public IParticleData getParticle() {
        return ParticleHelper.POISON_CLOUD;
    }
}
