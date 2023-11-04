package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;

public class DeadKingModel extends AbstractSpellCastingMobModel {
    public static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king.png");
    public static final ResourceLocation TEXTURE_CORPSE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king_resting.png");
    public static final ResourceLocation TEXTURE_ENRAGED = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king_enraged.png");
    public static final ResourceLocation MODEL = new ResourceLocation(IronsSpellbooks.MODID, "geo/dead_king.geo.json");

    public DeadKingModel() {
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractSpellCastingMob object) {
        if (object instanceof DeadKingBoss) {
            DeadKingBoss boss = (DeadKingBoss) object;
            if (boss.isPhase(DeadKingBoss.Phases.FinalPhase))
                return TEXTURE_ENRAGED;
            else
                return TEXTURE_NORMAL;
        } else
            return TEXTURE_CORPSE;
    }

    @Override
    public ResourceLocation getModelLocation(AbstractSpellCastingMob object) {
        return MODEL;
    }

    @Override
    public void setCustomAnimations(AbstractSpellCastingMob entity, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(entity, instanceId, animationEvent);
        IBone jaw = this.getAnimationProcessor().getBone("jaw");
        IBone hair1 = this.getAnimationProcessor().getBone("hair");
        IBone hair2 = this.getAnimationProcessor().getBone("hair2");

        float f = entity.tickCount + animationEvent.getPartialTick();
        //Builtin Resource Pack does not contain these bones
        if (jaw == null || hair1 == null || hair2 == null)
            return;

        jaw.setRotationX(MathHelper.sin(f * .05f) * 5 * Utils.DEG_TO_RAD);
        hair1.setRotationX((MathHelper.sin(f * .1f) * 10 - 30) * Utils.DEG_TO_RAD);
        hair2.setRotationX(MathHelper.sin(f * .15f) * 15 * Utils.DEG_TO_RAD);

    }


}