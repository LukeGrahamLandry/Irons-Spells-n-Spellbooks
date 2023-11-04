package io.redspace.ironsspellbooks.entity.spells.gust;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GustRenderer extends EntityRenderer<GustCollider> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(IronsSpellbooks.MODID, "gust_model"), "main");
    private static ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/trident_riptide.png");

    private final ModelRenderer body;

    public GustRenderer(Context context) {
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
    public void render(GustCollider entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-entity.yRot - 180.0F));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-entity.xRot - 90));
        poseStack.scale(.25f, .25f, .25f);

        float f = entity.tickCount + partialTicks;
        float scale = MathHelper.lerp(MathHelper.clamp(f / 6f, 0, 1), 1, 2.3f);
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        float alpha = 1f - f / 10f;

        for (int i = 0; i < 3; i++) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f * 10));
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0, scale - 1, 0);
            this.body.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, alpha);
        }


        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(GustCollider entity) {
        return TEXTURE;
    }

}