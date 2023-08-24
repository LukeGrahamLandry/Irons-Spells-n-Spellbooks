package io.redspace.ironsspellbooks.spells.void_school;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
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

public class AbyssalShroudSpell extends AbstractSpell {
    public AbyssalShroudSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(caster) * 20, 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchool(SchoolType.VOID)
            .setMaxLevel(3)
            .setCooldownSeconds(300)
            .build();

    public AbyssalShroudSpell(int level) {
        super(SpellType.ABYSSAL_SHROUD_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 6;
        this.castTime = 0;
        this.baseManaCost = 350;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.ABYSSAL_SHROUD.get());
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        entity.addEffect(new EffectInstance(MobEffectRegistry.ABYSSAL_SHROUD.get(), (int) getSpellPower(entity) * 20, 0, false, false, true));
        super.onCast(world, entity, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return HealSpell.SELF_CAST_ANIMATION;
    }
}
