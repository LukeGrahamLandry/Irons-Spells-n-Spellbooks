package io.redspace.ironsspellbooks.entity.spells.devour_jaw;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.entity.model.EvokerFangsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class DevourJawRenderer extends EntityRenderer<DevourJaw> {

    private final DevourJawModel model;

    public DevourJawRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new DevourJawModel(pContext.bakeLayer(ModelLayers.EVOKER_FANGS));
    }

    public void render(DevourJaw entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int light) {
        if (entity.tickCount < entity.waitTime)
            return;
        float f = entity.tickCount + partialTicks;
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-entity.getYRot()));
        poseStack.scale(-1, -1, 1);
        poseStack.scale(1.85f, 1.85f, 1.85f);
        this.model.setupAnim(entity, f, 0.0F, 0.0F, entity.getYRot(), entity.getXRot());
        IVertexBuilder vertexconsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, multiBufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(DevourJaw pEntity) {
        return IronsSpellbooks.id("textures/entity/devour_jaw.png");
    }

    static class DevourJawModel extends EvokerFangsModel<DevourJaw> {
        private final ModelRenderer root;
        private final ModelRenderer base;
        private final ModelRenderer upperJaw;
        private final ModelRenderer lowerJaw;

        public DevourJawModel(ModelRenderer pRoot) {
            super(pRoot);
            this.root = pRoot;
            this.base = pRoot.getChild("base");
            this.upperJaw = pRoot.getChild("upper_jaw");
            this.lowerJaw = pRoot.getChild("lower_jaw");
        }

        @Override
        public void setupAnim(DevourJaw entity, float time, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            time -= entity.waitTime;
            float interval = entity.warmupTime - entity.waitTime;

            float f = MathHelper.clamp(time / interval, 0, 1);
            f = 1 - f * f * f * f;
            this.upperJaw.zRot = (float) Math.PI - f * 0.35F * (float) Math.PI;
            this.lowerJaw.zRot = (float) Math.PI + f * 0.35F * (float) Math.PI;

            float f2 = (time / interval);
            f2 = .5f * MathHelper.cos(.5f * MathHelper.PI * (f2 - 1)) + .5f;
            f2 *= f2;
            this.upperJaw.y = -18F * f2 + 16f;
            this.lowerJaw.y = this.upperJaw.y;
            this.base.y = this.upperJaw.y;
        }
    }
}
