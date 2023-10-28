package io.redspace.ironsspellbooks.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.World;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class VisualFallingBlockRenderer extends EntityRenderer<VisualFallingBlockEntity> {
   private final BlockRendererDispatcher dispatcher;

   public VisualFallingBlockRenderer(EntityRendererProvider.Context pContext) {
      super(pContext);
      this.shadowRadius = 0.5F;
      this.dispatcher = pContext.getBlockRenderDispatcher();
   }

   public void render(VisualFallingBlockEntity entity, float pEntityYaw, float pPartialTicks, MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, int pPackedLight) {
      BlockState blockstate = entity.getBlockState();
      if (blockstate.getRenderShape() == BlockRenderType.MODEL) {
         World level = entity.getLevel();
//         if (blockstate != level.getBlockState(pEntity.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
         pMatrixStack.pushPose();
         BlockPos blockpos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
         pMatrixStack.translate(-0.5D, 0.0D, -0.5D);
         var model = this.dispatcher.getBlockModel(blockstate);
         for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(entity.getStartPos())), ModelData.EMPTY))
            this.dispatcher.getModelRenderer().tesselateBlock(level, model, blockstate, blockpos, pMatrixStack, pBuffer.getBuffer(renderType), false, RandomSource.create(), blockstate.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
         pMatrixStack.popPose();
         super.render(entity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
      }
   }

   public ResourceLocation getTextureLocation(VisualFallingBlockEntity pEntity) {
      return AtlasTexture.LOCATION_BLOCKS;
   }
}
