package io.redspace.ironsspellbooks.gui.overlays.network;

import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSetSpellBookActiveIndex {
    private final int selectedIndex;

    public ServerboundSetSpellBookActiveIndex(int selectedIndex) {
        //convert objects into bytes then re-read them into objects
        this.selectedIndex = selectedIndex;
    }

    public ServerboundSetSpellBookActiveIndex(PacketBuffer buf) {
        selectedIndex = buf.readInt();

    }

    public void toBytes(PacketBuffer buf) {

        buf.writeInt(selectedIndex);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {

        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are server side
            ServerPlayerEntity serverPlayer = ctx.getSender();
            if (serverPlayer != null) {
                //This could be simplified by passing in a hand too
                var mainHandStack = serverPlayer.getMainHandItem();
                var offHandStack = serverPlayer.getOffhandItem();

                if (mainHandStack.getItem() instanceof SpellBook) {
                    SpellBookData.getSpellBookData(mainHandStack).setActiveSpellIndex(selectedIndex, mainHandStack);
                } else if (offHandStack.getItem() instanceof SpellBook) {
                    SpellBookData.getSpellBookData(offHandStack).setActiveSpellIndex(selectedIndex, offHandStack);
                }
            }
        });
        return true;
    }
}
