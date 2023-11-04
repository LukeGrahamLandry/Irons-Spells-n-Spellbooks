package io.redspace.ironsspellbooks.entity.mobs.wizards.priest;


import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class PriestRenderer extends AbstractSpellCastingMobRenderer {
    public PriestRenderer(EntityRendererManager context) {
        super(context, new PriestModel());
    }
}
