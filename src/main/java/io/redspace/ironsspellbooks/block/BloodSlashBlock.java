package io.redspace.ironsspellbooks.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.AbstractBlock.Properties;

public class BloodSlashBlock extends Block {

    public BloodSlashBlock() {
        super(AbstractBlock.Properties.of(Material.STONE).strength(2.5F).sound(SoundType.STONE).noOcclusion());
    }

    public BloodSlashBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(@NotNull World level, @NotNull BlockPos blockPos, @NotNull BlockState blockState, @NotNull Entity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            int duration = 200;
            int amplifier = 2;
            //player.addEffect(new MobEffectInstance(MobEffectRegistry.BLOOD_SLASHED.get(), duration, amplifier));
        }
    }
}