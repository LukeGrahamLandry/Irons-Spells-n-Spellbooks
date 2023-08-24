package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Rarity;

import java.util.Map;
import java.util.UUID;

public class KeeperFlambergeItem extends ExtendedSwordItem {
    //TODO: custom tier
    public KeeperFlambergeItem() {
        super(ExtendedWeaponTiers.KEEPER_FLAMBERGE, 10, -2.7, Map.of(Attributes.ARMOR, new AttributeModifier(UUID.fromString("c552273e-6669-4cd2-80b3-a703b7616336"), "weapon mod", 5, AttributeModifier.Operation.ADDITION)), new Properties().setISTER(() -> ClientHelper.getISTER("keeper_flamberge")).stacksTo(1).rarity(Rarity.UNCOMMON).tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB));
    }
}
