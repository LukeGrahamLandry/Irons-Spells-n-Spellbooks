package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

public interface AlchemistCauldronInteraction{
    @Nullable
    ItemStack interact(BlockState blockState, World level, BlockPos pos, int currentLevel, ItemStack itemStack);
}
