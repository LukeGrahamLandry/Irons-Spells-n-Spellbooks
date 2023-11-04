package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.mobs.SummonedHorse;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class SummonHorseSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "summon_horse");

    public SummonHorseSpell() {
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 2;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 50;
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(20)
            .build();

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ILLUSIONER_PREPARE_MIRROR);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ILLUSIONER_MIRROR_MOVE);
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        int summonTime = 20 * 60 * 10;
        Vector3d spawn = entity.position();
        Vector3d forward = entity.getForward().normalize().scale(1.5f);
        spawn.add(forward.x, 0.15f, forward.z);

        //Teleport pre-existing or create new horse
        List<SummonedHorse> horses = world.getEntitiesOfClass(SummonedHorse.class, entity.getBoundingBox().inflate(100), (summonedHorse) -> summonedHorse.getSummoner() == entity && !summonedHorse.isDeadOrDying());
        SummonedHorse horse = horses.size() > 0 ? horses.get(0) : new SummonedHorse(world, entity);

        horse.moveTo(spawn);
        horse.removeEffectNoUpdate(MobEffectRegistry.SUMMON_HORSE_TIMER.get());
        horse.forceAddEffect(new EffectInstance(MobEffectRegistry.SUMMON_HORSE_TIMER.get(), summonTime, 0, false, false, false));
        setAttributes(horse, getSpellPower(spellLevel, entity));

        world.addFreshEntity(horse);
        entity.addEffect(new EffectInstance(MobEffectRegistry.SUMMON_HORSE_TIMER.get(), summonTime, 0, false, false, true));

        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    private void setAttributes(AbstractHorseEntity horse, float power) {
        int maxPower = baseSpellPower + (ServerConfigs.getSpellConfig(this).maxLevel() - 1) * spellPowerPerLevel;
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
