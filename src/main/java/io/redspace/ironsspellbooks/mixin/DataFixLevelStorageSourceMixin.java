package io.redspace.ironsspellbooks.mixin;

import com.mojang.datafixers.DataFixer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.datafix.IronsTagTraverser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.Tag;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;

@Mixin(SaveFormat.class)
public abstract class DataFixLevelStorageSourceMixin {
    @Unique
    private static final Object iron_sSpells_nSpellbooks$sync = new Object();

    @Inject(method = "readLevelData", at = @At("HEAD"))
    private void readLevelData(SaveFormat.LevelDirectory pLevelDirectory, BiFunction<Path, DataFixer, Object> pLevelDatReader, CallbackInfoReturnable<Object> cir) {
        if (Files.exists(pLevelDirectory.path())) {
            Path path = pLevelDirectory.dataFile();
            try {
                synchronized (iron_sSpells_nSpellbooks$sync) {
                    CompoundNBT compoundTag1 = CompressedStreamTools.readCompressed(path.toFile());
                    CompoundNBT compoundTag2 = compoundTag1.getCompound("Data");
                    CompoundNBT compoundTag3 = compoundTag2.getCompound("Player");

                    IronsTagTraverser ironsTraverser = new IronsTagTraverser();
                    ironsTraverser.visit(compoundTag3);

                    if (ironsTraverser.changesMade()) {
                        CompressedStreamTools.writeCompressed(compoundTag1, path.toFile());
                        IronsSpellbooks.LOGGER.debug("DataFixLevelStorageSourceMixin: Single player inventory updated: {} updates", ironsTraverser.totalChanges());
                    }
                }
            } catch (Exception exception) {
                IronsSpellbooks.LOGGER.warn("DataFixLevelStorageSourceMixin failed to load {}, {}", path, exception.getMessage());
            }
        }
    }
}