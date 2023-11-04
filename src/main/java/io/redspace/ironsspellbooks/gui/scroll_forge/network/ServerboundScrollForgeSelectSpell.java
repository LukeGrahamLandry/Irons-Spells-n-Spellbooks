package io.redspace.ironsspellbooks.gui.scroll_forge.network;

import io.redspace.ironsspellbooks.block.scroll_forge.ScrollForgeTile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundScrollForgeSelectSpell {


    private final BlockPos pos;
    private final String spellId;

    public ServerboundScrollForgeSelectSpell(BlockPos pos, String spellId) {
        this.pos = pos;
        this.spellId = spellId;
    }

    public ServerboundScrollForgeSelectSpell(PacketBuffer buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new BlockPos(x, y, z);
        spellId = buf.readUtf();

    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeUtf(spellId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are server side
            ScrollForgeTile scrollForgeTile = (ScrollForgeTile) ctx.getSender().level.getBlockEntity(pos);
            if (scrollForgeTile != null) {
                scrollForgeTile.setRecipeSpell(spellId);
            }
        });
        return true;
    }

}
