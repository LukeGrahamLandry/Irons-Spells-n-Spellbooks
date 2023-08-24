package io.redspace.ironsspellbooks.entity.spells.target_area;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.render.SpellTargetingLayer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class TargetAreaRenderer extends EntityRenderer<TargetedAreaEntity> {
    public TargetAreaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(TargetedAreaEntity pEntity) {
        return null;
    }

    @Override
    public void render(TargetedAreaEntity entity, float pEntityYaw, float pPartialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {

        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.energySwirl(SpellTargetingLayer.TEXTURE, 0, 0));
        var color = entity.getColor();
        poseStack.pushPose();
        MatrixStack.Entry pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float radius = entity.getRadius();
        float correction = .05f / radius;
        float circumference = (2.0f + correction) * radius * MathHelper.PI;
        int segments = (int) (3 * radius + 9);
        float angle = 360f / segments;
        float segmentWidth = (circumference / segments);

        for (int i = 0; i < segments; i++) {
            drawPlane(consumer, color, poseMatrix, normalMatrix, light, segmentWidth, radius);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(angle));
        }
        poseStack.popPose();
    }

    private static void drawPlane(IVertexBuilder consumer, Vector3f color, Matrix4f poseMatrix, Matrix3f normalMatrix, int light, float width, float radius) {
        float halfWidth = width * .5f;
        consumer.vertex(poseMatrix, halfWidth, 0, radius).color(color.x(), color.y(), color.z(), 1).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light * 4).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 1, radius).color(color.x(), color.y(), color.z(), 1).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light * 4).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, -halfWidth, 1, radius).color(color.x(), color.y(), color.z(), 1).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light * 4).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, -halfWidth, 0, radius).color(color.x(), color.y(), color.z(), 1).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light * 4).normal(normalMatrix, 0f, 1f, 0f).endVertex();
    }
}
