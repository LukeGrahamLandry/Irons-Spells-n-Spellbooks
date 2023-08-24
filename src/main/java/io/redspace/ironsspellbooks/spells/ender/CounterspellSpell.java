package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.Effect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;
import java.util.Optional;

public class CounterspellSpell extends AbstractSpell {
    public static final LazyOptional<List<Effect>> LAZY_MAGICAL_EFFECTS = LazyOptional.of(() ->
            List.of(MobEffectRegistry.ABYSSAL_SHROUD.get(),
                    MobEffectRegistry.ASCENSION.get(),
                    MobEffectRegistry.ANGEL_WINGS.get(),
                    MobEffectRegistry.CHARGED.get(),
                    MobEffectRegistry.EVASION.get(),
                    MobEffectRegistry.HEARTSTOP.get(),
                    MobEffectRegistry.FORTIFY.get(),
                    MobEffectRegistry.TRUE_INVISIBILITY.get(),
                    MobEffectRegistry.FORTIFY.get(),
                    MobEffectRegistry.REND.get(),
                    MobEffectRegistry.SPIDER_ASPECT.get(),
                    MobEffectRegistry.BLIGHT.get()
            ));

    public CounterspellSpell() {
        this(1);
    }

    public CounterspellSpell(int level) {
        super(SpellType.COUNTERSPELL_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 1;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 50;

    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchool(SchoolType.ENDER)
            .setMaxLevel(1)
            .setCooldownSeconds(15)
            .build();

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        Vector3d start = entity.getEyePosition();
        Vector3d end = start.add(entity.getForward().normalize().scale(80));
        RayTraceResult hitResult = Utils.raycastForEntity(entity.level, entity, start, end, true, 0.35f, Utils::validAntiMagicTarget);
        Vector3d forward = entity.getForward().normalize();
        if (hitResult instanceof EntityRayTraceResult entityHitResult) {
            double distance = entity.distanceTo(entityHitResult.getEntity());
            for (float i = 1; i < distance; i += .5f) {
                Vector3d pos = entity.getEyePosition().add(forward.scale(i));
                MagicManager.spawnParticles(world, ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0, false);
            }
            if (entityHitResult.getEntity() instanceof AntiMagicSusceptible antiMagicSusceptible && !(antiMagicSusceptible instanceof MagicSummon summon && summon.getSummoner() == entity))
                antiMagicSusceptible.onAntiMagic(playerMagicData);
            else if (entityHitResult.getEntity() instanceof ServerPlayerEntity serverPlayer)
                Utils.serverSideCancelCast(serverPlayer, true);
            else if (entityHitResult.getEntity() instanceof AbstractSpellCastingMob abstractSpellCastingMob)
                abstractSpellCastingMob.cancelCast();

            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity)
                for (Effect mobEffect : LAZY_MAGICAL_EFFECTS.resolve().get())
                    livingEntity.removeEffect(mobEffect);
        }else{
            for (float i = 1; i < 40; i += .5f) {
                Vector3d pos = entity.getEyePosition().add(forward.scale(i));
                MagicManager.spawnParticles(world, ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0, false);
                if (!world.getBlockState(new BlockPos(pos)).isAir())
                    break;
            }
        }
        super.onCast(world, entity, playerMagicData);
    }


}