package io.redspace.ironsspellbooks.entity.spells.icicle;

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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class IcicleRenderer extends EntityRenderer<IcicleProjectile> {
    public static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/icicle_projectile.png");
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(IronsSpellbooks.MODID, "icicle_model"), "main");

    private final ModelRenderer body;
    private final ModelRenderer tip;

    public IcicleRenderer(Context context) {
        super(context);
        ModelRenderer modelpart = context.bakeLayer(IcicleRenderer.MODEL_LAYER_LOCATION);
        this.tip = modelpart.getChild("tip");
        this.body = modelpart.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(0, 0).addBox(-0.75F, -0.75F, -6.0F, 1.5F, 1.5F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.25F, -1.25F, -1.0F, 2.5F, 2.5F, 5.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 20, 10);
    }

    @Override
    public void render(IcicleProjectile entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();

        Vector3d motion = entity.getDeltaMovement();
        float xRot = -((float) (MathHelper.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (MathHelper.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));

        IVertexBuilder consumer2 = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.body.render(poseStack, consumer2, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        this.tip.render(poseStack, consumer2, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(IcicleProjectile entity) {
        return TEXTURE;
    }
}