package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerMagicProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    public static Capability<MagicData> PLAYER_MAGIC = CapabilityManager.get(new CapabilityToken<>() {
    });

    private MagicData playerMagicData = null;
    private final LazyOptional<MagicData> opt = LazyOptional.of(this::createPlayerMagicData);
    private ServerPlayerEntity serverPlayer;

    public PlayerMagicProvider(ServerPlayerEntity serverPlayer) {
        this.serverPlayer = serverPlayer;
    }

    @Nonnull
    private MagicData createPlayerMagicData() {
        if (playerMagicData == null) {
            playerMagicData = new MagicData(serverPlayer);
        }
        return playerMagicData;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == PLAYER_MAGIC) {
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        createPlayerMagicData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        createPlayerMagicData().loadNBTData(nbt);
    }
}
