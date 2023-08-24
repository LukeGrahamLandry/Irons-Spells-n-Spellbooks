package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;

public class SummonTimer extends Effect {
    public SummonTimer(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        //Ironsspellbooks.logger.debug("Summoner Timer Wore out on {}", pLivingEntity.getName().getString());
        if(pLivingEntity instanceof MagicSummon summon)
            summon.onUnSummon();
    }

}
