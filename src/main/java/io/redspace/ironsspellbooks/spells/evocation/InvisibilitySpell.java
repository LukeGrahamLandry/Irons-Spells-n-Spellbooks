package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class InvisibilitySpell extends AbstractSpell {
    public InvisibilitySpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(caster) * 20, 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchool(SchoolType.EVOCATION)
            .setMaxLevel(6)
            .setCooldownSeconds(45)
            .build();

    public InvisibilitySpell(int level) {
        super(SpellType.INVISIBILITY_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 8;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 5;
        this.castTime = 40;
        this.baseManaCost = 35;

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ILLUSIONER_PREPARE_MIRROR);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {

        entity.addEffect(new EffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), getDuration(entity), 0, false, false, true));

        var targetingCondition = EntityPredicate.forCombat().selector(e -> {
            //IronsSpellbooks.LOGGER.debug("InvisibilitySpell TargetingConditions:{}", e);
            return (((MobEntity) e).getTarget() == entity);
        });

        world.getNearbyEntities(MobEntity.class, targetingCondition, entity, entity.getBoundingBox().inflate(40D))
                .forEach(entityTargetingCaster -> {
                    //IronsSpellbooks.LOGGER.debug("InvisibilitySpell Clear Target From:{}", entityTargetingCaster);
                    entityTargetingCaster.setTarget(null);
                    entityTargetingCaster.setLastHurtMob(null);
                    entityTargetingCaster.setLastHurtByMob(null);
                    entityTargetingCaster.targetSelector.getAvailableGoals().forEach(PrioritizedGoal::stop);
                });

        super.onCast(world, entity, playerMagicData);
    }

    private int getDuration(LivingEntity source) {
        return (int) (getSpellPower(source) * 20);
    }

}
