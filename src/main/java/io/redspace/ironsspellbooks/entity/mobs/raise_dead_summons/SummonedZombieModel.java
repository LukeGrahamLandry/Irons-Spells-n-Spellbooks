package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SummonedZombieModel extends AnimatedGeoModel<SummonedZombie> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(IronsSpellbooks.MODID,"textures/entity/summoned_zombie.png");
    public static final ResourceLocation MODEL = new ResourceLocation(IronsSpellbooks.MODID, "geo/abstract_casting_mob.geo.json");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(IronsSpellbooks.MODID, "animations/casting_animations.json");


    @Override
    public ResourceLocation getTextureResource(SummonedZombie object) {
        return TEXTURE;
    }
    @Override
    public ResourceLocation getModelResource(SummonedZombie object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(SummonedZombie animatable) {
        return ANIMATIONS;
    }

}