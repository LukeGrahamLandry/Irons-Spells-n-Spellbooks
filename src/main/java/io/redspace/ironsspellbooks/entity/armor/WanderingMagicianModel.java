package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.WanderingMagicianArmorItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WanderingMagicianModel extends AnimatedGeoModel<WanderingMagicianArmorItem> {

    public WanderingMagicianModel(){
        super();

    }
    @Override
    public ResourceLocation getModelResource(WanderingMagicianArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "geo/wandering_magician_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WanderingMagicianArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "textures/models/armor/wandering_magician.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WanderingMagicianArmorItem animatable) {
        return new ResourceLocation(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}