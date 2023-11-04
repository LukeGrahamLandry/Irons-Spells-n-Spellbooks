package io.redspace.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.effect.ChargeEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class ChargeSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "charge");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList(
                new TranslationTextComponent("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 20, 1)),
                new TranslationTextComponent("attribute.modifier.plus.1", Utils.stringTruncation(getPercentSpeed(spellLevel, caster), 0), new TranslationTextComponent("attribute.name.generic.movement_speed")),
                new TranslationTextComponent("attribute.modifier.plus.1", Utils.stringTruncation(getPercentAttackDamage(spellLevel, caster), 0), new TranslationTextComponent("attribute.name.generic.attack_damage")),
                new TranslationTextComponent("attribute.modifier.plus.1", Utils.stringTruncation(getPercentSpellPower(spellLevel, caster), 0), new TranslationTextComponent("attribute.irons_spellbooks.spell_power"))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(40)
            .build();

    public ChargeSpell() {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 8;
        this.castTime = 0;
        this.baseManaCost = 50;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
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
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {

        entity.addEffect(new EffectInstance(MobEffectRegistry.CHARGED.get(), (int) (getSpellPower(spellLevel, entity) * 20), this.getLevel(spellLevel, entity) - 1, false, false, true));

        super.onCast(level, spellLevel, entity, playerMagicData);
    }

    private float getPercentAttackDamage(int spellLevel, LivingEntity entity) {
        return getLevel(spellLevel, entity) * ChargeEffect.ATTACK_DAMAGE_PER_LEVEL * 100;
    }

    private float getPercentSpeed(int spellLevel, LivingEntity entity) {
        return getLevel(spellLevel, entity) * ChargeEffect.SPEED_PER_LEVEL * 100;
    }

    private float getPercentSpellPower(int spellLevel, LivingEntity entity) {
        return getLevel(spellLevel, entity) * ChargeEffect.SPELL_POWER_PER_LEVEL * 100;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
