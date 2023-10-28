package io.redspace.ironsspellbooks.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class OwnerHelper {

    public static LivingEntity getAndCacheOwner(World level, LivingEntity cachedOwner, UUID summonerUUID) {
        if (cachedOwner != null && cachedOwner.isAlive()) {
            return cachedOwner;
        } else if (summonerUUID != null && level instanceof ServerWorld serverLevel) {
            if (serverLevel.getEntity(summonerUUID) instanceof LivingEntity livingEntity)
                cachedOwner = livingEntity;
            return cachedOwner;
        } else {
            return null;
        }
    }

    public static void serializeOwner(CompoundNBT compoundTag, UUID ownerUUID) {
        if (ownerUUID != null) {
            compoundTag.putUUID("Summoner", ownerUUID);
        }
    }

    public static UUID deserializeOwner(CompoundNBT compoundTag) {
        if (compoundTag.hasUUID("Summoner")) {
            return compoundTag.getUUID("Summoner");
        }
        return null;
    }

}
