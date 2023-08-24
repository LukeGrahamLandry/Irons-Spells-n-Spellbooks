package io.redspace.ironsspellbooks.block;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.level.block.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.DirectionProperty;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public class ArmorPileBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    private static final VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    public ArmorPileBlock() {
        super(AbstractBlock.Properties.of(Material.METAL, MaterialColor.NONE).strength(5.0F, 8.0F).sound(SoundType.CHAIN).noOcclusion().requiresCorrectToolForDrops());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return BASE;
    }

    /* FACING */

    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void destroy(IWorld pLevel, BlockPos pPos, BlockState pState) {
        super.destroy(pLevel, pPos, pState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void spawnAfterBreak(BlockState pState, ServerWorld level, BlockPos pos, ItemStack pStack, boolean pDropExperience) {
        super.spawnAfterBreak(pState, level, pos, pStack, pDropExperience);
        KeeperEntity keeper = new KeeperEntity(level);
        keeper.moveTo(Vector3d.atCenterOf(pos));
        keeper.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), SpawnReason.TRIGGERED, null, null);
        level.addFreshEntity(keeper);

        MagicManager.spawnParticles(level, ParticleTypes.SOUL, pos.getX(), pos.getY(), pos.getZ(), 20, .1, .1, .1, .05, false);
        level.playSound(null, pos, SoundEvents.SOUL_ESCAPE, SoundCategory.BLOCKS, 1, 1);

    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState blockState) {
        return BlockRenderType.MODEL;
    }
}
