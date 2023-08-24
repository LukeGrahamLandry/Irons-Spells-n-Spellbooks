package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Function;


public class AlchemistCauldronRenderer implements TileEntityRenderer<AlchemistCauldronTile> {
    ItemRenderer itemRenderer;

    public AlchemistCauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    private static final Vector3d ITEM_POS = new Vector3d(.5, 1.5, .5);
    @Override
    public void render(AlchemistCauldronTile cauldron, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
        float waterOffset = getWaterOffest(cauldron.getBlockState());

        int waterLevel = cauldron.getBlockState().getValue(AlchemistCauldronBlock.LEVEL);
        if (waterLevel > 0) {
            renderWater(cauldron, poseStack, bufferSource, packedLight, waterOffset);
        }

        var floatingItems = cauldron.inputItems;
        for (int i = 0; i < floatingItems.size(); i++) {
            var itemStack = floatingItems.get(i);
            if (!itemStack.isEmpty()) {
                float f = waterLevel > 0 ? cauldron.getLevel().getGameTime() + partialTick : 15;
                Vector2f floatOffset = getFloatingItemOffset(f, i * 587);
                float yRot = (f + i * 213) / (i + 1) * 1.5f;
                renderItem(itemStack,
                        new Vector3d(
                                floatOffset.x,
                                waterOffset + i * .01f,
                                floatOffset.y),
                        yRot, cauldron, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            }
        }
    }

    public Vector2f getFloatingItemOffset(float time, int offset) {
        //for our case, offset never changes
        float xspeed = offset % 2 == 0 ? .0075f : .025f * (1 + (offset % 88) * .001f);
        float yspeed = offset % 2 == 0 ? .025f : .0075f * (1 + (offset % 88) * .001f);
        float x = (time + offset) * xspeed;
        x = (Math.abs((x % 2) - 1) + 1) / 2;
        float y = (time + offset + 4356) * yspeed;
        y = (Math.abs((y % 2) - 1) + 1) / 2;

        //these values are "bouncing" between 0-1. however, this needs to be bounded to inside the limits of the cauldron, taking into account the item size
        x = MathHelper.lerp(x, -.2f, .75f);
        y = MathHelper.lerp(y, -.2f, .75f);
        return new Vector2f(x, y);

    }

    public static float getWaterOffest(BlockState blockState) {
        return MathHelper.lerp(AlchemistCauldronBlock.getLevel(blockState) / (float) AlchemistCauldronBlock.MAX_LEVELS, .25f, .9f);
    }

    private void renderWater(AlchemistCauldronTile cauldron, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, float waterOffset) {
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.beaconBeam(new ResourceLocation("textures/block/water_still.png"), true));
        long color = cauldron.getAverageWaterColor();
        var rgb = colorFromLong(color);

        Matrix4f pose = poseStack.last().pose();
        int frames = 32;
        float frameSize = 1f / frames;
        long frame = (cauldron.getLevel().getGameTime() / 3) % frames;
        float min_u = 0;
        float max_u = 1;
        float min_v = (frameSize * frame);
        float max_v = (frameSize * (frame + 1));


        consumer.vertex(pose, 1, waterOffset, 0).color(rgb.x(), rgb.y(), rgb.z(), 1f).uv(max_u, min_v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, 0, waterOffset, 0).color(rgb.x(), rgb.y(), rgb.z(), 1f).uv(min_u, min_v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, 0, waterOffset, 1).color(rgb.x(), rgb.y(), rgb.z(), 1f).uv(min_u, max_v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
        consumer.vertex(pose, 1, waterOffset, 1).color(rgb.x(), rgb.y(), rgb.z(), 1f).uv(max_u, max_v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
    }

    private Vector3f colorFromLong(long color) {
        //Copied from potion utils
        return new Vector3f(
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        );
    }

    private void renderItem(ItemStack itemStack, Vector3d offset, float yRot, AlchemistCauldronTile tile, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        //renderId seems to be some kind of uuid/salt
        int renderId = (int) tile.getBlockPos().asLong();
        //BakedModel model = itemRenderer.getModel(itemStack, null, null, renderId);
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
        poseStack.scale(0.4f, 0.4f, 0.4f);

        itemRenderer.renderStatic(itemStack, ItemCameraTransforms.TransformType.FIXED, WorldRenderer.getLightColor(tile.getLevel(), tile.getBlockPos()), packedOverlay, poseStack, bufferSource, renderId);
        poseStack.popPose();
    }

}
