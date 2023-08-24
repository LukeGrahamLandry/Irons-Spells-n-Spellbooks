package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.mobs.SummonedPolarBear;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class SummonPolarBearSpell extends AbstractSpell {
    public SummonPolarBearSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.hp", getBearHealth(null)),
                new TranslationTextComponent("ui.irons_spellbooks.damage", getBearDamage(null))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchool(SchoolType.ICE)
            .setMaxLevel(10)
            .setCooldownSeconds(180)
            .build();

    public SummonPolarBearSpell(int level) {
        super(SpellType.SUMMON_POLAR_BEAR_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 4;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 50;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.EVOKER_CAST_SPELL);
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        int summonTime = 20 * 60 * 10;

        SummonedPolarBear polarBear = new SummonedPolarBear(world, entity);
        polarBear.setPos(entity.position());

        polarBear.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(getBearDamage(entity));
        polarBear.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(getBearHealth(entity));
        polarBear.setHealth(polarBear.getMaxHealth());

        world.addFreshEntity(polarBear);

        polarBear.addEffect(new EffectInstance(MobEffectRegistry.POLAR_BEAR_TIMER.get(), summonTime, 0, false, false, false));
        int effectAmplifier = 0;
        if(entity.hasEffect(MobEffectRegistry.POLAR_BEAR_TIMER.get()))
            effectAmplifier += entity.getEffect(MobEffectRegistry.POLAR_BEAR_TIMER.get()).getAmplifier() + 1;
        entity.addEffect(new EffectInstance(MobEffectRegistry.POLAR_BEAR_TIMER.get(), summonTime, effectAmplifier, false, false, true));

        super.onCast(world, entity, playerMagicData);
    }

    private float getBearHealth(LivingEntity caster) {
        return 20 + getLevel(caster) * 4;
    }

    private float getBearDamage(LivingEntity caster) {
        return getSpellPower(caster);
    }



}
