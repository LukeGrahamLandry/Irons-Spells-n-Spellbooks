package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.capabilities.magic.CooldownInstance;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ClientboundSyncCooldowns {
    private final Map<String, CooldownInstance> spellCooldowns;

    public static String readSpellID(PacketBuffer buffer) {
        return buffer.readUtf();
    }

    public static CooldownInstance readCoolDownInstance(PacketBuffer buffer) {
        int spellCooldown = buffer.readInt();
        int spellCooldownRemaining = buffer.readInt();
        return new CooldownInstance(spellCooldown, spellCooldownRemaining);
    }

    public static void writeSpellId(PacketBuffer buf, String spellId) {
        buf.writeUtf(spellId);
    }

    public static void writeCoolDownInstance(PacketBuffer buf, CooldownInstance cooldownInstance) {
        buf.writeInt(cooldownInstance.getSpellCooldown());
        buf.writeInt(cooldownInstance.getCooldownRemaining());
    }

    public ClientboundSyncCooldowns(Map<String, CooldownInstance> spellCooldowns) {
        this.spellCooldowns = spellCooldowns;
    }

    public ClientboundSyncCooldowns(PacketBuffer buf) {
        this.spellCooldowns = buf.readMap(ClientboundSyncCooldowns::readSpellID, ClientboundSyncCooldowns::readCoolDownInstance);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeMap(spellCooldowns, ClientboundSyncCooldowns::writeSpellId, ClientboundSyncCooldowns::writeCoolDownInstance);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            this.spellCooldowns.forEach((k, v) -> {
                //irons_spellbooks.LOGGER.debug("ClientboundSyncCooldowns {} {} {}", k, v.getSpellCooldown(), v.getCooldownRemaining());
                ClientMagicData.getCooldowns().addCooldown(k, v.getSpellCooldown(), v.getCooldownRemaining());
            });
            ClientMagicData.resetClientCastState(null);

        });
        return true;
    }
}