package io.redspace.ironsspellbooks.entity.mobs.keeper;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.client.Minecraft;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.DefaultBipedBoneIdents.PartNames;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;

public class KeeperModel extends AbstractSpellCastingMobModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/keeper/keeper.png");
    public static final ResourceLocation modelResource = new ResourceLocation(IronsSpellbooks.MODID, "geo/citadel_keeper.geo.json");

    @Override
    public ResourceLocation getTextureLocation(AbstractSpellCastingMob object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getModelLocation(AbstractSpellCastingMob object) {
        return modelResource;
    }

    @Override
    public void setCustomAnimations(AbstractSpellCastingMob entity, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(entity, instanceId, animationEvent);
        if (Minecraft.getInstance().isPaused())
            return;

        float partialTick = animationEvent.getPartialTick();

        IBone rightLeg = this.getAnimationProcessor().getBone(PartNames.RIGHT_LEG);
        IBone leftLeg = this.getAnimationProcessor().getBone(PartNames.LEFT_LEG);
        IBone rightArm = this.getAnimationProcessor().getBone(PartNames.RIGHT_ARM);
        IBone leftArm = this.getAnimationProcessor().getBone(PartNames.LEFT_ARM);
        IBone body = this.getAnimationProcessor().getBone(PartNames.BODY);

        boolean tick = lastTick != entity.tickCount;
        lastTick = entity.tickCount;

        float pLimbSwingAmount = 0.0F;
        float pLimbSwing = 0.0F;
        if (entity.isAlive()) {
            pLimbSwingAmount = MathHelper.lerp(partialTick, entity.animationSpeedOld, entity.animationSpeed);
            pLimbSwing = entity.animationPosition - entity.animationSpeed * (1.0F - partialTick);
            //pLimbSwingAmount *= .75f;
            //pLimbSwing *= .75f;
            if (pLimbSwingAmount > 1.0F) {
                pLimbSwingAmount = 1.0F;
            }
            if (entity.hurtTime > 0) {
                pLimbSwingAmount *= .25f;
            }
        }
        if (!(entity.isPassenger() && entity.getVehicle().shouldRiderSit())) {
            float strength = .75f;
            updatePosition(rightLeg, 0, MathHelper.cos(pLimbSwing * 0.6662F) * 4 * strength * pLimbSwingAmount, -MathHelper.sin(pLimbSwing * 0.6662F) * 4 * pLimbSwingAmount);
            updatePosition(leftLeg, 0, MathHelper.cos(pLimbSwing * 0.6662F - ((float) Math.PI)) * 4 * strength * pLimbSwingAmount, -MathHelper.sin(pLimbSwing * 0.6662F - ((float) Math.PI)) * 4 * pLimbSwingAmount);
            updatePosition(body, 0, MathHelper.cos(pLimbSwing * 1.2662F - ((float) Math.PI) * .5f) * 1 * strength * pLimbSwingAmount, 0);
            if (tick) {
                if (!entity.isAnimating() || entity.shouldAlwaysAnimateLegs()) {
                    legTween = MathHelper.lerp(.9f, 0, 1);
                } else {
                    legTween = MathHelper.lerp(.9f, 1, 0);
                }
            }
            rightLeg.setRotationX(MathHelper.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount * legTween * strength);
            leftLeg.setRotationX(MathHelper.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount * legTween * strength);
        }
        if (entity.isAnimating()){
            bobBone(rightArm, entity.tickCount, 1);
            bobBone(leftArm, entity.tickCount, -1);
        }
    }

    private int lastTick;
    private float legTween = 1f;

    private static void updatePosition(IBone bone, float x, float y, float z) {
        bone.setPositionX(x);
        bone.setPositionY(y);
        bone.setPositionZ(z);
    }

    private static void updateRotation(IBone bone, float x, float y, float z) {
        bone.setRotationX(x);
        bone.setRotationY(y);
        bone.setRotationZ(z);
    }
}