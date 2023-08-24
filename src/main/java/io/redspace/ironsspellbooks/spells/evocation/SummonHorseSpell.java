package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.mobs.SummonedHorse;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.*;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;

public class SummonHorseSpell extends AbstractSpell {
    public SummonHorseSpell() {
        this(1);
    }

    public SummonHorseSpell(int level) {
        super(SpellType.SUMMON_HORSE_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 2;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 50;

    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.EVOCATION)
            .setMaxLevel(5)
            .setCooldownSeconds(20)
            .build();

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ILLUSIONER_PREPARE_MIRROR);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ILLUSIONER_MIRROR_MOVE);
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        int summonTime = 20 * 60 * 10;
        Vector3d spawn = entity.position();
        Vector3d forward = entity.getForward().normalize().scale(1.5f);
        spawn.add(forward.x, 0.15f, forward.z);

        //Teleport pre-existing or create new horse
        var horses = world.getEntitiesOfClass(SummonedHorse.class, entity.getBoundingBox().inflate(100), (summonedHorse) -> summonedHorse.getSummoner() == entity && !summonedHorse.isDeadOrDying());
        SummonedHorse horse = horses.size() > 0 ? horses.get(0) : new SummonedHorse(world, entity);

        horse.setPos(spawn);
        horse.removeEffectNoUpdate(MobEffectRegistry.SUMMON_HORSE_TIMER.get());
        horse.forceAddEffect(new EffectInstance(MobEffectRegistry.SUMMON_HORSE_TIMER.get(), summonTime, 0, false, false, false), null);
        setAttributes(horse, getSpellPower(entity));

        world.addFreshEntity(horse);
        entity.addEffect(new EffectInstance(MobEffectRegistry.SUMMON_HORSE_TIMER.get(), summonTime, 0, false, false, true));

        super.onCast(world, entity, playerMagicData);
    }

    private void setAttributes(AbstractHorseEntity horse, float power) {
        int maxPower = baseSpellPower + (ServerConfigs.getSpellConfig(SpellType.SUMMON_HORSE_SPELL).maxLevel() - 1) * spellPowerPerLevel;
        float quality = power / (float) maxPower;

        float minSpeed = .2f;
        float maxSpeed = .45f;

        float minJump = .6f;
        float maxJump = 1f;

        float minHealth = 10;
        float maxHealth = 40;

        horse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(quality, minSpeed, maxSpeed));
        horse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(MathHelper.lerp(quality, minJump, maxJump));
        horse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(MathHelper.lerp(quality, minHealth, maxHealth));
        if (!horse.isDeadOrDying())
            horse.setHealth(horse.getMaxHealth());
    }
}
