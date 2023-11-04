package io.redspace.ironsspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.effect.SummonTimer;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.Util;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface MagicSummon extends AntiMagicSusceptible {

    LivingEntity getSummoner();

    void onUnSummon();

    @Override
    default void onAntiMagic(MagicData playerMagicData) {
        onUnSummon();
    }

    default boolean shouldIgnoreDamage(DamageSource damageSource) {
        if (!damageSource.isBypassInvul()) {
            if (damageSource instanceof EntityDamageSource && !ServerConfigs.CAN_ATTACK_OWN_SUMMONS.get())
                return !(getSummoner() == null || damageSource.getEntity() == null || (!damageSource.getEntity().equals(getSummoner()) && !getSummoner().isAlliedTo(damageSource.getEntity())));
        }
        return false;
    }

    default boolean isAlliedHelper(Entity entity) {
        if (getSummoner() == null)
            return false;
        boolean isFellowSummon = entity == getSummoner() || entity.isAlliedTo(getSummoner());
        boolean hasCommonOwner = entity instanceof TameableEntity && ((TameableEntity) entity).getOwner() == getSummoner();
        return isFellowSummon || hasCommonOwner;
    }

    default void onDeathHelper() {
        if (this instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) this;
            World level = entity.level;
            ITextComponent deathMessage = entity.getCombatTracker().getDeathMessage();

            if (!level.isClientSide && level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && getSummoner() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) getSummoner();
                player.sendMessage(deathMessage, Util.NIL_UUID);
            }
        }
    }

    default void onRemovedHelper(Entity entity, SummonTimer timer) {
        /*
        Decreases player's summon timer amplifier to keep track of how many of their summons remain.
        */
        var reason = entity.getRemovalReason();
        if (reason != null && getSummoner() instanceof ServerPlayerEntity && reason.shouldDestroy()) {
            ServerPlayerEntity player = (ServerPlayerEntity) getSummoner();

            EffectInstance effect = player.getEffect(timer);
            if (effect != null) {
                EffectInstance decrement = new EffectInstance(timer, effect.getDuration(), effect.getAmplifier() - 1, false, false, true);
                if (decrement.getAmplifier() >= 0) {
                    player.getActiveEffectsMap().put(timer, decrement);
                    player.connection.send(new SPlayEntityEffectPacket(player.getId(), decrement));
                } else {
                    player.removeEffect(timer);
                }
            }
            if (reason.equals(Entity.RemovalReason.DISCARDED))
                player.sendSystemMessage(new TranslationTextComponent("ui.irons_spellbooks.summon_despawn_message", ((Entity) this).getDisplayName()));

        }
    }
}
