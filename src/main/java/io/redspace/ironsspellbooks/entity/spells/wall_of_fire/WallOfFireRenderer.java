package io.redspace.ironsspellbooks.entity.spells.wall_of_fire;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class WallOfFireRenderer extends EntityRenderer<WallOfFireEntity> {

    private static ResourceLocation TEXTURE = new ResourceLocation("textures/block/fire_0.png");

    //private static ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    public WallOfFireRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(WallOfFireEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        Entry pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float height = 3;
        Vector3d origin = entity.position();

        for (int i = 0; i < entity.subEntities.length - 1; i++) {
            Vector3d start = entity.subEntities[i].position().subtract(origin);
            Vector3d end = entity.subEntities[i + 1].position().subtract(origin);
            int frameCount = 32;
            int frame = (entity.tickCount + i * 87) % frameCount;
            float uvPerFrame = (1 / (float) frameCount);
            float uvY = frame * uvPerFrame;
            poseStack.pushPose();
            consumer.vertex(poseMatrix, (float) start.x, (float) start.y, (float) start.z).color(255, 255, 255, 255).uv(0f, uvY + uvPerFrame).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, (float) start.x, (float) start.y + height, (float) start.z).color(255, 255, 255, 255).uv(0f, uvY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, (float) end.x, (float) end.y + height, (float) end.z).color(255, 255, 255, 255).uv(1f, uvY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, (float) end.x, (float) end.y, (float) end.z).color(255, 255, 255, 255).uv(1f, uvY + uvPerFrame).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            consumer.vertex(poseMatrix, (float) start.x, (float) start.y, (float) start.z).color(255, 255, 255, 255).uv(0f, uvY + uvPerFrame).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, (float) start.x, (float) start.y + height, (float) start.z).color(255, 255, 255, 255).uv(0f, uvY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, (float) end.x, (float) end.y + height, (float) end.z).color(255, 255, 255, 255).uv(1f, uvY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, (float) end.x, (float) end.y, (float) end.z).color(255, 255, 255, 255).uv(1f, uvY + uvPerFrame).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();

            poseStack.popPose();
        }
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }


    @Override
    public ResourceLocation getTextureLocation(WallOfFireEntity entity) {
        return TEXTURE;
    }
}