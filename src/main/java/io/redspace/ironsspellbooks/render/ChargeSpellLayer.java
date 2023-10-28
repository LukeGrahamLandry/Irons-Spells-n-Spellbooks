package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceRenderer;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowRenderer;
import io.redspace.ironsspellbooks.entity.spells.poison_arrow.PoisonArrowRenderer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.entity.LivingEntity;
import software.bernie.example.client.DefaultBipedBoneIdents;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.RenderUtils;

public class ChargeSpellLayer {

    public static class Vanilla<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {

        public Vanilla(IEntityRenderer<T, M> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(MatrixStack poseStack, IRenderTypeBuffer bufferSource, int pPackedLight, T entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            var syncedSpellData = ClientMagicData.getSyncedSpellData(entity);

            if (!syncedSpellData.isCasting()) {
                return;
            }

            var spellId = syncedSpellData.getCastingSpellId();
            poseStack.pushPose();
            var arm = getArmFromUseHand(entity);
            this.getParentModel().translateToHand(arm, poseStack);
            boolean flag = arm == HandSide.LEFT;
            if (spellId.equals(SpellRegistry.LIGHTNING_LANCE_SPELL.get().getSpellId())) {
                poseStack.translate((double) ((float) (flag ? -1 : 1) / 32.0F) - .125, .5, 0);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                float castCompletion = Utils.smoothstep(.35f, 1, ClientMagicData.getCastCompletionPercent());
                poseStack.scale(castCompletion, castCompletion, castCompletion);
                LightningLanceRenderer.renderModel(poseStack, bufferSource, entity.tickCount);
            } else if (spellId.equals(SpellRegistry.MAGIC_ARROW_SPELL.get().getSpellId())) {
                poseStack.translate(((float) (flag ? -1 : 1) / 32.0F), .5, 0);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                float castCompletion = Utils.smoothstep(.65f, 1, ClientMagicData.getCastCompletionPercent());
                poseStack.scale(castCompletion, castCompletion, castCompletion);
                MagicArrowRenderer.renderModel(poseStack, bufferSource);
            } else if (spellId.equals(SpellRegistry.POISON_ARROW_SPELL.get().getSpellId())) {
                poseStack.translate(((float) (flag ? -1 : 1) / 32.0F), 1, 0);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                float castCompletion = Utils.smoothstep(.65f, 1, ClientMagicData.getCastCompletionPercent());
                poseStack.scale(castCompletion, castCompletion, castCompletion);
                PoisonArrowRenderer.renderModel(poseStack, bufferSource, pPackedLight);
            }
            poseStack.popPose();
        }
    }

    public static class Geo extends GeoLayerRenderer<AbstractSpellCastingMob> {
        public Geo(IGeoRenderer entityRenderer) {
            super(entityRenderer);
        }

        @Override
        public void render(MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, AbstractSpellCastingMob entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            var syncedSpellData = ClientMagicData.getSyncedSpellData(entity);

            //irons_spellbooks.LOGGER.debug("GeoChargeSpellLayer.render: {}", syncedSpellData);
            var spellId = syncedSpellData.getCastingSpellId();
            var modelResource = entityRenderer.getGeoModelProvider().getModelResource(entity);
            var model = entityRenderer.getGeoModelProvider().getModel(modelResource);
            var bone = model.getBone(DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT).get();
            poseStack.pushPose();
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, model.getBone("right_arm").get());
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
            //poseStack.translate(0,bone.getPivotY()/2/16,0);
            var arm = getArmFromUseHand(entity);
            //TODO: hold on... were still rotating around the right arm regardless...
            boolean flag = arm == HandSide.LEFT;


            if (spellId.equals(SpellRegistry.LIGHTNING_LANCE_SPELL.get().getSpellId())) {
                poseStack.translate(-(((flag ? -1 : 1) / 32.0F) - .125), .5, 0);
                poseStack.translate(0, -bone.getPivotY() / 16, 0);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
                LightningLanceRenderer.renderModel(poseStack, bufferSource, entity.tickCount);
            } else if (spellId.equals(SpellRegistry.MAGIC_ARROW_SPELL.get().getSpellId())) {
                poseStack.translate(0, -bone.getPivotY() / 16, 0);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                poseStack.translate(-((flag ? -1 : 1) / 32.0F), .5, -.55);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                MagicArrowRenderer.renderModel(poseStack, bufferSource);
            } else if (spellId.equals(SpellRegistry.POISON_ARROW_SPELL.get().getSpellId())) {
                poseStack.translate(0, -bone.getPivotY() / 16, 0);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                poseStack.translate(-((flag ? -1 : 1) / 32.0F), .5, -.55);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                PoisonArrowRenderer.renderModel(poseStack, bufferSource, packedLight);
            }
            poseStack.popPose();
        }
    }

    public static HandSide getArmFromUseHand(LivingEntity livingEntity) {
        return livingEntity.getUsedItemHand() == Hand.MAIN_HAND ? livingEntity.getMainArm() : livingEntity.getMainArm().getOpposite();
    }
}
