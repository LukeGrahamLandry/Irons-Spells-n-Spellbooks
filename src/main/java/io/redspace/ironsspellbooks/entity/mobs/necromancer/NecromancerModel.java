package io.redspace.ironsspellbooks.entity.mobs.necromancer;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.util.ResourceLocation;

public class NecromancerModel extends AbstractSpellCastingMobModel {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/necromancer.png");
    private static final ResourceLocation MODEL = new ResourceLocation(IronsSpellbooks.MODID, "geo/skeleton_mob.geo.json");

    @Override
    public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
        return TEXTURE;
    }
    @Override
    public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
        return MODEL;
    }
}