package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.util.math.vector.Vector3d;

public class AscensionEffect extends Effect {

    public AscensionEffect(EffectType mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(livingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(livingEntity).getSyncedData().removeEffects(SyncedSpellData.ASCENSION);
        livingEntity.resetFallDistance();
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(pLivingEntity).getSyncedData().addEffects(SyncedSpellData.ASCENSION);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.resetFallDistance();
        //IronsSpellbooks.LOGGER.debug("Ascension Tick");
    }

    public static void ambientParticles(ClientWorld level, LivingEntity entity) {
        var random = entity.getRandom();
        for (int i = 0; i < 2; i++) {
            Vector3d motion = new Vector3d(
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1
            );
            motion = motion.scale(.04f);
            level.addParticle(ParticleHelper.ELECTRICITY, entity.getRandomX(.4f), entity.getRandomY(), entity.getRandomZ(.4f), motion.x, motion.y, motion.z);
        }
    }
}
