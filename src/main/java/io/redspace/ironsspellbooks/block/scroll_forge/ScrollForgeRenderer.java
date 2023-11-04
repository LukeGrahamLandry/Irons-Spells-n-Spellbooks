package io.redspace.ironsspellbooks.block.scroll_forge;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.util.ModTags;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.vector.Vector3d;


public class  ScrollForgeRenderer implements TileEntityRenderer<ScrollForgeTile> {
    private static final ResourceLocation PAPER_TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/block/scroll_forge_paper.png");
    private static final ResourceLocation SIGIL_TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/block/scroll_forge_sigil.png");
    ItemRenderer itemRenderer;

    public ScrollForgeRenderer(BlockEntityRendererManager context) {
        this.itemRenderer = context.getItemRenderer();
    }

    private static final Vector3d INK_POS = new Vector3d(.175, .876, .25);
    private static final Vector3d FOCUS_POS = new Vector3d(.75, .876, .4);
    private static final Vector3d PAPER_POS = new Vector3d(.5, .876, .7);

    @Override
    public void render(ScrollForgeTile scrollForgeTile, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
        ItemStack inkStack = scrollForgeTile.getStackInSlot(0);
        ItemStack paperStack = scrollForgeTile.getStackInSlot(1);
        ItemStack focusStack = scrollForgeTile.getItemHandler().getStackInSlot(2);

        if (!inkStack.isEmpty() && inkStack.getItem() instanceof InkItem) {
            renderItem(inkStack, INK_POS, 15, scrollForgeTile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
        if (!focusStack.isEmpty() && focusStack.getItem().is(ModTags.SCHOOL_FOCUS)) {
            renderItem(focusStack, FOCUS_POS, 5, scrollForgeTile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }

        if (!paperStack.isEmpty() && paperStack.is(Items.PAPER)) {
            poseStack.pushPose();
            rotatePoseWithBlock(poseStack, scrollForgeTile);
            poseStack.translate(PAPER_POS.x, PAPER_POS.y, PAPER_POS.z);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(85));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
            IVertexBuilder consumer = bufferSource.getBuffer(RenderType.entityCutout(PAPER_TEXTURE));
            int light = WorldRenderer.getLightColor(scrollForgeTile.getLevel(), scrollForgeTile.getBlockPos());

            drawQuad(.45f, poseStack.last(), consumer, light);
            poseStack.popPose();

        }

        /*
        //Test Sigil
        float angle = (Minecraft.getInstance().player.tickCount + partialTick )%360;
        poseStack.pushPose();
        rotatePoseWithBlock(poseStack, scrollForgeTile);
        poseStack.translate(INK_POS.x, INK_POS.y, INK_POS.z);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(angle));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(SIGIL_TEXTURE));
        drawQuad(.5f, poseStack.last(), consumer, LightTexture.FULL_BRIGHT);
        poseStack.popPose();

        */


    }

    private void renderItem(ItemStack itemStack, Vector3d offset, float yRot, ScrollForgeTile scrollForgeTile, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        //renderId seems to be some kind of uuid/salt
        int renderId = (int) scrollForgeTile.getBlockPos().asLong();
        //BakedModel model = itemRenderer.getModel(itemStack, null, null, renderId);

        rotatePoseWithBlock(poseStack, scrollForgeTile);

        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-yRot));

        //poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        poseStack.scale(0.45f, 0.45f, 0.45f);

        itemRenderer.renderStatic(itemStack, ItemCameraTransforms.TransformType.FIXED, WorldRenderer.getLightColor(scrollForgeTile.getLevel(), scrollForgeTile.getBlockPos()), packedOverlay, poseStack, bufferSource, renderId);
        poseStack.popPose();
    }

    private void drawQuad(float width, MatrixStack.Entry pose, IVertexBuilder consumer, int light) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        float halfWidth = width * .5f;
        consumer.vertex(poseMatrix, -halfWidth, 0, -halfWidth).color(255, 255, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0f, -1f, 0f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0, -halfWidth).color(255, 255, 255, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0f, -1f, 0f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0, halfWidth).color(255, 255, 255, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0f, -1f, 0f).endVertex();
        consumer.vertex(poseMatrix, -halfWidth, 0, halfWidth).color(255, 255, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0f, -1f, 0f).endVertex();

    }

    private void rotatePoseWithBlock(MatrixStack poseStack, ScrollForgeTile scrollForgeTile) {
        Vector3d center = new Vector3d(0.5, 0.5, 0.5);
        poseStack.translate(center.x, center.y, center.z);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(getBlockFacingDegrees(scrollForgeTile)));
        poseStack.translate(-center.x, -center.y, -center.z);
    }

    private int getBlockFacingDegrees(ScrollForgeTile tileEntity) {
        BlockState block = tileEntity.getLevel().getBlockState(tileEntity.getBlockPos());
        if (block.getBlock() instanceof ScrollForgeBlock) {
            Direction facing = block.getValue(BlockStateProperties.HORIZONTAL_FACING);
            switch (facing) {
                case NORTH:
                    return 180;
                case EAST:
                    return 90;
                case WEST:
                    return -90;
                default:
                    return 0;
            }
        } else
            return 0;

    }
}
