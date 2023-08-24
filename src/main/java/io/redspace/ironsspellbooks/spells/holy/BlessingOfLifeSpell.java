package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.network.spell.ClientboundHealParticles;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class BlessingOfLifeSpell extends AbstractSpell {
    public BlessingOfLifeSpell() {
        this(1);
    }
    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.healing", Utils.stringTruncation(getSpellPower(caster), 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.HOLY)
            .setMaxLevel(10)
            .setCooldownSeconds(10)
            .build();

    public BlessingOfLifeSpell(int level) {
        super(SpellType.BLESSING_OF_LIFE_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 4;
        this.spellPowerPerLevel = 1;
        this.castTime = 30;
        this.baseManaCost = 10;

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
        return Utils.preCastTargetHelper(level, entity, playerMagicData, getSpellType(), 64, .35f);
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData healTargetingData) {
            var targetEntity = healTargetingData.getTarget((ServerWorld) world);
            if (targetEntity != null) {
                targetEntity.heal(getSpellPower(entity));
                Messages.sendToPlayersTrackingEntity(new ClientboundHealParticles(targetEntity.position()), targetEntity, true);
            }
        }

        super.onCast(world, entity, playerMagicData);
    }

    @Nullable
    private LivingEntity findTarget(LivingEntity caster) {
        var target = Utils.raycastForEntity(caster.level, caster, 32, true, 0.35f);
        if (target instanceof EntityRayTraceResult entityHit && entityHit.getEntity() instanceof LivingEntity livingTarget) {
            return livingTarget;
        } else {
            return null;
        }
    }

}
