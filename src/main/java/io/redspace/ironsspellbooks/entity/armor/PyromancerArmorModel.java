package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.PyromancerArmorItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PyromancerArmorModel extends AnimatedGeoModel<PyromancerArmorItem> {

    public PyromancerArmorModel(){
        super();

    }
    @Override
    public ResourceLocation getModelResource(PyromancerArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "geo/pyromancer_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PyromancerArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "textures/models/armor/pyromancer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PyromancerArmorItem animatable) {
        return new ResourceLocation(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}