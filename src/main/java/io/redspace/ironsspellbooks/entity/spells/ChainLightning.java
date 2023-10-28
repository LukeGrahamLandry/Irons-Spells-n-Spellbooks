package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ChainLightning extends AbstractMagicProjectile {
    List<Entity> allVictims;
    List<Entity> lastVictims;
    Entity initialVictim;
    public int maxConnections = 4;
    public int maxConnectionsPerWave = 3;
    public float range = 3f;
    private final static Supplier<AbstractSpell> SPELL = SpellRegistry.CHAIN_LIGHTNING_SPELL;

    public ChainLightning(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        allVictims = new ArrayList<>();
        lastVictims = new ArrayList<>();
    }

    public ChainLightning(World level, Entity owner, Entity initialVictim) {
        this(EntityRegistry.CHAIN_LIGHTNING.get(), level);
        this.setOwner(owner);
        this.setPos(initialVictim.position());
        this.initialVictim = initialVictim;
    }

    int hits;

    @Override
    public void tick() {
        super.tick();
        int f = tickCount - 1;
        if (!this.level.isClientSide && f % 6 == 0) {
            if (f == 0 && !allVictims.contains(initialVictim)) {
                //First time zap
                doHurt(initialVictim);
                if (getOwner() != null) {
                    Vector3d start = getOwner().position().add(0, getOwner().getBbHeight() / 2, 0);
                    PositionSource dest = new EntityPositionSource(initialVictim, initialVictim.getBbHeight() / 2);
                    ((ServerWorld) level).sendParticles(new ZapParticleOption(dest), start.x, start.y, start.z, 1, 0, 0, 0, 0);
                }

            } else {
                int j = lastVictims.size();
                AtomicInteger zapsThisWave = new AtomicInteger();
                //cannot be enhanced for
                for (int i = 0; i < j; i++) {
                    var entity = lastVictims.get(i);
                    var entities = level.getEntities(entity, entity.getBoundingBox().inflate(range), this::canHitEntity);
                    entities.sort((o1, o2) -> (int) (o1.distanceToSqr(entity) - o2.distanceToSqr(entity)));
                    entities.forEach((victim) -> {
                        if (zapsThisWave.get() < maxConnectionsPerWave && hits < maxConnections && victim.distanceToSqr(entity) < range * range && Utils.hasLineOfSight(level, entity.getEyePosition(), victim.getEyePosition(), true)) {
                            doHurt(victim);
                            victim.playSound(SoundRegistry.CHAIN_LIGHTNING_CHAIN.get(), 2, 1);
                            zapsThisWave.getAndIncrement();
                            Vector3d start = new Vector3d(entity.xOld, entity.yOld, entity.zOld).add(0, entity.getBbHeight() / 2, 0);
                            PositionSource dest = new EntityPositionSource(victim, victim.getBbHeight() / 2);
                            ((ServerWorld) level).sendParticles(new ZapParticleOption(dest), start.x, start.y, start.z, 1, 0, 0, 0, 0);
                        }
                    });
                }
                lastVictims.removeAll(allVictims);
            }
            allVictims.addAll(lastVictims);
        }
    }

    public void doHurt(Entity victim) {
        hits++;
        DamageSources.applyDamage(victim, damage, SPELL.get().getDamageSource(this, getOwner()), SPELL.get().getSchoolType());
        MagicManager.spawnParticles(level, ParticleHelper.ELECTRICITY, victim.getX(), victim.getY() + victim.getBbHeight() / 2, victim.getZ(), 10, victim.getBbWidth() / 3, victim.getBbHeight() / 3, victim.getBbWidth() / 3, 0.1, false);

        lastVictims.add(victim);
    }

    public boolean hasAlreadyZapped(Entity entity) {
        return allVictims.contains(entity) || lastVictims.contains(entity);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return target instanceof LivingEntity && !DamageSources.isFriendlyFireBetween(target, getOwner()) && target != getOwner() && !hasAlreadyZapped(target) && super.canHitEntity(target);
    }

    @Override
    public void trailParticles() {

    }

    @Override
    public void impactParticles(double x, double y, double z) {

    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.empty();
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
