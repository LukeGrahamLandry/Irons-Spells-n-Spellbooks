package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;


public class HeartstopEffect extends Effect {
    private int duration;

    public HeartstopEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        PlayerMagicData.getPlayerMagicData(pLivingEntity).getSyncedData().addEffects(SyncedSpellData.HEARTSTOP);
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeModifierManager pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        var playerMagicData = PlayerMagicData.getPlayerMagicData(pLivingEntity);
        playerMagicData.getSyncedData().removeEffects(SyncedSpellData.HEARTSTOP);

        //Whether or not player has spawn immunity (we want to damage them regardless)
        if (pLivingEntity.tickCount > 60) {
            pLivingEntity.hurt(DamageSources.HEARTSTOP, playerMagicData.getSyncedData().getHeartstopAccumulatedDamage());
            //irons_spellbooks.LOGGER.debug("{} had no spawn immunity", pLivingEntity.getName().getString());

        } else {
            //TODO: find a better way to apply damage
            pLivingEntity.kill();
//                serverPlayer.setHealth(serverPlayer.getHealth() - playerMagicData.getSyncedData().getHeartstopAccumulatedDamage());

            //irons_spellbooks.LOGGER.debug("{} had spawn immunity", pLivingEntity.getName().getString());

        }
        playerMagicData.getSyncedData().setHeartstopAccumulatedDamage(0);

    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        //irons_spellbooks.LOGGER.debug("{} ticks existed: {}", pLivingEntity.getName().getString(), pLivingEntity.tickCount);

        //Heart beats once every 2 seconds at 0% damage, and 2 times per second at 100% damage (relative to health)
        if (pLivingEntity.level.isClientSide) {
            if (pLivingEntity instanceof PlayerEntity player) {
                float damage = ClientMagicData.getSyncedSpellData(player).getHeartstopAccumulatedDamage();
                float f = 1 - MathHelper.clamp(damage / player.getHealth(), 0, 1);
                int i = (int) (10 + (40 - 10) * f);
 //Ironsspellbooks.logger.debug("{} ({}/{} = {})", i, damage, player.getHealth(), f);
                if (this.duration % Math.max(i, 1) == 0) {
                    player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1, 0.85f);
                }
            }
        }

    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        this.duration = pDuration;
        return true;
    }
}
