package io.redspace.ironsspellbooks.gui.inscription_table.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundInscriptionTableSelectSpell {


    private final BlockPos pos;
    private final int selectedIndex;

    public ServerboundInscriptionTableSelectSpell(BlockPos pos, int selectedIndex) {
        this.pos = pos;
        this.selectedIndex = selectedIndex;
    }

    public ServerboundInscriptionTableSelectSpell(PacketBuffer buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new BlockPos(x, y, z);
        selectedIndex = buf.readInt();

    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(selectedIndex);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are server side
//            InscriptionTableTile inscriptionTableTile = (InscriptionTableTile) ctx.getSender().level.getBlockEntity(pos);
//            if (inscriptionTableTile != null) {
//                inscriptionTableTile.setSelectedSpell(selectedIndex);
//            }

        });
        return true;
    }

}
