package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.registries.AttributeRegistry;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

import java.util.Map;
import java.util.UUID;

public class MagehunterItem extends ExtendedSwordItem {

    public MagehunterItem() {
        super(ExtendedWeaponTiers.METAL_MAGEHUNTER, 6, -2.4f,
                /*SpellType.COUNTERSPELL_SPELL, 1,*/
                Map.of(
                        AttributeRegistry.SPELL_RESIST.get(), new AttributeModifier(UUID.fromString("412b5a66-2b43-4c18-ab05-6de0bb4d64d3"), "Weapon Modifier", .25, AttributeModifier.Operation.MULTIPLY_BASE)
                ),
                (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).rarity(Rarity.UNCOMMON).setISTER(() -> ClientHelper.getISTER("magehunter")));
    }
}
