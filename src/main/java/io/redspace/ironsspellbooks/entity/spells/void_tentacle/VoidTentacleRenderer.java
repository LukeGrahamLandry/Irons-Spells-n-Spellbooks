package io.redspace.ironsspellbooks.entity.spells.void_tentacle;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class VoidTentacleRenderer extends GeoEntityRenderer<VoidTentacle> {

    public VoidTentacleRenderer(EntityRendererManager context) {
        super(context, new VoidTentacleModel());
        this.addLayer(new VoidTentacleEmissiveLayer(this));
        this.shadowRadius = 1f;
    }

//    @Override
//    public RenderType getRenderType(VoidTentacle animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
//        return RenderType.endGateway();
//    }


    @Override
    public void render(VoidTentacle animatable, float entityYaw, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight) {
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
