package io.redspace.ironsspellbooks.api.spells;

import net.minecraft.network.PacketBuffer;

public interface ICastDataSerializable extends ICastData {

    void writeToStream(PacketBuffer buffer);

    void readFromStream(PacketBuffer buffer);
}
