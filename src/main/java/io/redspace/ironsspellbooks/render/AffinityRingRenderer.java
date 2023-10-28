package io.redspace.ironsspellbooks.render;

import net.minecraft.client.renderer.RenderHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.item.curios.RingData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

public class AffinityRingRenderer extends ItemStackTileEntityRenderer {


    private final ItemRenderer renderer;
    private final ResourceLocation defaultModel = IronsSpellbooks.id("item/affinity_ring_evocation");

    public AffinityRingRenderer(ItemRenderer renderDispatcher, EntityModelSet modelSet) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), modelSet);
        this.renderer = renderDispatcher;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);

        IBakedModel model;
        if (!RingData.hasRingData(itemStack)) {
            model = renderer.getItemModelShaper().getModelManager().getModel(defaultModel);
        } else {
            ResourceLocation modelResource = getAffinityRingModelLocation(RingData.getRingData(itemStack).getSpell().getSchoolType().getId());
            model = renderer.getItemModelShaper().getModelManager().getModel(modelResource);
        }

        if (transformType == ItemCameraTransforms.TransformType.GUI) {
            RenderHelper.setupForFlatItems();
            renderer.render(itemStack, transformType, false, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
            RenderHelper.setupFor3DItems();
        } else {
            boolean leftHand = transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
            renderer.render(itemStack, transformType, leftHand, poseStack, bufferSource, combinedLightIn, combinedOverlayIn, model);
        }
        poseStack.popPose();
    }

    public static ResourceLocation getAffinityRingModelLocation(ResourceLocation schoolResource) {
        String namespace = schoolResource.getNamespace();
        String schoolName = schoolResource.getPath();
        return new ResourceLocation(namespace, "item/affinity_ring_" + schoolName);
    }
}
