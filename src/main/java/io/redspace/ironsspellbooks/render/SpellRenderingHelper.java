package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.spells.blood.RayOfSiphoningSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellRenderingHelper {
    public static final ResourceLocation SOLID = IronsSpellbooks.id("textures/entity/ray/solid.png");
    public static final ResourceLocation BEACON = IronsSpellbooks.id("textures/entity/ray/beacon_beam.png");
    public static final ResourceLocation STRAIGHT_GLOW = IronsSpellbooks.id("textures/entity/ray/ribbon_glow.png");
    public static final ResourceLocation TWISTING_GLOW = IronsSpellbooks.id("textures/entity/ray/twisting_glow.png");

    public static void renderSpellHelper(SyncedSpellData spellData, LivingEntity castingMob, MatrixStack poseStack, IRenderTypeBuffer bufferSource, float partialTicks) {
        if (SpellRegistry.RAY_OF_SIPHONING_SPELL.get().getSpellId().equals(spellData.getCastingSpellId())) {
            renderRayOfSiphoning(castingMob, poseStack, bufferSource, partialTicks);
        }
    }

    public static void renderRayOfSiphoning(LivingEntity entity, MatrixStack poseStack, IRenderTypeBuffer bufferSource, float partialTicks) {

        poseStack.pushPose();
        poseStack.translate(0, entity.getEyeHeight() * .8f, 0);
        if (entity instanceof AbstractSpellCastingMob mob/* && mob.getTarget() != null*/) {
            //Vec3 dir = mob.getEyePosition().subtract(mob.getTarget().position().add(0, mob.getTarget().getEyeHeight() * .7f, 0));
            Vector3d dir = entity.getLookAngle().normalize();
            var pitch = Math.asin(dir.y);
            var yaw = Math.atan2(dir.x, dir.z);

            poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
            poseStack.mulPose(Vector3f.XP.rotationDegrees((float) -pitch * MathHelper.RAD_TO_DEG));

        } else {
            float f = MathHelper.rotlerp(entity.yRotO, entity.getYRot(), partialTicks);
            float f1 = MathHelper.lerp(partialTicks, entity.xRotO, entity.getXRot());
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-f));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(f1));
        }


        var pose = poseStack.last();
        Vector3d start = Vector3d.ZERO;//caster.getEyePosition(partialTicks);
        Vector3d end;
        //TODO: too expensive?
        Vector3d impact = Utils.raycastForEntity(entity.level, entity, RayOfSiphoningSpell.getRange(0), true).getLocation();
        float distance = (float) entity.getEyePosition().distanceTo(impact);
        float radius = .12f;
        int r = (int) (255 * .7f);
        int g = (int) (255 * 0f);
        int b = (int) (255 * 0f);
        int a = (int) (255 * 1f);

        float deltaTicks = entity.tickCount + partialTicks;
        float deltaUV = -deltaTicks % 10;
        float max = MathHelper.frac(deltaUV * 0.2F - (float) MathHelper.floor(deltaUV * 0.1F));
        float min = -1.0F + max;
        for (int j = 1; j <= distance; j++) {
            Vector3d wiggle = new Vector3d(
                    MathHelper.sin(deltaTicks * .8f) * .02f,
                    MathHelper.sin(deltaTicks * .8f + 100) * .02f,
                    MathHelper.cos(deltaTicks * .8f) * .02f
            );
            end = new Vector3d(0, 0, Math.min(j, distance)).add(wiggle);
            IVertexBuilder inner = bufferSource.getBuffer(RenderType.entityTranslucent(BEACON, true));
            drawHull(start, end, radius, radius, pose, inner, r, g, b, a, min, max);
            //drawHull(start, end, .25f, .25f, pose, outer, r / 2, g / 2, b / 2, a / 2);
            IVertexBuilder outer = bufferSource.getBuffer(RenderType.entityTranslucent(TWISTING_GLOW));
            drawQuad(start, end, radius * 4f, 0, pose, outer, r, g, b, a, min, max);
            drawQuad(start, end, 0, radius * 4f, pose, outer, r, g, b, a, min, max);
            start = end;

        }

        poseStack.popPose();
    }

    private static void drawHull(Vector3d from, Vector3d to, float width, float height, MatrixStack.Entry pose, IVertexBuilder consumer, int r, int g, int b, int a, float uvMin, float uvMax) {
        //Bottom
        drawQuad(from.subtract(0, height * .5f, 0), to.subtract(0, height * .5f, 0), width, 0, pose, consumer, r, g, b, a, uvMin, uvMax);
        //Top
        drawQuad(from.add(0, height * .5f, 0), to.add(0, height * .5f, 0), width, 0, pose, consumer, r, g, b, a, uvMin, uvMax);
        //Left
        drawQuad(from.subtract(width * .5f, 0, 0), to.subtract(width * .5f, 0, 0), 0, height, pose, consumer, r, g, b, a, uvMin, uvMax);
        //Right
        drawQuad(from.add(width * .5f, 0, 0), to.add(width * .5f, 0, 0), 0, height, pose, consumer, r, g, b, a, uvMin, uvMax);
    }

    private static void drawQuad(Vector3d from, Vector3d to, float width, float height, MatrixStack.Entry pose, IVertexBuilder consumer, int r, int g, int b, int a, float uvMin, float uvMax) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float halfWidth = width * .5f;
        float halfHeight = height * .5f;

        consumer.vertex(poseMatrix, (float) from.x - halfWidth, (float) from.y - halfHeight, (float) from.z).color(r, g, b, a).uv(0f, uvMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, (float) from.x + halfWidth, (float) from.y + halfHeight, (float) from.z).color(r, g, b, a).uv(1f, uvMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, (float) to.x + halfWidth, (float) to.y + halfHeight, (float) to.z).color(r, g, b, a).uv(1f, uvMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, (float) to.x - halfWidth, (float) to.y - halfHeight, (float) to.z).color(r, g, b, a).uv(0f, uvMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();

    }
}