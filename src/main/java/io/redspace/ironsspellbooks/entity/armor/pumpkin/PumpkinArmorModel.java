package io.redspace.ironsspellbooks.entity.armor.pumpkin;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.PumpkinArmorItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PumpkinArmorModel extends AnimatedGeoModel<PumpkinArmorItem> {

    public PumpkinArmorModel(){
        super();

    }
    @Override
    public ResourceLocation getModelResource(PumpkinArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "geo/pumpkin_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PumpkinArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "textures/models/armor/pumpkin.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PumpkinArmorItem animatable) {
        return new ResourceLocation(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}