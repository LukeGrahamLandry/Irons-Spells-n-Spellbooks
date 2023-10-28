package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.entity.mobs.SummonedPolarBear;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class SummonPolarBearSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "summon_polar_bear");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                ITextComponent.translatable("ui.irons_spellbooks.hp", getBearHealth(spellLevel, null)),
                ITextComponent.translatable("ui.irons_spellbooks.damage", getBearDamage(spellLevel, null))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(180)
            .build();

    public SummonPolarBearSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 4;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 50;
    }

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
        return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.EVOKER_CAST_SPELL);
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        int summonTime = 20 * 60 * 10;

        SummonedPolarBear polarBear = new SummonedPolarBear(world, entity);
        polarBear.setPos(entity.position());

        polarBear.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(getBearDamage(spellLevel, entity));
        polarBear.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(getBearHealth(spellLevel, entity));
        polarBear.setHealth(polarBear.getMaxHealth());

        world.addFreshEntity(polarBear);

        polarBear.addEffect(new EffectInstance(MobEffectRegistry.POLAR_BEAR_TIMER.get(), summonTime, 0, false, false, false));
        int effectAmplifier = 0;
        if (entity.hasEffect(MobEffectRegistry.POLAR_BEAR_TIMER.get()))
            effectAmplifier += entity.getEffect(MobEffectRegistry.POLAR_BEAR_TIMER.get()).getAmplifier() + 1;
        entity.addEffect(new EffectInstance(MobEffectRegistry.POLAR_BEAR_TIMER.get(), summonTime, effectAmplifier, false, false, true));

        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    private float getBearHealth(int spellLevel, LivingEntity caster) {
        return 20 + getLevel(spellLevel, caster) * 4;
    }

    private float getBearDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }


}
