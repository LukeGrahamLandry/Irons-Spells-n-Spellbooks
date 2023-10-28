package io.redspace.ironsspellbooks.entity.spells.target_area;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import com.mojang.math.Vector3f;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
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

        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.energySwirl(SpellRenderingHelper.SOLID, 0, 0));
        var color = entity.getColor();
        poseStack.pushPose();
        MatrixStack.Entry pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float radius = entity.getRadius();
        int segments = (int) (5 * radius + 9);
        float angle = 2 * MathHelper.PI / segments;
        float entityY = (float) MathHelper.lerp(pPartialTick, entity.yOld, entity.getY());

        for (int i = 0; i < segments; i++) {
            float theta = angle * i;
            float theta2 = angle * (i + 1);
            float x1 = radius * MathHelper.cos(theta);
            float x2 = radius * MathHelper.cos(theta2);
            float z1 = radius * MathHelper.sin(theta);
            float z2 = radius * MathHelper.sin(theta2);

            float y1 = Utils.findRelativeGroundLevel(entity.level, entity.position().add(x1, entity.getBbHeight(), z1), (int) (entity.getBbHeight() * 2.5)) - entityY;
            float y2 = Utils.findRelativeGroundLevel(entity.level, entity.position().add(x2, entity.getBbHeight(), z2), (int) (entity.getBbHeight() * 2.5)) - entityY;
            consumer.vertex(poseMatrix, x2, y2, z2).color(color.x(), color.y(), color.z(), 1).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light * 4).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, x2, y2 + 0.6f, z2).color(0, 0, 0, 1).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light * 4).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, x1, y1 + 0.6f, z1).color(0, 0, 0, 1).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light * 4).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, x1, y1, z1).color(color.x(), color.y(), color.z(), 1).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light * 4).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        }
        poseStack.popPose();
    }
}
