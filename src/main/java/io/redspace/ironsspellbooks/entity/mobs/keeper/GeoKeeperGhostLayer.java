package io.redspace.ironsspellbooks.entity.mobs.keeper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class GeoKeeperGhostLayer extends GeoLayerRenderer<AbstractSpellCastingMob> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/keeper/keeper_ghost.png");

    public GeoKeeperGhostLayer(IGeoRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractSpellCastingMob entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        int hurtTime = entityLivingBaseIn.hurtTime;
        if (hurtTime > 0) {
            float alpha = (float) hurtTime / entityLivingBaseIn.hurtDuration;
            float f = (float) (entityLivingBaseIn.tickCount + partialTicks) * .6f;
            RenderType renderType = RenderType.energySwirl(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
            //renderType = RenderType.endGateway();
            IVertexBuilder vertexconsumer = bufferIn.getBuffer(renderType);
            matrixStackIn.pushPose();
            GeoModel model = getEntityModel().getModel(KeeperModel.modelResource);
//        var bone = model.getBone("head");
//        bone.ifPresent((b) -> b.setHidden(true));
            float scale = 1 / (1.3f);
            matrixStackIn.scale(scale, scale, scale);

            model.getBone("body").ifPresent((rootBone) -> {
                rootBone.childBones.forEach(bone -> {
                    //IronsSpellbooks.LOGGER.debug("{}", bone.getName());
                    if (bone.getName().equals("head")) {
                        bone.setScale(.65f, .65f, .65f);
                    } else
                        bone.setScale(.95f, .99f, .95f);
                });
            });


            this.getRenderer().render(
                    model,
                    entityLivingBaseIn, partialTicks, renderType, matrixStackIn, bufferIn,
                    vertexconsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, .15f * alpha, .02f * alpha, 0.0f * alpha, 1f
            );
//        bone.ifPresent((b) -> b.setHidden(false));
            model.getBone("body").ifPresent((rootBone) -> {
                rootBone.childBones.forEach(bone -> {
                    //IronsSpellbooks.LOGGER.debug("{}", bone.getName());

                    bone.setScale(1, 1, 1);
                });
            });
            matrixStackIn.popPose();
        }

    }


}