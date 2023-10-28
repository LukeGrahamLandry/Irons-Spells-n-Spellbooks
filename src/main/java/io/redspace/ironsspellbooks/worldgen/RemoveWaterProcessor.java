package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.registries.StructureProcessorRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.fluid.FluidState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class RemoveWaterProcessor extends StructureProcessor {

    public static final Codec<RemoveWaterProcessor> CODEC = Codec.unit(RemoveWaterProcessor::new);

    public RemoveWaterProcessor() {

    }

    @Nullable
    @Override
    public Template.BlockInfo process(@Nonnull IWorldReader level, @Nonnull BlockPos jigsawPiecePos, @Nonnull BlockPos jigsawPieceBottomCenterPos, @Nonnull Template.BlockInfo blockInfoLocal, @Nonnull Template.BlockInfo blockInfoGlobal, @Nonnull PlacementSettings settings, @Nullable Template template) {
        if (blockInfoGlobal.state.hasProperty(BlockStateProperties.WATERLOGGED) && !blockInfoGlobal.state.getValue(BlockStateProperties.WATERLOGGED)) {
            ChunkPos chunkPos = new ChunkPos(blockInfoGlobal.pos);
            IChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
            int sectionIndex = chunk.getSectionIndex(blockInfoGlobal.pos.getY());

            // if section index is < 0 we are out of bounds
            if (sectionIndex >= 0) {
                ChunkSection section = chunk.getSection(sectionIndex);
                // if we are waterlogged, reset us to our original state
                if (this.getFluidState(section, blockInfoGlobal.pos).is(FluidTags.WATER)) {
                    this.setBlock(section, blockInfoGlobal.pos, blockInfoGlobal.state);
                }
            }
        }

        return blockInfoGlobal;
    }

    private void setBlock(ChunkSection section, BlockPos pos, BlockState state) {
        section.setBlockState(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()), state);
    }

    private FluidState getFluidState(ChunkSection section, BlockPos pos) {
        return section.getFluidState(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()));
    }

    @Nonnull
    @Override
    protected IStructureProcessorType<?> getType() {
        return StructureProcessorRegistry.REMOVE_WATER.get();
    }
}