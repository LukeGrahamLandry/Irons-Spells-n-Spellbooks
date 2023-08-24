package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.network.ClientboundSyncMana;
import io.redspace.ironsspellbooks.registries.AttributeRegistry;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class InstantManaEffect extends Effect {
    public static final int manaPerAmplifier = 25;
    public static final float manaPerAmplifierPercent = .05f;

    public InstantManaEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity livingEntity, int pAmplifier, double pHealth) {
        IronsSpellbooks.LOGGER.debug("Instant mana applying effect");
        int i = pAmplifier + 1;
        int maxMana = (int) livingEntity.getAttributeValue(AttributeRegistry.MAX_MANA.get());
        int manaAdd = (int) (i * manaPerAmplifier + (maxMana * (i * manaPerAmplifierPercent)));
        PlayerMagicData pmg = PlayerMagicData.getPlayerMagicData(livingEntity);
        IronsSpellbooks.LOGGER.debug("old mana: {}", pmg.getMana());
        pmg.setMana(pmg.getMana() + manaAdd);
        if (livingEntity instanceof ServerPlayerEntity serverPlayer) {
            Messages.sendToPlayer(new ClientboundSyncMana(pmg), serverPlayer);
        }
        IronsSpellbooks.LOGGER.debug("new mana: {}", pmg.getMana());

    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int pAmplifier) {
        applyInstantenousEffect(null, null, livingEntity, pAmplifier, livingEntity.getHealth());
    }
}
