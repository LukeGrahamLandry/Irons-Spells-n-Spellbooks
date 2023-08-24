package io.redspace.ironsspellbooks.capabilities.magic;

import net.minecraft.network.PacketBuffer;

public interface CastDataSerializable extends CastData {

    void writeToStream(PacketBuffer buffer);

    void readFromStream(PacketBuffer buffer);
}
