package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.TarnishedCrownArmorItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TarnishedCrownModel extends AnimatedGeoModel<TarnishedCrownArmorItem> {

    public TarnishedCrownModel(){
        super();

    }
    @Override
    public ResourceLocation getModelResource(TarnishedCrownArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "geo/tarnished_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TarnishedCrownArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "textures/models/armor/tarnished.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TarnishedCrownArmorItem animatable) {
        return new ResourceLocation(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}