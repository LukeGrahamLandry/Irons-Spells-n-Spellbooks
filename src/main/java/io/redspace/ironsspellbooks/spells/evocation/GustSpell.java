package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.effect.AirborneEffect;
import io.redspace.ironsspellbooks.entity.spells.gust.GustCollider;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class GustSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "gust");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList(
                new TranslationTextComponent("ui.irons_spellbooks.strength", Utils.stringTruncation(getStrength(spellLevel, caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(AirborneEffect.getDamageFromLevel(getLevel(spellLevel, caster)), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(12)
            .build();

    public GustSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 1;
        this.castTime = 15;
        this.baseManaCost = 30;
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
        return Optional.of(SoundRegistry.GUST_CHARGE.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.GUST_CAST.get());
    }

    @Override
    public void onCast(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        float range = getRange(spellLevel, entity);
        float strength = getStrength(spellLevel, entity);

        GustCollider gust = new GustCollider(level, entity);
        gust.moveTo(entity.position().add(0, entity.getEyeHeight() * .7, 0).add(entity.getForward().normalize().scale(2f)));
        gust.range = range;
        gust.strength = strength;
        gust.amplifier = this.getLevel(spellLevel, entity) - 1;
        level.addFreshEntity(gust);
        gust.setDealDamageActive();
        gust.tick();

        super.onCast(level, spellLevel, entity, playerMagicData);
    }

    public float getRange(int spellLevel, LivingEntity caster) {
        return 8;
    }

    public float getStrength(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) * .2f;
    }

    public float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_WAVY_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.ANIMATION_LONG_CAST_FINISH;
    }

    @Override
    public boolean shouldAIStopCasting(int spellLevel, MobEntity mob, LivingEntity target) {
        return target.distanceToSqr(mob) > getRange(spellLevel, mob) * getRange(spellLevel, mob) * 1.25;
    }
}
