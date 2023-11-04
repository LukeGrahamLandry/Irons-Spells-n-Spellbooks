package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.function.Supplier;

public class ClientboundSyncCooldown {
    private final String spellId;
    private final int duration;

    public ClientboundSyncCooldown(String spellId, int duration) {
        this.spellId = spellId;
        this.duration = duration;
    }

    public ClientboundSyncCooldown(PacketBuffer buf) {
        spellId = buf.readUtf();
        duration = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(spellId);
        buf.writeInt(duration);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientMagicData.getCooldowns().addCooldown(spellId, duration);
        });
        return true;
    }
}
