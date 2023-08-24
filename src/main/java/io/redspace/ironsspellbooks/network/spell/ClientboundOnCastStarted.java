package io.redspace.ironsspellbooks.network.spell;

import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundOnCastStarted {

    private SpellType spellType;
    private UUID castingEntityId;

    public ClientboundOnCastStarted(UUID castingEntityId, SpellType spellType) {
        this.spellType = spellType;
        this.castingEntityId = castingEntityId;
    }

    public ClientboundOnCastStarted(PacketBuffer buf) {
        spellType = buf.readEnum(SpellType.class);
        castingEntityId = buf.readUUID();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeEnum(spellType);
        buf.writeUUID(castingEntityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientSpellCastHelper.handleClientBoundOnCastStarted(castingEntityId, spellType);
        });
        return true;
    }
}
