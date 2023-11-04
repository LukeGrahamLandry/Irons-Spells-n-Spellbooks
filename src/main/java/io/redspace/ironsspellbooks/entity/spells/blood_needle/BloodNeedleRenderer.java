package io.redspace.ironsspellbooks.entity.spells.blood_needle;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.entity.spells.blood_slash.BloodSlashProjectile;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class BloodNeedleRenderer extends EntityRenderer<BloodNeedle> {
//    private static final ResourceLocation[] TEXTURES = {
//            IronsSpellbooks.id("textures/entity/blood_needle/needle_0.png"),
//            IronsSpellbooks.id("textures/entity/blood_needle/needle_1.png"),
//            IronsSpellbooks.id("textures/entity/blood_needle/needle_2.png"),
//            IronsSpellbooks.id("textures/entity/blood_needle/needle_3.png"),
//            IronsSpellbooks.id("textures/entity/blood_needle/needle_4.png"),
//            IronsSpellbooks.id("textures/entity/blood_needle/needle_5.png"),
//            IronsSpellbooks.id("textures/entity/blood_needle/needle_6.png"),
//            IronsSpellbooks.id("textures/entity/blood_needle/needle_7.png")
//    };
    private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/blood_needle/needle_5.png");

    //private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/magic_missile_projectile.png");
    public BloodNeedleRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(BloodNeedle entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();

        Entry pose = poseStack.last();
        Vector3d motion = entity.getDeltaMovement();
        float xRot = -((float) (MathHelper.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (MathHelper.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(entity.getZRot() + (entity.tickCount + partialTicks) * 40));

        //VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

        //float halfWidth = width * .5f;
        //old color: 125, 0, 10
        float width = 2.5f;
        //drawSlash(pose, bufferSource, light, width, 2);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(45));
        float scale = entity.getScale();
        poseStack.scale(scale, scale, scale);
        drawSlash(pose, entity, bufferSource, light, width);

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    private void drawSlash(Entry pose, BloodNeedle entity, IRenderTypeBuffer bufferSource, int light, float width) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        float halfWidth = width * .5f;
        //old color: 125, 0, 10
        consumer.vertex(poseMatrix, 0, -halfWidth, -halfWidth).color(90, 0, 10, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, halfWidth, -halfWidth).color(90, 0, 10, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, halfWidth, halfWidth).color(90, 0, 10, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, -halfWidth, halfWidth).color(90, 0, 10, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0f, 1f, 0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BloodNeedle entity) {
//        int frame = (entity.tickCount / 4) % TEXTURES.length;
//        return TEXTURES[frame];
        return TEXTURE;
    }

}