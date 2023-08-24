package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import static io.redspace.ironsspellbooks.spells.SpellType.BLESSING_OF_LIFE_SPELL;
import static io.redspace.ironsspellbooks.spells.SpellType.HEALING_CIRCLE_SPELL;

public class SpellTargetingLayer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/target/heal.png");

    public static class Vanilla<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
        public Vanilla(IEntityRenderer<T, M> pRenderer) {
            super(pRenderer);
        }


        @Override
        public void render(MatrixStack poseStack, IRenderTypeBuffer bufferSource, int pPackedLight, T entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (shouldRender(entity)) {
                renderTargetLayer(poseStack, bufferSource, entity);
            }
        }

    }

    public static class Geo extends GeoLayerRenderer<AbstractSpellCastingMob> {
        public Geo(IGeoRenderer entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int packedLightIn, AbstractSpellCastingMob abstractSpellCastingMob, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (shouldRender(abstractSpellCastingMob)) {
//                //It's upside down???
//                poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
//                poseStack.translate(0, -(abstractSpellCastingMob.getBbWidth() + abstractSpellCastingMob.getBbHeight()) / 2, 0);
                poseStack.pushPose();
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                poseStack.translate(0, -abstractSpellCastingMob.getBoundingBox().getYsize() / 2, 0);
                renderTargetLayer(poseStack, multiBufferSource, abstractSpellCastingMob);
                poseStack.popPose();
            }
        }
    }

    private static Vector3f getColor(int spellId) {
        //Specific Spells
        if(spellId == BLESSING_OF_LIFE_SPELL.getValue() || spellId == HEALING_CIRCLE_SPELL.getValue())
            return new Vector3f(.85f, 0, 0);
        //By School Otherwise
        return switch (SpellType.getTypeFromValue(spellId).getSchoolType()) {
            case HOLY -> new Vector3f(.85f, .75f, .25f);
            case ICE -> new Vector3f(.25f, .25f, 1f);
            case POISON -> new Vector3f(.41f, .88f, .22f);
            default -> new Vector3f(.8f, .8f, .8f);
        };
    }

    public static void renderTargetLayer(MatrixStack poseStack, IRenderTypeBuffer bufferSource, LivingEntity entity) {
        //EntityRenderDispatcher#169(renderHitbox)
//        AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
//        double magicYOffset = 1.5 - aabb.getYsize();
//        poseStack.translate(0, magicYOffset, 0);
//        poseStack.pushPose();
//        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), aabb, 1.0F, 1.0F, 1.0F, 1.0F);
//        poseStack.popPose();

        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.energySwirl(TEXTURE, 0, 0));
        AxisAlignedBB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());

        float width = (float) aabb.getXsize();
        float height = (float) aabb.getYsize();
        float halfWidth = width * .55f;
        float magicYOffset = (float) (1.5 - height);
        var color = getColor(ClientMagicData.getTargetingData().spellId);
        color.mul(.4f);
        poseStack.pushPose();
        poseStack.translate(0, magicYOffset, 0);
        MatrixStack.Entry pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();


        //LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), aabb, 1.0F, 1.0F, 1.0F, 1.0F);
        for (int i = 0; i < 4; i++) {
            consumer.vertex(poseMatrix, halfWidth, height, halfWidth).color(color.x(), color.y(), color.z(), 1).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, halfWidth, 0, halfWidth).color(color.x(), color.y(), color.z(), 1).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, -halfWidth, 0, halfWidth).color(color.x(), color.y(), color.z(), 1).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            consumer.vertex(poseMatrix, -halfWidth, height, halfWidth).color(color.x(), color.y(), color.z(), 1).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));

        }

        poseStack.popPose();
    }

    public static boolean shouldRender(LivingEntity entity) {
        return ClientMagicData.getTargetingData().isTargeted(entity);
    }

}
