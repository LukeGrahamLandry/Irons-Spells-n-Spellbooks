package io.redspace.ironsspellbooks.spells.poison;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Log;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class RootSpell extends AbstractSpell {
    public RootSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getDuration(caster), 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.POISON)
            .setMaxLevel(10)
            .setCooldownSeconds(35)
            .build();

    public RootSpell(int level) {
        super(SpellType.ROOT_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 3;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 40;
        this.baseManaCost = 45;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_ATTACK);
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
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
//        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData targetData) {
//            var targetEntity = targetData.getTarget((ServerLevel) level);
//            if (targetEntity != null) {
//                //targetEntity.addEffect(new MobEffectInstance(MobEffectRegistry.ROOT.get(), getDuration(entity), getAmplifier(entity)));
//                IronsSpellbooks.LOGGER.debug("RootSpell.onCast targetEntity:{}", targetEntity);
//                RootEntity rootEntity = new RootEntity(level, entity, getDuration(entity));
//                rootEntity.setTarget(targetEntity);
//                rootEntity.moveTo(targetEntity.getPosition(2));
//                level.addFreshEntity(rootEntity);
//                targetEntity.stopRiding();
//                targetEntity.startRiding(rootEntity, true);
//            }
//
//        }

        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData castTargetingData) {
            LivingEntity target = castTargetingData.getTarget((ServerWorld) level);

            if (Log.SPELL_DEBUG) {
                IronsSpellbooks.LOGGER.debug("RootSpell.onCast.1 targetEntity:{}", target);
            }

            if (target != null && !target.getType().is(ModTags.CANT_ROOT)) {
                if (Log.SPELL_DEBUG) {
                    IronsSpellbooks.LOGGER.debug("RootSpell.onCast.2 targetEntity:{}", target);
                }
                Vector3d spawn = target.position();
                RootEntity rootEntity = new RootEntity(level, entity);
                rootEntity.setDuration(getDuration(entity));
                rootEntity.setTarget(target);
                rootEntity.moveTo(spawn);
                level.addFreshEntity(rootEntity);
                target.stopRiding();
                target.startRiding(rootEntity, true);
            }
        }

        super.onCast(level, entity, playerMagicData);
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

    public int getDuration(LivingEntity caster) {
        return (int) (getSpellPower(caster) * 20);
    }

}
