package io.redspace.ironsspellbooks.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class SpiderAspectEffect extends Effect {
    public static final float DAMAGE_PER_LEVEL = .05f;
    public SpiderAspectEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }
}
