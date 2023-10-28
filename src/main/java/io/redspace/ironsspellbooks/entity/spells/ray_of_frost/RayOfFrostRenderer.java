package io.redspace.ironsspellbooks.entity.spells.ray_of_frost;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.gust.GustCollider;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RayOfFrostRenderer extends EntityRenderer<RayOfFrostVisualEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(IronsSpellbooks.MODID, "ray_of_frost_model"), "main");
    private static final ResourceLocation TEXTURE_CORE = IronsSpellbooks.id("textures/entity/ray_of_frost/core.png");
    private static final ResourceLocation TEXTURE_OVERLAY = IronsSpellbooks.id("textures/entity/ray_of_frost/overlay.png");

    private final ModelRenderer body;

    public RayOfFrostRenderer(Context context) {
        super(context);
        ModelRenderer modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -16, -8, 16, 32, 16), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public boolean shouldRender(RayOfFrostVisualEntity pLivingEntity, ClippingHelper pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    @Override
    public void render(RayOfFrostVisualEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();
        float lifetime = RayOfFrostVisualEntity.lifetime;
        float scalar = .25f;
        float length = 32 * scalar * scalar;
        float f = entity.tickCount + partialTicks;
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-entity.getYRot() - 180.0F));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-entity.getXRot() - 90));
        poseStack.scale(scalar, scalar, scalar);

        //float scale = Mth.lerp(Mth.clamp(f / 6f, 0, 1), 1, 2.3f);

        float alpha = MathHelper.clamp(1f - f / lifetime, 0, 1);

        for (float i = 0; i < entity.distance * 4; i += length) {
            poseStack.translate(0, length, 0);
            //Render overlay
            //VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE_OVERLAY));
            IVertexBuilder consumer = bufferSource.getBuffer(RenderType.energySwirl(TEXTURE_OVERLAY, 0, 0));
            {
                poseStack.pushPose();
                float expansion = MathHelper.clampedLerp(1.2f, 0, f / (lifetime));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(f * 5));
                poseStack.scale(expansion, 1, expansion);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(45));
                this.body.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, alpha);
                poseStack.popPose();
            }
            //Render core
            consumer = bufferSource.getBuffer(RenderType.energySwirl(TEXTURE_CORE, 0, 0));
            {
                poseStack.pushPose();
                float expansion = MathHelper.clampedLerp(1, 0, f / (lifetime - 8));
                poseStack.scale(expansion, 1, expansion);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(f * -10));
                this.body.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
                poseStack.popPose();
            }
        }


        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(RayOfFrostVisualEntity entity) {
        return TEXTURE_CORE;
    }

}