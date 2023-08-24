package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class AngelWingsSpell extends AbstractSpell {
    public AngelWingsSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(ITextComponent.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(caster) * 20, 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchool(SchoolType.HOLY)
            .setMaxLevel(5)
            .setCooldownSeconds(120)
            .build();

    public AngelWingsSpell(int level) {
        super(SpellType.ANGEL_WING_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 30;
        this.castTime = 0;
        this.baseManaCost = 60;

    }

    private int getEffectDuration(LivingEntity entity) {
        return (int) getSpellPower(entity) * 20;
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
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        entity.addEffect(new EffectInstance(MobEffectRegistry.ANGEL_WINGS.get(), getEffectDuration(entity)), entity);
        super.onCast(world, entity, playerMagicData);
    }
}
