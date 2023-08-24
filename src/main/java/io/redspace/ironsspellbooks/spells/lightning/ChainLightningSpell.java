package io.redspace.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.ChainLightning;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;


public class ChainLightningSpell extends AbstractSpell {
    public ChainLightningSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                ITextComponent.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 1)),
                ITextComponent.translatable("ui.irons_spellbooks.max_victims", getMaxConnections(caster)),
                ITextComponent.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getRange(caster), 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.LIGHTNING)
            .setMaxLevel(10)
            .setCooldownSeconds(20)
            .build();

    public ChainLightningSpell(int level) {
        super(SpellType.CHAIN_LIGHTNING_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 7;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 25;

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
    public boolean checkPreCastConditions(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, getSpellType(), 32, .35f);
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData targetData) {
            var targetEntity = targetData.getTarget((ServerWorld) world);
            if (targetEntity != null) {
                ChainLightning chainLightning = new ChainLightning(world, entity, targetEntity);
                chainLightning.setDamage(getDamage(entity));
                chainLightning.range = getRange(entity);
                chainLightning.maxConnections = getMaxConnections(entity);
                world.addFreshEntity(chainLightning);
            }
        }

        super.onCast(world, entity, playerMagicData);
    }

    public float getDamage(LivingEntity caster) {
        return getSpellPower(caster);
    }

    public int getMaxConnections(LivingEntity caster) {
        return 3 + getLevel(caster);
    }

    public float getRange(LivingEntity caster) {
        return 1f + getSpellPower(caster) * .5f;
    }

}
