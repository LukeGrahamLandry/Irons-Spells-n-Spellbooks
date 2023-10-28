package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.world.level.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;

public class AlchemistCauldronBlock extends ContainerBlock {
    public AlchemistCauldronBlock() {
        super(Properties.copy(Blocks.CAULDRON).lightLevel((blockState) -> 3));
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(LEVEL, 0));

    }

    //    private static final VoxelShape INSIDE = box(2, 4, 2, 14, 16, 14);
//    private static final VoxelShape BODY = box(0, 2, 0, 16, 16, 16);
//    private static final VoxelShape RIM_NEGATIVE = box(0, 12, 0, 16, 14, 16);
//    private static final VoxelShape RIM_INNER = box(1, 12, 1, 15, 14, 15);
//    private static final VoxelShape LOGS = Shapes.or(box(0, 0, 4, 16, 2, 6), box(0, 0, 10, 16, 2, 12), box(4, 0, 0, 6, 2, 16), box(10, 0, 0, 12, 2, 16));
//    private static final VoxelShape DETAILED_BODY = Shapes.join(Shapes.or(Shapes.join(BODY, RIM_NEGATIVE, BooleanOp.ONLY_FIRST), RIM_INNER), INSIDE, BooleanOp.ONLY_FIRST);
//    private static final VoxelShape SHAPE = Shapes.or(LOGS, DETAILED_BODY);
    //magic shape. see comment above for legible shape
    private static final VoxelShape SHAPE = VoxelShapes.or(VoxelShapes.or(box(0, 0, 4, 16, 2, 6), box(0, 0, 10, 16, 2, 12), box(4, 0, 0, 6, 2, 16), box(10, 0, 0, 12, 2, 16)), VoxelShapes.join(VoxelShapes.or(VoxelShapes.join(box(0, 2, 0, 16, 16, 16), box(0, 12, 0, 16, 14, 16), IBooleanFunction.ONLY_FIRST), box(1, 12, 1, 15, 14, 15)), box(2, 4, 2, 14, 16, 14), IBooleanFunction.ONLY_FIRST));

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final int MAX_LEVELS = 4;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, MAX_LEVELS);

    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World pLevel, BlockState pState, TileEntityType<T> pBlockEntityType) {
        return createTicker(pLevel, pBlockEntityType, BlockRegistry.ALCHEMIST_CAULDRON_TILE.get());
    }

    @javax.annotation.Nullable
    protected static <T extends TileEntity> BlockEntityTicker<T> createTicker(World pLevel, TileEntityType<T> pServerType, TileEntityType<? extends AlchemistCauldronTile> pClientType) {
        return pLevel.isClientSide ? null : createTickerHelper(pServerType, pClientType, AlchemistCauldronTile::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT, LEVEL);
    }

    public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return SHAPE;
    }

    @Override
    public ActionResultType use(BlockState blockState, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockHit) {
        if (level.getBlockEntity(pos) instanceof AlchemistCauldronTile) {
            AlchemistCauldronTile tile = (AlchemistCauldronTile) level.getBlockEntity(pos);
            return tile.handleUse(blockState, level, pos, player, hand);
        }
        return super.use(blockState, level, pos, player, hand, blockHit);
    }

    @Override
    public void entityInside(BlockState blockState, World level, BlockPos pos, Entity entity) {
        if (entity.tickCount % 20 == 0) {
            if (isBoiling(blockState)) {
                if (entity instanceof LivingEntity && ((LivingEntity) entity).hurt(DamageSources.CAULDRON, 2)) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    MagicManager.spawnParticles(level, ParticleHelper.BLOOD, entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), 20, .05, .05, .05, .1, false);
                    if (level.getBlockEntity(pos) instanceof AlchemistCauldronTile) {
                        AlchemistCauldronTile cauldronTile = (AlchemistCauldronTile) level.getBlockEntity(pos);
                        AlchemistCauldronTile.appendItem(cauldronTile.resultItems, new ItemStack(ItemRegistry.BLOOD_VIAL.get()));
                        cauldronTile.setChanged();
                    }
                }
            }
        }

        super.entityInside(blockState, level, pos, entity);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemistCauldronTile(pos, state);
    }

    @Override
    @SuppressWarnings({"all"})
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborstate, IWorld level, BlockPos pos, BlockPos pNeighborPos) {
        if (direction.equals(Direction.DOWN)) {
            level.setBlock(pos, state.setValue(LIT, isFireSource(neighborstate)), 11);
        }
        return super.updateShape(state, direction, neighborstate, level, pos, pNeighborPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        IWorld levelaccessor = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos().below();
        boolean flag = isFireSource(levelaccessor.getBlockState(blockpos));
        return this.defaultBlockState().setValue(LIT, flag);
    }

    @Override
    public void onRemove(BlockState pState, World pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            TileEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof AlchemistCauldronTile) {
                AlchemistCauldronTile cauldronTile = (AlchemistCauldronTile) blockEntity;
                cauldronTile.drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull BlockRenderType getRenderShape(@NotNull BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    public boolean isFireSource(BlockState blockState) {
        //TODO: its a magic cauldron. why does it need a fire source?
        return true;//CampfireBlock.isLitCampfire(blockState);
    }

    public static boolean isLit(BlockState blockState) {
        return blockState.hasProperty(LIT) && blockState.getValue(LIT);
    }

    public static int getLevel(BlockState blockState) {
        return blockState.hasProperty(LEVEL) ? blockState.getValue(LEVEL) : 0;
    }

    public static boolean isBoiling(BlockState blockState) {
        return isLit(blockState) && getLevel(blockState) > 0;
    }

}
