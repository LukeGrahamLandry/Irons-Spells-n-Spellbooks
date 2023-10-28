package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.network.ClientboundSyncCooldowns;
import io.redspace.ironsspellbooks.setup.Messages;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.AbstractCollection;
import java.util.List;
import java.util.Map;

public class PlayerCooldowns {
    public static final String LEGACY_SPELL_ID = "sid";
    public static final String SPELL_ID = "id";
    public static final String SPELL_COOLDOWN = "scd";
    public static final String COOLDOWN_REMAINING = "cdr";

    //spell type and for how many more ticks it will be on cooldown
    private final Map<String, CooldownInstance> spellCooldowns;

    //This is used to deal with the client and server tick's not in sync so
    // the client has a little grace period so it's remove doesn't happen before the server's
    private int tickBuffer = 0;

    public PlayerCooldowns() {
        this(Maps.newHashMap());
    }

    public PlayerCooldowns(Map<String, CooldownInstance> spellCooldowns) {
        this.spellCooldowns = spellCooldowns;
    }

    public void setTickBuffer(int tickBuffer) {
        this.tickBuffer = tickBuffer;
    }

    public void tick(int actualTicks) {
        List<Map.Entry<String, CooldownInstance>> spells = spellCooldowns.entrySet().stream().filter(x -> decrementCooldown(x.getValue(), actualTicks)).toList();
        spells.forEach(spell -> spellCooldowns.remove(spell.getKey()));

//        spellCooldowns.forEach((spell, cooldown) -> {
//            if (decrementCooldown(cooldown, actualTicks))
//                spellCooldowns.remove(spell);
//        });
    }

    public boolean hasCooldownsActive() {
        return !spellCooldowns.isEmpty();
    }

    public Map<String, CooldownInstance> getSpellCooldowns() {
        return spellCooldowns;
    }

    public void addCooldown(AbstractSpell spell, int durationTicks) {
        spellCooldowns.put(spell.getSpellId(), new CooldownInstance(durationTicks));
    }

    public void addCooldown(AbstractSpell spell, int durationTicks, int remaining) {
        spellCooldowns.put(spell.getSpellId(), new CooldownInstance(durationTicks, remaining));
    }

    public void addCooldown(String spellID, int durationTicks) {
        spellCooldowns.put(spellID, new CooldownInstance(durationTicks));
    }

    public void addCooldown(String spellID, int durationTicks, int remaining) {
        spellCooldowns.put(spellID, new CooldownInstance(durationTicks, remaining));
    }

    public boolean isOnCooldown(AbstractSpell spell) {
        return spellCooldowns.containsKey(spell.getSpellId());
    }

    public float getCooldownPercent(AbstractSpell spell) {
        return spellCooldowns.getOrDefault(spell.getSpellId(), new CooldownInstance(0)).getCooldownPercent();
    }

    public boolean decrementCooldown(CooldownInstance c, int amount) {
        c.decrementBy(amount);
        return c.getCooldownRemaining() <= tickBuffer;
    }

    public void syncToPlayer(ServerPlayerEntity serverPlayer) {
        Messages.sendToPlayer(new ClientboundSyncCooldowns(this.spellCooldowns), serverPlayer);
    }

    public void saveNBTData(ListNBT listTag) {
        spellCooldowns.forEach((spellId, cooldown) -> {
            if (cooldown.getCooldownRemaining() > 0) {
                CompoundNBT ct = new CompoundNBT();
                ct.putString(SPELL_ID, spellId);
                ct.putInt(SPELL_COOLDOWN, cooldown.getSpellCooldown());
                ct.putInt(COOLDOWN_REMAINING, cooldown.getCooldownRemaining());
                listTag.add(ct);
            }
        });
    }

    public void loadNBTData(ListNBT listTag) {
        if (listTag != null) {
            listTag.forEach(tag -> {
                CompoundNBT t = (CompoundNBT) tag;
                String spellId = t.getString(SPELL_ID);
                int spellCooldown = t.getInt(SPELL_COOLDOWN);
                int cooldownRemaining = t.getInt(COOLDOWN_REMAINING);
                spellCooldowns.put(spellId, new CooldownInstance(spellCooldown, cooldownRemaining));
            });
        }
    }
}
