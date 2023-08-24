package io.redspace.ironsspellbooks.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.item.IItemTier;

import java.util.Map;

import net.minecraft.item.Item.Properties;

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
