package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoid;
import io.redspace.ironsspellbooks.network.spell.ClientboundFrostStepParticles;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class FrostStepSpell extends AbstractSpell {

    public FrostStepSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                ITextComponent.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getDistance(caster), 1)),
                ITextComponent.translatable("ui.irons_spellbooks.shatter_damage", Utils.stringTruncation(getDamage(caster), 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchool(SchoolType.ICE)
            .setMaxLevel(8)
            .setCooldownSeconds(10)
            .build();

    public FrostStepSpell(int level) {
        super(SpellType.FROST_STEP_SPELL);
        this.setLevel(level);
        this.baseSpellPower = 14;
        this.spellPowerPerLevel = 3;
        this.baseManaCost = 15;
        this.manaCostPerLevel = 3;
        this.castTime = 0;


    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.FROST_STEP.get());
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        var teleportData = (TeleportSpell.TeleportData) playerMagicData.getAdditionalCastData();

        FrozenHumanoid shadow = new FrozenHumanoid(level, entity);
        shadow.setShatterDamage(getDamage(entity));
        shadow.setDeathTimer(60);
        level.addFreshEntity(shadow);
        Vector3d dest = null;
        if (teleportData != null) {
            var potentialTarget = teleportData.getTeleportTargetPosition();
            dest = potentialTarget;
        }

        if (dest == null) {
            dest = findTeleportLocation(level, entity);
        }
        Messages.sendToPlayersTrackingEntity(new ClientboundFrostStepParticles(entity.position(), dest), entity, true);
        if (entity.isPassenger()) {
            entity.stopRiding();
        }
        entity.teleportTo(dest.x, dest.y, dest.z);
        entity.resetFallDistance();
        level.playSound(null, dest.x, dest.y, dest.z, getCastFinishSound().get(), SoundCategory.NEUTRAL, 1f, 1f);

        playerMagicData.resetAdditionalCastData();

        super.onCast(level, entity, playerMagicData);
    }

    private Vector3d findTeleportLocation(World level, LivingEntity entity) {
        return TeleportSpell.findTeleportLocation(level, entity, getDistance(entity));
    }

    public static void particleCloud(World level, Vector3d pos) {
        if (level.isClientSide) {
            double width = 0.5;
            float height = 1;
            for (int i = 0; i < 25; i++) {
                double x = pos.x + level.random.nextDouble() * width * 2 - width;
                double y = pos.y + height + level.random.nextDouble() * height * 1.2 * 2 - height * 1.2;
                double z = pos.z + level.random.nextDouble() * width * 2 - width;
                double dx = level.random.nextDouble() * .1 * (level.random.nextBoolean() ? 1 : -1);
                double dy = level.random.nextDouble() * .1 * (level.random.nextBoolean() ? 1 : -1);
                double dz = level.random.nextDouble() * .1 * (level.random.nextBoolean() ? 1 : -1);
                level.addParticle(ParticleHelper.SNOWFLAKE, true, x, y, z, dx, dy, dz);
                level.addParticle(ParticleTypes.SNOWFLAKE, true, x, y, z, -dx, -dy, -dz);
            }
        }
    }

    private float getDistance(LivingEntity sourceEntity) {
        return getSpellPower(sourceEntity) * .65f;
    }

    private float getDamage(LivingEntity caster) {
        return this.getSpellPower(caster) / 3;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }
}
