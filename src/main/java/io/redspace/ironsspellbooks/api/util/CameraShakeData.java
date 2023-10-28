package io.redspace.ironsspellbooks.api.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CameraShakeData {

    final int duration;
    int tickCount;
    final Vector3d origin;

    public CameraShakeData(int duration, Vector3d origin) {
        this.duration = duration;
        this.origin = origin;
    }

    public void serializeToBuffer(PacketBuffer buf) {
        buf.writeInt(duration);
        buf.writeInt(tickCount);
        buf.writeInt((int) (origin.x * 10));
        buf.writeInt((int) (origin.y * 10));
        buf.writeInt((int) (origin.z * 10));
    }

    public static CameraShakeData deserializeFromBuffer(PacketBuffer buf) {
        int duration = buf.readInt();
        int tickCount = buf.readInt();
        Vector3d origin = new Vector3d(buf.readInt() / 10f, buf.readInt() / 10f, buf.readInt() / 10f);
        CameraShakeData data = new CameraShakeData(duration, origin);
        data.tickCount = tickCount;
        return data;
    }
}
