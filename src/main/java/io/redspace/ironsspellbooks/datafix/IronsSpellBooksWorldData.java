package io.redspace.ironsspellbooks.datafix;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;
import org.jetbrains.annotations.NotNull;

public class IronsSpellBooksWorldData extends WorldSavedData {
    private int dataVersion;

    public IronsSpellBooksWorldData() {
        dataVersion = 0;
    }

    public IronsSpellBooksWorldData(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
        this.setDirty();
    }

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT pCompoundTag) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("dataVersion", dataVersion);
        return tag;
    }

    public static IronsSpellBooksWorldData load(CompoundNBT tag) {
        int dataVersion = tag.getInt("dataVersion");
        return new IronsSpellBooksWorldData(dataVersion);
    }
}