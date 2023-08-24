package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.IItemTier;

import java.util.Map;

public class MagicSwordItem extends ExtendedSwordItem {

    private final SpellType imbuedSpell;
    private final int imbuedLevel;

    public SpellType getImbuedSpell() {
        return imbuedSpell;
    }

    public int getImbuedLevel() {
        return imbuedLevel;
    }

    public MagicSwordItem(IItemTier tier, double attackDamage, double attackSpeed, SpellType imbuedSpell, int imbuedLevel, Map<Attribute, AttributeModifier> additionalAttributes, Properties properties) {
        super(tier, attackDamage, attackSpeed, additionalAttributes, properties);
        this.imbuedSpell = imbuedSpell;
        this.imbuedLevel = imbuedLevel;
    }

}
