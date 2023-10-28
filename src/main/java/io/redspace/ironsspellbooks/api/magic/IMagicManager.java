package io.redspace.ironsspellbooks.api.magic;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IMagicManager {
    void setPlayerCurrentMana(ServerPlayerEntity serverPlayer, int newManaValue);

    void addCooldown(ServerPlayerEntity serverPlayer, AbstractSpell spell, CastSource castSource);
}
