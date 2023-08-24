package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class EnergySwirlLayer {
    public static final ResourceLocation EVASION_TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/evasion.png");
    public static final ResourceLocation CHARGE_TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/charged.png");

    public static class Vanilla extends LayerRenderer<PlayerEntity, BipedModel<PlayerEntity>> {
        public static ModelLayerLocation ENERGY_LAYER = new ModelLayerLocation(new ResourceLocation(IronsSpellbooks.MODID, "energy_layer"), "main");
        private final BipedModel<PlayerEntity> model;
        private final ResourceLocation TEXTURE;
        private final Long shouldRenderFlag;

        public Vanilla(IEntityRenderer pRenderer, ResourceLocation texture, Long shouldRenderFlag) {
            super(pRenderer);
            this.model = new BipedModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ENERGY_LAYER));
            this.TEXTURE = texture;
            this.shouldRenderFlag = shouldRenderFlag;
        }

        public void render(MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, int pPackedLight, PlayerEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (EnergySwirlLayer.shouldRender(pLivingEntity, shouldRenderFlag)) {
                float f = (float) pLivingEntity.tickCount + pPartialTicks;
                BipedModel<PlayerEntity> entitymodel = this.model();
                entitymodel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
                this.getParentModel().copyPropertiesTo(entitymodel);
                IVertexBuilder vertexconsumer = pBuffer.getBuffer(EnergySwirlLayer.getRenderType(TEXTURE, f));
                entitymodel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                entitymodel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 0.8F, 0.8F, 0.8F, 1.0F);
            }
        }

        protected BipedModel<PlayerEntity> model() {
            return model;
        }

        protected boolean shouldRender(PlayerEntity entity) {
            return true;
        }
    }

    public static class Geo extends GeoLayerRenderer<AbstractSpellCastingMob> {
        private final ResourceLocation TEXTURE/* = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/evasion.png")*/;
        private final Long shouldRenderFlag;

        public Geo(IGeoRenderer entityRendererIn, ResourceLocation texture, Long shouldRenderFlag) {
            super(entityRendererIn);
            this.TEXTURE = texture;
            this.shouldRenderFlag = shouldRenderFlag;

        }

        @Override
        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractSpellCastingMob entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (EnergySwirlLayer.shouldRender(entityLivingBaseIn, shouldRenderFlag)) {
                float f = (float) entityLivingBaseIn.tickCount + partialTicks;
                var renderType = EnergySwirlLayer.getRenderType(TEXTURE, f);
                IVertexBuilder vertexconsumer = bufferIn.getBuffer(renderType);
                matrixStackIn.pushPose();
//            this.getRenderer().setCurrentRTB(bufferIn);
                var model = ((AnimatedGeoModel) this.getEntityModel()).getModel(AbstractSpellCastingMob.modelResource);
                model.getBone("body").ifPresent((rootBone) -> {
                    rootBone.childBones.forEach(bone -> {
                        bone.setScale(1.1f, 1.1f, 1.1f);
                    });
                });
                this.getRenderer().render(model, entityLivingBaseIn, partialTicks, renderType, matrixStackIn, bufferIn,
                        vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, .5f, .5f, .5f, 1f);
                model.getBone("body").ifPresent((rootBone) -> {
                    rootBone.childBones.forEach(bone -> {
                        bone.setScale(1f, 1f, 1f);
                    });
                });
                matrixStackIn.popPose();
            }
        }
    }

    private static RenderType getRenderType(ResourceLocation texture, float f) {
        return RenderType.energySwirl(texture, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
    }

    private static boolean shouldRender(LivingEntity entity, Long shouldRenderFlag) {
        return ClientMagicData.getSyncedSpellData(entity).hasEffect(shouldRenderFlag);
    }
}
