package io.redspace.ironsspellbooks.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class RendEffect extends Effect {
    public static final float ARMOR_PER_LEVEL = -.05f;
    public RendEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }
}
