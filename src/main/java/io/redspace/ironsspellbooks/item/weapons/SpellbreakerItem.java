package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.registries.AttributeRegistry;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;

import java.util.Map;
import java.util.UUID;

public class SpellbreakerItem extends MagicSwordItem {
    public SpellbreakerItem(SpellType imbuedSpell, int imbuedLevel) {
        super(ItemTier.DIAMOND, 6, -2.4f, imbuedSpell, imbuedLevel,
            Map.of(
                AttributeRegistry.COOLDOWN_REDUCTION.get(), new AttributeModifier(UUID.fromString("412b5a66-2b43-4c18-ab05-6de0bb4d64d3"), "Weapon Modifier", .15, AttributeModifier.Operation.MULTIPLY_BASE)
            ),
            (new Properties()).tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).rarity(Rarity.EPIC).setISTER(() -> ClientHelper.getISTER("spellbreaker")));
    }
}
