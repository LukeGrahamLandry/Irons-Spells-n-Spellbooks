package io.redspace.ironsspellbooks.network.spell;

import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundOnCastFinished {

    private final String spellId;
    private final UUID castingEntityId;
    private final boolean cancelled;

    public ClientboundOnCastFinished(UUID castingEntityId, String spellId, boolean cancelled) {
        this.spellId = spellId;
        this.castingEntityId = castingEntityId;
        this.cancelled = cancelled;
    }

    public ClientboundOnCastFinished(PacketBuffer buf) {
        spellId = buf.readUtf();
        castingEntityId = buf.readUUID();
        cancelled = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(spellId);
        buf.writeUUID(castingEntityId);
        buf.writeBoolean(cancelled);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientSpellCastHelper.handleClientBoundOnCastFinished(castingEntityId, spellId, cancelled);
        });
        return true;
    }
}
