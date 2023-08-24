package io.redspace.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.ExtendedLightningBolt;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class LightningBoltSpell extends AbstractSpell {
    public LightningBoltSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(caster), 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchool(SchoolType.LIGHTNING)
            .setMaxLevel(10)
            .setCooldownSeconds(25)
            .build();

    public LightningBoltSpell(int level) {
        super(SpellType.LIGHTNING_BOLT_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 2;
        this.castTime = 0;
        this.baseManaCost = 75;

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ILLUSIONER_PREPARE_BLINDNESS);
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        Vector3d pos = Utils.raycastForEntity(level, entity, 100, true).getLocation();
        LightningBoltEntity lightningBolt = new ExtendedLightningBolt(level, entity, getSpellPower(entity));
        //lightningBolt.setDamage(getSpellPower(entity));
        lightningBolt.setPos(pos);
        if (entity instanceof ServerPlayerEntity serverPlayer)
            lightningBolt.setCause(serverPlayer);
        level.addFreshEntity(lightningBolt);
        super.onCast(level, entity, playerMagicData);
    }
}
