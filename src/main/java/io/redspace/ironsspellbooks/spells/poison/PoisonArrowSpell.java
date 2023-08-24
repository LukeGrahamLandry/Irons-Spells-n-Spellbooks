package io.redspace.ironsspellbooks.spells.poison;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.poison_arrow.PoisonArrow;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.spells.ender.MagicArrowSpell;
import io.redspace.ironsspellbooks.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class PoisonArrowSpell extends AbstractSpell {

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getArrowDamage(caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.aoe_damage", Utils.stringTruncation(getAOEDamage(caster), 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.POISON)
            .setMaxLevel(10)
            .setCooldownSeconds(15)
            .build();

    public PoisonArrowSpell(int level) {
        super(SpellType.POISON_ARROW_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 40;

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.POISON_ARROW_CHARGE.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.POISON_ARROW_CAST.get());
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        PoisonArrow magicArrow = new PoisonArrow(level, entity);
        magicArrow.setPos(entity.position().add(0, entity.getEyeHeight() - magicArrow.getBoundingBox().getYsize() * .5f, 0).add(entity.getForward()));
        magicArrow.shoot(entity.getLookAngle());
        magicArrow.setDamage(getArrowDamage(entity));
        magicArrow.setAoeDamage(getAOEDamage(entity));
        level.addFreshEntity(magicArrow);
        super.onCast(level, entity, playerMagicData);
    }

    public float getArrowDamage(LivingEntity caster) {
        return getSpellPower(caster);
    }

    public float getAOEDamage(LivingEntity caster) {
        return getSpellPower(caster) * .185f;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return MagicArrowSpell.BOW_CHARGE_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return AnimationHolder.none();
    }
}
