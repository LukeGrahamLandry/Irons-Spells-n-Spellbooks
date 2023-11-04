package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.List;

public class GeoHumanoidRenderer<T extends MobEntity & IAnimatable> extends ExtendedGeoEntityRenderer<T> {
    private ResourceLocation textureResource;

    public GeoHumanoidRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> model) {
        super(renderManager, model);
        this.shadowRadius = 0.5f;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String boneName, T animatable) {
        if ("bipedCape".equals(boneName))
            return EntityResources.EXTENDED_CAPE_TEXTURE;

        return modelProvider.getTextureLocation(animatable);
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
//        boolean f = bone.getName().startsWith("armor") || bone.getName().equals(GenericCustomArmorRenderer.leggingTorsoLayerBone);
//        IronsSpellbooks.LOGGER.debug("GeoHumanoidRenderer.isArmorBone: {} - {}",bone.getName(),f);

        return bone.getName().startsWith("armor");
    }

    @Override
    protected ModelRenderer getArmorPartForBone(String name, BipedModel<?> armorModel) {
        switch (name) {
            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT:
            case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT:
                return armorModel.leftLeg;
            case DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT:
            case DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT:
                return armorModel.rightLeg;
            case DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT:
                return armorModel.rightArm;
            case DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT:
                return armorModel.leftArm;
            case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT:
                return armorModel.body;
            case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT:
                return armorModel.head;
            default:
                return null;
        }
    }

    @Override
    protected void prepareArmorPositionAndScale(GeoBone bone, ObjectList<ModelRenderer.ModelBox> cubeList, ModelRenderer sourceLimb, MatrixStack poseStack, boolean geoArmor, boolean modMatrixRot) {
        if (bone.getName().equals(GenericCustomArmorRenderer.leggingTorsoLayerBone)) {
            IronsSpellbooks.LOGGER.debug("GeoHumanoidRenderer: attempting to prepare leggingTorsoLayer");
            super.prepareArmorPositionAndScale((GeoBone) this.modelProvider.getBone(DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT), cubeList, sourceLimb, poseStack, false, modMatrixRot);
        } else {
            super.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, poseStack, false, modMatrixRot);
        }
    }

    @Override
    protected EquipmentSlotType getEquipmentSlotForArmorBone(String boneName, T currentEntity) {
        switch (boneName) {
            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT:
            case DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT:
                return EquipmentSlotType.FEET;
            case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT:
            case DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT:
                return EquipmentSlotType.LEGS;
            case DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT:
                return !currentEntity.isLeftHanded() ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
            case DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT:
                return currentEntity.isLeftHanded() ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
            case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT:
                return EquipmentSlotType.CHEST;
            case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT:
                return EquipmentSlotType.HEAD;
            default:
                return null;
        }
    }

    @Override
    protected ItemStack getArmorForBone(String boneName, T currentEntity) {
        switch (boneName) {
            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT:
            case DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT:
                return currentEntity.getItemBySlot(EquipmentSlotType.FEET);
            case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT:
            case DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT:
                return currentEntity.getItemBySlot(EquipmentSlotType.LEGS);
            case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT:
            case DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT:
                return currentEntity.getItemBySlot(EquipmentSlotType.CHEST);
            case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT:
                return currentEntity.getItemBySlot(EquipmentSlotType.HEAD);
            default:
                return null;
        }
    }

