package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundQuickCast {

    private int slot;
    private Hand hand;

    public ServerboundQuickCast(int slot, Hand hand) {
        this.slot = slot;
        this.hand = hand;
    }

    public ServerboundQuickCast(PacketBuffer buf) {
        slot = buf.readInt();
        hand = buf.readEnum(Hand.class);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(slot);
        buf.writeEnum(hand);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.getSender();
            ItemStack itemStack = serverPlayer.getItemInHand(hand);
            SpellBookData sbd = SpellBookData.getSpellBookData(itemStack);
            if (sbd.getSpellSlots() > 0) {
                SpellData spellData = sbd.getSpell(slot);
                if (spellData != null) {
                    MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
                    if (playerMagicData.isCasting() && !playerMagicData.getCastingSpellId().equals(spellData.getSpell().getSpellId())) {
                        ServerboundCancelCast.cancelCast(serverPlayer, playerMagicData.getCastType() != CastType.LONG);
                    }
                    spellData.getSpell().attemptInitiateCast(itemStack, spellData.getLevel(), serverPlayer.level, serverPlayer, CastSource.SPELLBOOK, true);
                }
            }
        });
        return true;
    }
}
