package io.redspace.ironsspellbooks.block.pedestal;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.vector.Vector3d;


public class PedestalRenderer implements TileEntityRenderer<PedestalTile> {
    ItemRenderer itemRenderer;

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    private static final Vector3d ITEM_POS = new Vector3d(.5, 1.5, .5);

    @Override
    public void render(PedestalTile pedestalTile, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
        ItemStack heldItem = pedestalTile.getHeldItem();

        if (!heldItem.isEmpty()) {
            PlayerEntity player = Minecraft.getInstance().player;
            float bob = (float) (Math.sin((player.tickCount + partialTick) * .1f) * .0875f);
            float rotation = player.tickCount * 2 + partialTick;
            renderItem(heldItem, ITEM_POS.add(0, bob, 0), rotation, pedestalTile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }

    }

    private void renderItem(ItemStack itemStack, Vector3d offset, float yRot, PedestalTile pedestalTile, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        //renderId seems to be some kind of uuid/salt
        int renderId = (int) pedestalTile.getBlockPos().asLong();
        //BakedModel model = itemRenderer.getModel(itemStack, null, null, renderId);

        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        if(itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof ToolItem){
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(-45));

        }
        //poseStack.mulPose(Vector3f.ZP.rotationDegrees(yRot));

        //poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        poseStack.scale(0.65f, 0.65f, 0.65f);

        itemRenderer.renderStatic(itemStack, ItemCameraTransforms.TransformType.FIXED, WorldRenderer.getLightColor(pedestalTile.getLevel(), pedestalTile.getBlockPos()), packedOverlay, poseStack, bufferSource, renderId);
        poseStack.popPose();
    }

}
