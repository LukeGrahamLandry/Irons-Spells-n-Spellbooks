package io.redspace.ironsspellbooks.entity.spells.void_tentacle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class VoidTentacleEmissiveLayer extends GeoLayerRenderer<VoidTentacle> {
    public static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/void_tentacle/void_tentacle_emissive.png");

    public VoidTentacleEmissiveLayer(IGeoRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, VoidTentacle entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType renderType = RenderType.eyes(TEXTURE);
        //renderType = RenderType.endGateway();
        IVertexBuilder vertexconsumer = bufferIn.getBuffer(renderType);
        matrixStackIn.pushPose();
        float f = MathHelper.sin((float) ((entityLivingBaseIn.tickCount + partialTicks + ((entityLivingBaseIn.getX() + entityLivingBaseIn.getZ()) * 500)) * .15f)) * .5f + .5f;
        //IronsSpellbooks.LOGGER.debug("{}", f);
        GeoModel model = this.getEntityModel().getModel(VoidTentacleModel.modelResource);
        this.getRenderer().render(
                model,
                entityLivingBaseIn, partialTicks, renderType, matrixStackIn, bufferIn,
                vertexconsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, f, f, f, 1f
        );
        matrixStackIn.popPose();
    }

}