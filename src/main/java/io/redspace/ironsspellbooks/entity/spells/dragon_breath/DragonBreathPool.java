package io.redspace.ironsspellbooks.entity.spells.dragon_breath;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class DragonBreathPool extends AoeEntity {

    public static final DamageSource DAMAGE_SOURCE = new DamageSource(SpellRegistry.DRAGON_BREATH_SPELL.get().getDeathMessageId() + "_pool").setMagic();

    public DragonBreathPool(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        setCircular();
        setRadius(1.8f);
        this.radiusOnUse = -.15f;
        this.radiusPerTick = -.02f;
        IronsSpellbooks.LOGGER.debug("Creating DragonBreathPool");
    }

    public DragonBreathPool(World level) {
        this(EntityRegistry.DRAGON_BREATH_POOL.get(), level);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        var damageSource = DamageSources.indirectDamageSource(DAMAGE_SOURCE, this, getOwner());
        DamageSources.ignoreNextKnockback(target);
        target.hurt(damageSource, getDamage());
    }

    @Override
    public float getParticleCount() {
        return 4f;
    }

    @Override
    public IParticleData getParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }
}
