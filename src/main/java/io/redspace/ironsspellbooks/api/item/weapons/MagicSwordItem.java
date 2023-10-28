package io.redspace.ironsspellbooks.api.item.weapons;

import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.IItemTier;

import java.util.Map;

import net.minecraft.item.Item.Properties;

public class MagicSwordItem extends ExtendedSwordItem {

    private final SpellDataRegistryHolder holder;

    public AbstractSpell getImbuedSpell() {
        return holder.getSpellData().getSpell();
    }

    public int getImbuedLevel() {
        return holder.getSpellData().getLevel();
    }

    public MagicSwordItem(IItemTier tier, double attackDamage, double attackSpeed, SpellDataRegistryHolder holder, Map<Attribute, AttributeModifier> additionalAttributes, Properties properties) {
        super(tier, attackDamage, attackSpeed, additionalAttributes, properties);
        this.holder = holder;
    }

}
