package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.LivingEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class GlowingEyesLayer {
    public static final ResourceLocation EYE_TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/purple_eyes.png");
    public static final RenderType EYES = RenderType.eyes(EYE_TEXTURE);

    public static class Vanilla<T extends LivingEntity, M extends BipedModel<T>> extends AbstractEyesLayer<T, M> {

        public Vanilla(IEntityRenderer pRenderer) {
            super(pRenderer);
        }

        @Override
        public RenderType renderType() {
            return EYES;
        }

        @Override
        public void render(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int pPackedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            EyeType eye = getEyeType(livingEntity);
            if (eye != EyeType.None) {
                IVertexBuilder vertexconsumer = multiBufferSource.getBuffer(this.renderType());

                //pMatrixStack.translate(0, -eye.yOffset, -eye.forwardOffset);
                float scale = getEyeScale(livingEntity);
                poseStack.scale(scale, scale, scale);
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, eye.r, eye.g, eye.b, 1.0F);
            }
        }
    }

    public static class Geo extends GeoLayerRenderer<AbstractSpellCastingMob> {
        public Geo(IGeoRenderer entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public RenderType getRenderType(ResourceLocation textureLocation) {
            return EYES;
        }

        @Override
        public void render(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int packedLightIn, AbstractSpellCastingMob abstractSpellCastingMob, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            EyeType eye = getEyeType(abstractSpellCastingMob);
            if (eye != EyeType.None) {
                GeoModel model = entityRenderer.getGeoModelProvider().getModel(entityRenderer.getGeoModelProvider().getModelLocation(abstractSpellCastingMob));
                model.getBone("head").ifPresent((headBone) -> {
                    float scale = getEyeScale(abstractSpellCastingMob);
                    headBone.setScale(scale, scale, scale);
                    this.renderModel(this.getEntityModel(), EYE_TEXTURE, poseStack, multiBufferSource, packedLightIn, abstractSpellCastingMob, partialTicks, eye.r, eye.g, eye.b);
                });
            }
        }
    }

    public static EyeType getEyeType(LivingEntity entity) {
        //Sorted by most prioritized color
        if (ClientMagicData.getSyncedSpellData(entity).hasEffect(SyncedSpellData.ABYSSAL_SHROUD))
            return EyeType.Abyssal;
//        else if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.SHADOWWALKER_HELMET.get()))
//            return EyeType.Ender_Armor;
        else return EyeType.None;
    }

    public static float getEyeScale(LivingEntity entity) {
        //Sorted by most prioritized scale (highest to lowest)
        if (entity.getItemBySlot(EquipmentSlotType.HEAD).is(ItemRegistry.SHADOWWALKER_HELMET.get()))
            return EyeType.Ender_Armor.scale;
        if (ClientMagicData.getSyncedSpellData(entity).hasEffect(SyncedSpellData.ABYSSAL_SHROUD))
            return EyeType.Abyssal.scale;
        else return EyeType.None.scale;
    }

    public enum EyeType {
        None(0, 0, 0, 0),
        Abyssal(1f, 1f, 1f, 1f),
        Ender_Armor(.816f, 0f, 1f, 1.15f);

        public final float r, g, b, scale;

        EyeType(float r, float g, float b, float scale) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.scale = scale;
        }
    }
}

