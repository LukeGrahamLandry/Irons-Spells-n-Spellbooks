package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.AnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncAnimation<T extends Entity & AnimatedAttacker> {
    int entityId;
    int animationId;

    public ClientboundSyncAnimation(int animationId, T entity) {
        this.entityId = entity.getId();
        this.animationId = animationId;
    }

    public ClientboundSyncAnimation(PacketBuffer buf) {
        entityId = buf.readInt();
        animationId = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(entityId);
        buf.writeInt(animationId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ClientWorld level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }
            Entity entity = level.getEntity(entityId);
            if (entity instanceof AnimatedAttacker) {
                AnimatedAttacker animatedAttacker = (AnimatedAttacker) entity;
                animatedAttacker.playAnimation(animationId);
            }
        });

        return true;
    }
}
