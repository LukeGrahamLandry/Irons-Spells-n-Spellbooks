package io.redspace.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.effect.ChargeEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.spells.holy.HealSpell;
import io.redspace.ironsspellbooks.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;


public class ChargeSpell extends AbstractSpell {
    public ChargeSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(caster) * 20, 1)),
                new TranslationTextComponent("attribute.modifier.plus.1", Utils.stringTruncation(getPercentSpeed(caster), 0), new TranslationTextComponent("attribute.name.generic.movement_speed")),
                new TranslationTextComponent("attribute.modifier.plus.1", Utils.stringTruncation(getPercentAttackDamage(caster), 0), new TranslationTextComponent("attribute.name.generic.attack_damage")),
                new TranslationTextComponent("attribute.modifier.plus.1", Utils.stringTruncation(getPercentSpellPower(caster), 0), new TranslationTextComponent("attribute.irons_spellbooks.spell_power"))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchool(SchoolType.LIGHTNING)
            .setMaxLevel(3)
            .setCooldownSeconds(40)
            .build();

    public ChargeSpell(int level) {
        super(SpellType.CHARGE_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 8;
        this.castTime = 0;
        this.baseManaCost = 50;

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
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {

        entity.addEffect(new EffectInstance(MobEffectRegistry.CHARGED.get(), (int) (getSpellPower(entity) * 20), this.getLevel(entity) - 1, false, false, true));

        super.onCast(level, entity, playerMagicData);
    }

    private float getPercentAttackDamage(LivingEntity entity) {
        return getLevel(entity) * ChargeEffect.ATTACK_DAMAGE_PER_LEVEL * 100;
    }

    private float getPercentSpeed(LivingEntity entity) {
        return getLevel(entity) * ChargeEffect.SPEED_PER_LEVEL * 100;
    }

    private float getPercentSpellPower(LivingEntity entity) {
        return getLevel(entity) * ChargeEffect.SPELL_POWER_PER_LEVEL * 100;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return HealSpell.SELF_CAST_ANIMATION;
    }
}
