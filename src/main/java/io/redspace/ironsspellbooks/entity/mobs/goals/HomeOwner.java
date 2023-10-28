package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public interface HomeOwner {

    @Nullable
    BlockPos getHome();

    void setHome(BlockPos homePos);

    default void serializeHome(HomeOwner self, CompoundNBT tag) {
        if (self.getHome() != null)
            tag.putIntArray("HomePos", new int[]{getHome().getX(), getHome().getY(), getHome().getZ()});
    }

    default void deserializeHome(HomeOwner self, CompoundNBT tag) {
        if (tag.contains("HomePos")) {
            int[] home = tag.getIntArray("HomePos");
            self.setHome(new BlockPos(home[0], home[1], home[2]));
        }
    }
}
