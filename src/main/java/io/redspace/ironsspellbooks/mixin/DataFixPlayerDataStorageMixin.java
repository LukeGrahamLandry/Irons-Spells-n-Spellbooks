package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.datafix.IronsTagTraverser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.Tag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.storage.PlayerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(PlayerData.class)
public abstract class DataFixPlayerDataStorageMixin {
    @Shadow
    @Final
    private File playerDir;

    @Unique
    private static final Object iron_sSpells_nSpellbooks$sync = new Object();

    @Inject(method = "load", at = @At("HEAD"))
    private void load(PlayerEntity pPlayer, CallbackInfoReturnable<CompoundNBT> cir) {
        if (ServerConfigs.RUN_WORLD_UPGRADER.get()) {
            File file1 = new File(this.playerDir, pPlayer.getStringUUID() + ".dat");
            if (file1.exists() && file1.isFile()) {
                try {
                    synchronized (iron_sSpells_nSpellbooks$sync) {
                        CompoundNBT compoundTag1 = CompressedStreamTools.readCompressed(file1);

                        IronsTagTraverser ironsTraverser = new IronsTagTraverser();
                        ironsTraverser.visit(compoundTag1);

                        if (ironsTraverser.changesMade()) {
                            CompressedStreamTools.writeCompressed(compoundTag1, file1);
                            IronsSpellbooks.LOGGER.debug("DataFixPlayerDataStorageMixin: Player inventory updated: {} updates", ironsTraverser.totalChanges());
                        }
                    }
                } catch (Exception exception) {
                    IronsSpellbooks.LOGGER.debug("DataFixPlayerDataStorageMixin: Failed to load player data for {} {}", pPlayer.getName().getString(), exception.getMessage());
                }
            }
        }
    }
}
