package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class BlightEffect extends Effect {
    public static final float DAMAGE_PER_LEVEL = -.05f;
    public static final float HEALING_PER_LEVEL = -.10f;

    public BlightEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @SubscribeEvent
    public static void reduceHealing(LivingHealEvent event) {
        var effect = event.getEntity().getEffect(MobEffectRegistry.BLIGHT.get());
        if (effect != null) {
            int lvl = effect.getAmplifier() + 1;
            float healingMult = 1 + HEALING_PER_LEVEL * lvl;
            float before = event.getAmount();
            event.setAmount(event.getAmount() * healingMult);
            //IronsSpellbooks.LOGGER.debug("BlightEffect.reduceHealing: {}->{}", before, event.getAmount());

        }
    }

    @SubscribeEvent
    public static void reduceDamageOutput(LivingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity livingAttacker) {
            var effect = livingAttacker.getEffect(MobEffectRegistry.BLIGHT.get());
            if (effect != null) {
                int lvl = effect.getAmplifier() + 1;
                float before = event.getAmount();
                float multiplier = 1 + BlightEffect.DAMAGE_PER_LEVEL * lvl;
                event.setAmount(event.getAmount() * multiplier);
                //IronsSpellbooks.LOGGER.debug("BlightEffect.reduceDamageOutput: {}->{}", before, event.getAmount());
            }
        }
    }
}
