package io.redspace.ironsspellbooks.network.spell;

import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundAborptionParticles {

    private Vector3d pos;

    public ClientboundAborptionParticles(Vector3d pos) {
        this.pos = pos;
    }

    public ClientboundAborptionParticles(PacketBuffer buf) {
        pos = readVec3(buf);
    }

    public void toBytes(PacketBuffer buf) {
        writeVec3(pos, buf);
    }

    public Vector3d readVec3(PacketBuffer buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vector3d(x, y, z);
    }

    public void writeVec3(Vector3d vec3, PacketBuffer buf) {
        buf.writeDouble(vec3.x);
        buf.writeDouble(vec3.y);
        buf.writeDouble(vec3.z);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientSpellCastHelper.handleClientsideAbsorptionParticles(pos);
        });
        return true;
    }
}