//    @Override
//    protected void setLimbBoneVisible(GeoArmorRenderer<? extends GeoArmorItem> armorRenderer, ModelPart limb, HumanoidModel<?> armorModel, EquipmentSlot slot) {
//        super.setLimbBoneVisible(armorRenderer, limb, armorModel, slot);
//        IBone gbBootL = armorRenderer.getGeoModelProvider().getBone(GenericCustomArmorRenderer.leggingTorsoLayerBone);
//        gbBootL.setHidden(true);
//        if (limb == armorModel.body) {
//            if (slot == EquipmentSlot.LEGS) {
//                gbBootL.setHidden(false);
//            }
//            return;
//        }
//    }


    private static final EquipmentSlotType[] SLOTS = {EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD};

    @Override
    protected void handleArmorRenderingForBone(GeoBone bone, MatrixStack stack, IVertexBuilder buffer, int packedLight, int packedOverlay, ResourceLocation currentTexture) {
        super.handleArmorRenderingForBone(bone, stack, buffer, packedLight, packedOverlay, currentTexture);
        for (EquipmentSlotType slot : SLOTS)
            if (currentEntityBeingRendered.getItemBySlot(slot).getItem() instanceof GeoArmorItem) {
                GeoArmorItem geoArmorItem = (GeoArmorItem) currentEntityBeingRendered.getItemBySlot(slot).getItem();
                if(GeoArmorRenderer.getRenderer(geoArmorItem.getClass(), this.currentEntityBeingRendered) instanceof GenericCustomArmorRenderer<?>){
                    GenericCustomArmorRenderer<?> armorRenderer = (GenericCustomArmorRenderer<?>) GeoArmorRenderer.getRenderer(geoArmorItem.getClass(), this.currentEntityBeingRendered);

                }
                //HumanoidModel<?> armorModel = (HumanoidModel<?>) geoArmorRenderer;
            }

    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, T entity) {
        switch (boneName) {
            case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
                return entity.isLeftHanded() ? entity.getMainHandItem() : entity.getOffhandItem();
            case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
                return entity.isLeftHanded() ? entity.getOffhandItem() : entity.getMainHandItem();
            default:
                return null;
        }
    }

    @Override
    protected ItemCameraTransforms.TransformType getCameraTransformForItemAtBone(ItemStack stack, String boneName) {
        // Do Defaults
        switch (boneName) {
            case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
            case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
                return ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
            default:
                return ItemCameraTransforms.TransformType.NONE;
        }
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName, T animatable) {
        return null;
    }

    @Override
    protected void preRenderItem(MatrixStack poseStack, ItemStack itemStack, String boneName, T animatable, IBone bone) {
        ItemStack mainHandItem = animatable.getMainHandItem();
        ItemStack offHandItem = animatable.getOffhandItem();
        poseStack.translate(0, 0, -0.0625);
        poseStack.translate(0, -0.0625, 0);

        if (itemStack == mainHandItem) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f));

            if (itemStack.getItem() instanceof ShieldItem)
                poseStack.translate(0, 0.125, -0.25);
        } else if (itemStack == offHandItem) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f));

            if (itemStack.getItem() instanceof ShieldItem) {
                poseStack.translate(0, 0.125, 0.25);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
            }
        }
    }

//    @Override
//    protected void handleItemAndBlockBoneRendering(PoseStack poseStack, GeoBone bone, @Nullable ItemStack boneItem, @Nullable BlockState boneBlock, int packedLight, int packedOverlay) {
//        IronsSpellbooks.LOGGER.debug("{}",bone!=null?bone.getName():"null bone");
//        super.handleItemAndBlockBoneRendering(poseStack, bone, boneItem, boneBlock, packedLight, packedOverlay);
//    }

    @Override
    protected void preRenderBlock(MatrixStack poseStack, BlockState state, String boneName, T animatable) {

    }

    @Override
    protected void postRenderItem(MatrixStack poseStack, ItemStack stack, String boneName, T animatable, IBone bone) {

    }

    @Override
    protected void postRenderBlock(MatrixStack poseStack, BlockState state, String boneName, T animatable) {

    }

    @Override
    public Color getRenderColor(T animatable, float partialTick, MatrixStack poseStack, @Nullable IRenderTypeBuffer bufferSource, @Nullable IVertexBuilder buffer, int packedLight) {
        return animatable.isInvisible() ? Color.ofRGBA(1f, 1f, 1f, .3f) : super.getRenderColor(animatable, partialTick, poseStack, bufferSource, buffer, packedLight);
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTick, MatrixStack poseStack, @Nullable IRenderTypeBuffer bufferSource, @Nullable IVertexBuilder buffer, int packedLight, ResourceLocation texture) {
        return animatable.isInvisible() ? RenderType.entityTranslucent(texture) : RenderType.entityCutout(texture);
    }
}