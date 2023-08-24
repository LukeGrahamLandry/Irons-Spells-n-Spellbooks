package io.redspace.ironsspellbooks.spells.poison;

import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.effect.BlightEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;


public class BlightSpell extends AbstractSpell {
    public BlightSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.reduced_healing",Utils.stringTruncation((1 + getAmplifier(caster)) * BlightEffect.HEALING_PER_LEVEL * -100, 1)),
                new TranslationTextComponent("ui.irons_spellbooks.reduced_damage", Utils.stringTruncation((1 + getAmplifier(caster)) * BlightEffect.DAMAGE_PER_LEVEL * -100, 1)),
                new TranslationTextComponent("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getDuration(caster), 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchool(SchoolType.POISON)
            .setMaxLevel(8)
            .setCooldownSeconds(35)
            .build();

    public BlightSpell(int level) {
        super(SpellType.BLIGHT_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 50;
        this.baseManaCost = 10;

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.BLIGHT_BEGIN.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }


    @Override
    public boolean checkPreCastConditions(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, getSpellType(), 32, .35f);
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData targetData) {
            var targetEntity = targetData.getTarget((ServerWorld) world);
            if (targetEntity != null) {
                targetEntity.addEffect(new EffectInstance(MobEffectRegistry.BLIGHT.get(), getDuration(entity), getAmplifier(entity)));
            }
        }

        super.onCast(world, entity, playerMagicData);
    }

    public int getAmplifier(LivingEntity caster) {
        return (int) (getSpellPower(caster) * this.getLevel(caster) - 1);
    }

    public int getDuration(LivingEntity caster) {
        return (int) (getSpellPower(caster) * 20 * 30);
    }

}
