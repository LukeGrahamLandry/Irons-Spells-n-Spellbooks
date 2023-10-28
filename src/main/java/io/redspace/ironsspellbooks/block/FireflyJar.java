package io.redspace.ironsspellbooks.block;

import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;

public class FireflyJar extends Block {
    public FireflyJar() {
        super(AbstractBlock.Properties.copy(Blocks.GLASS).lightLevel((x) -> 8));
    }

    public static final VoxelShape SHAPE = VoxelShapes.or(Block.box(4, 0, 4, 12, 13, 12),Block.box(6, 13, 6, 10, 16, 10));

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState pState, World pLevel, BlockPos pPos, RandomSource pRandom) {
        double d0 = pPos.getX() + 0.5D;
        double d1 = pPos.getY();
        double d2 = pPos.getZ() + 0.5D;
        double d3 = pRandom.nextDouble() * 0.6D - 0.3D;
        double d4 = pRandom.nextDouble() * 0.6D;
        double d6 = pRandom.nextDouble() * 0.6D - 0.3D;

        pLevel.addParticle(ParticleHelper.FIREFLY, d0 + d3, d1 + d4, d2 + d6, 0.0D, 0.0D, 0.0D);
        pLevel.addParticle(ParticleHelper.FIREFLY, d0 + d3 * 2, d1 + d4 * 2, d2 + d6 * 2, 0.0D, 0.0D, 0.0D);

    }
}
