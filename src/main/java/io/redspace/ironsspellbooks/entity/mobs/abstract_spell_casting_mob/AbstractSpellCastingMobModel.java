package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class AbstractSpellCastingMobModel extends AnimatedGeoModel<AbstractSpellCastingMob> {

    @Override
    public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
        return AbstractSpellCastingMob.modelResource;
    }

    @Override
    public abstract ResourceLocation getTextureResource(AbstractSpellCastingMob mob);

    @Override
    public ResourceLocation getAnimationResource(AbstractSpellCastingMob animatable) {
        return AbstractSpellCastingMob.animationInstantCast;
    }

    @Override
    public void setCustomAnimations(AbstractSpellCastingMob entity, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(entity, instanceId, animationEvent);
        if (Minecraft.getInstance().isPaused() || !entity.shouldBeExtraAnimated())
            return;

        float partialTick = animationEvent.getPartialTick();
        /*
                This overrides all other animation
         */
        IBone head = this.getAnimationProcessor().getBone(PartNames.HEAD);
        IBone body = this.getAnimationProcessor().getBone(PartNames.BODY);
        IBone rightArm = this.getAnimationProcessor().getBone(PartNames.RIGHT_ARM);
        IBone leftArm = this.getAnimationProcessor().getBone(PartNames.LEFT_ARM);
        IBone rightLeg = this.getAnimationProcessor().getBone(PartNames.RIGHT_LEG);
        IBone leftLeg = this.getAnimationProcessor().getBone(PartNames.LEFT_LEG);

        /*
            Head Controls
         */
        //Make the head look forward, whatever forward is (influenced externally, such as a lootAt target)
        if (!entity.isAnimating() || entity.shouldAlwaysAnimateHead()) {
            head.setRotationY(MathHelper.lerp(partialTick,
                    MathHelper.wrapDegrees(-entity.yHeadRotO + entity.yBodyRotO) * MathHelper.DEG_TO_RAD,
                    MathHelper.wrapDegrees(-entity.yHeadRot + entity.yBodyRot) * MathHelper.DEG_TO_RAD));
            head.setRotationX(MathHelper.lerp(partialTick, -entity.xRotO, -entity.getXRot()) * MathHelper.DEG_TO_RAD);
        }
        /*
            Crazy Vanilla Magic Calculations (LivingEntityRenderer:116 & HumanoidModel#setupAnim
         */
        float pLimbSwingAmount = 0.0F;
        float pLimbSwing = 0.0F;
        if (entity.isAlive()) {
            pLimbSwingAmount = MathHelper.lerp(partialTick, entity.animationSpeedOld, entity.animationSpeed);
            pLimbSwing = entity.animationPosition - entity.animationSpeed * (1.0F - partialTick);
            if (entity.isBaby()) {
                pLimbSwing *= 3.0F;
            }

            if (pLimbSwingAmount > 1.0F) {
                pLimbSwingAmount = 1.0F;
            }
        }
        float f = 1.0F;
        if (entity.getFallFlyingTicks() > 4) {
            f = (float) entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }
        /*
            Leg Controls
         */
        if (entity.isPassenger() && entity.getVehicle().shouldRiderSit()) {
            //If we are riding something, pose ourselves sitting
            rightLeg.setRotationX(1.4137167F);
            rightLeg.setRotationY(-(float) Math.PI / 10F);
            rightLeg.setRotationZ(-0.07853982F);
            leftLeg.setRotationX(1.4137167F);
            leftLeg.setRotationY((float) Math.PI / 10F);
            leftLeg.setRotationZ(0.07853982F);
        } else if (!entity.isAnimating() || entity.shouldAlwaysAnimateLegs()) {
            //rightLeg.setRotationX(Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount / f);
            //leftLeg.setRotationX(Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount / f);
            addRotationX(rightLeg, MathHelper.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount / f);
            addRotationX(leftLeg, MathHelper.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount / f);

        }
        /*
            Arm Controls
         */
        if (!entity.isAnimating()) {
            addRotationX(rightArm, MathHelper.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 2.0F * pLimbSwingAmount * 0.5F / f);
            addRotationX(leftArm, MathHelper.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F / f);
            bobBone(rightArm, entity.tickCount, 1);
            bobBone(leftArm, entity.tickCount, -1);
            if (entity.isDrinkingPotion()) {
                addRotationX(entity.isLeftHanded() ? leftArm : rightArm, 35 * MathHelper.DEG_TO_RAD);
                addRotationZ(entity.isLeftHanded() ? leftArm : rightArm, (entity.isLeftHanded() ? 15 : -15) * MathHelper.DEG_TO_RAD);
                addRotationY(entity.isLeftHanded() ? leftArm : rightArm, (entity.isLeftHanded() ? -25 : 25) * MathHelper.DEG_TO_RAD);
            }
        } else if (entity.shouldPointArmsWhileCasting()) {
            addRotationX(rightArm, -entity.getXRot() * MathHelper.DEG_TO_RAD);
            addRotationX(leftArm, -entity.getXRot() * MathHelper.DEG_TO_RAD);
        }

//        rightArm.setRotationX(Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 2.0F * pLimbSwingAmount * 0.5F / f);
//        leftArm.setRotationX(Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F / f);

        //rightLeg.yRot = 0.0F;
        //leftLeg.yRot = 0.0F;
        //rightLeg.zRot = 0.0F;
        //leftLeg.zRot = 0.0F;

    }

    protected void bobBone(IBone bone, int offset, float multiplier) {
        //Copied from AnimationUtils#bobLimb
        float z = multiplier * (MathHelper.cos(offset * 0.09F) * 0.05F + 0.05F);
        float x = multiplier * MathHelper.sin(offset * 0.067F) * 0.05F;
        addRotationZ(bone, z);
        addRotationX(bone, x);
        //bone.setRotationX(bone.getRotationX() + x);
        //bone.setRotationZ(bone.getRotationZ() + z);

    }

    protected void addRotationX(IBone bone, float rotation) {
        bone.setRotationX(wrapRadians(bone.getRotationX() + rotation));
    }

    protected void addRotationZ(IBone bone, float rotation) {
        bone.setRotationZ(wrapRadians(bone.getRotationZ() + rotation));
    }

    protected void addRotationY(IBone bone, float rotation) {
        bone.setRotationY(wrapRadians(bone.getRotationY() + rotation));
    }

    public static float wrapRadians(float pValue) {
        float twoPi = 6.2831f;
        float pi = 3.14155f;
        float f = pValue % twoPi;
        if (f >= pi) {
            f -= twoPi;
        }

        if (f < -pi) {
            f += twoPi;
        }

        return f;
    }
}