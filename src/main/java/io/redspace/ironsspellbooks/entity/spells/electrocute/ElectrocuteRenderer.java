package io.redspace.ironsspellbooks.entity.spells.electrocute;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ElectrocuteRenderer extends EntityRenderer<ElectrocuteProjectile> {
    private static ResourceLocation TEXTURES[] = {
            IronsSpellbooks.id("textures/entity/electric_beams/beam_1.png"),
            IronsSpellbooks.id("textures/entity/electric_beams/beam_2.png"),
            IronsSpellbooks.id("textures/entity/electric_beams/beam_3.png"),
            IronsSpellbooks.id("textures/entity/electric_beams/beam_4.png")
    };
    private static ResourceLocation SOLID = IronsSpellbooks.id("textures/entity/electric_beams/solid.png");

    public ElectrocuteRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(ElectrocuteProjectile entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        if (entity.getOwner() == null)
            return;
        poseStack.pushPose();
        MatrixStack.Entry pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

//        RenderSystem.disableDepthTest();
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);

        //VertexConsumer consumer = bufferSource.getBuffer(RenderType.lightning());
        poseStack.translate(0, entity.getEyeHeight() * .5f, 0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-entity.getOwner().yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(entity.getOwner().xRot));

        if (entity.getAge() % 2 == 0 && !Minecraft.getInstance().isPaused())
            entity.generateLightningBeams();
        List<Vector3d> segments = entity.getBeamCache();
        //irons_spellbooks.LOGGER.debug("ElectrocuteRenderer.segments.length: {}",segments.size());

        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity)));
        float width = .25f;
        float height = width;
        Vector3d start = Vector3d.ZERO;//entity.getOwner().getEyePosition(0).add(entity.getForward().normalize().scale(.15f));
        for (int i = 0; i < segments.size() - 1; i += 2) {
            Vector3d from = segments.get(i).add(start);
            Vector3d to = segments.get(i + 1).add(start);
            drawHull(from, to, width, height, pose, consumer, 0, 156, 255, 30);
            drawHull(from, to, width * .55f, height * .55f, pose, consumer, 0, 226, 255, 30);
        }

        consumer = bufferSource.getBuffer(RenderType.energySwirl(getTextureLocation(entity),0,0));
        for (int i = 0; i < segments.size() - 1; i += 2) {
            Vector3d from = segments.get(i).add(start);
            Vector3d to = segments.get(i + 1).add(start);
            drawHull(from, to, width * .2f, height * .2f, pose, consumer, 255, 255, 255, 255);
        }


//        drawSegment(new Vec3(0, 0, 0), new Vec3(-1, 1, 3), 1, pose, consumer, entity, bufferSource, light);
//        drawSegment(new Vec3(-1, 1, 3), new Vec3(1, 1, 5), 1, pose, consumer, entity, bufferSource, light);
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public void drawHull(Vector3d from, Vector3d to, float width, float height, MatrixStack.Entry pose, IVertexBuilder consumer, int r, int g, int b, int a) {
        //Bottom
        drawQuad(from.subtract(0, height * .5f, 0), to.subtract(0, height * .5f, 0), width, 0, pose, consumer, r, g, b, a);
        //Top
        drawQuad(from.add(0, height * .5f, 0), to.add(0, height * .5f, 0), width, 0, pose, consumer, r, g, b, a);
        //Left
        drawQuad(from.subtract(width * .5f, 0, 0), to.subtract(width * .5f, 0, 0), 0, height, pose, consumer, r, g, b, a);
        //Right
        drawQuad(from.add(width * .5f, 0, 0), to.add(width * .5f, 0, 0), 0, height, pose, consumer, r, g, b, a);
    }

    public void drawQuad(Vector3d from, Vector3d to, float width, float height, MatrixStack.Entry pose, IVertexBuilder consumer, int r, int g, int b, int a) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        //to = new Vec3(1, 0, 10);
        float halfWidth = width * .5f;
        float halfHeight = height * .5f;
        //float height = (float) (Math.random() * .25f) + .25f;
        consumer.vertex(poseMatrix, (float) from.x - halfWidth, (float) from.y - halfHeight, (float) from.z).color(r, g, b, a).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, (float) from.x + halfWidth, (float) from.y + halfHeight, (float) from.z).color(r, g, b, a).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, (float) to.x + halfWidth, (float) to.y + halfHeight, (float) to.z).color(r, g, b, a).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, (float) to.x - halfWidth, (float) to.y - halfHeight, (float) to.z).color(r, g, b, a).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(ElectrocuteProjectile p_115264_) {
        //return TEXTURES[(int) (Math.random() * 4)];
        return SOLID;
    }
}