package io.redspace.ironsspellbooks.effect;


import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;

public class TrueInvisibilityEffect extends Effect {
    public TrueInvisibilityEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    int lastHurtTimestamp;

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(livingEntity, pAttributeMap, pAmplifier);
        if (livingEntity instanceof PlayerEntity || livingEntity instanceof AbstractSpellCastingMob) {
            MagicData.getPlayerMagicData(livingEntity).getSyncedData().addEffects(SyncedSpellData.TRUE_INVIS);
        }

        var targetingCondition = (new EntityPredicate()).allowUnseeable().selector(e -> {
            //IronsSpellbooks.LOGGER.debug("InvisibilitySpell TargetingConditions:{}", e);
            return (((MobEntity) e).getTarget() == livingEntity);
        });

        //remove aggro from anything targeting us
        livingEntity.level.getNearbyEntities(MobEntity.class, targetingCondition, livingEntity, livingEntity.getBoundingBox().inflate(40D))
                .forEach(entityTargetingCaster -> {
                    //IronsSpellbooks.LOGGER.debug("InvisibilitySpell Clear Target From:{}", entityTargetingCaster);
                    entityTargetingCaster.setTarget(null);
                    entityTargetingCaster.setLastHurtMob(null);
                    entityTargetingCaster.setLastHurtByMob(null);
                    entityTargetingCaster.targetSelector.getAvailableGoals().forEach(PrioritizedGoal::stop);
                });
        this.lastHurtTimestamp = livingEntity.getLastHurtMobTimestamp();

    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        //If we attack, we lose invis
        if (!pLivingEntity.level.isClientSide && lastHurtTimestamp != pLivingEntity.getLastHurtMobTimestamp()){
 //Ironsspellbooks.logger.debug("TrueInvisibilityEffect.applyEffectTick: entity attacked, removing effect");
            pLivingEntity.removeEffect(this);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(livingEntity, pAttributeMap, pAmplifier);
        if (livingEntity instanceof PlayerEntity || livingEntity instanceof AbstractSpellCastingMob) {
            MagicData.getPlayerMagicData(livingEntity).getSyncedData().removeEffects(SyncedSpellData.TRUE_INVIS);
        }
    }
}
