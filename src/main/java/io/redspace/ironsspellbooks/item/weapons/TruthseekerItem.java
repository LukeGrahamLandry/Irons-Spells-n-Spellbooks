package io.redspace.ironsspellbooks.item.weapons;

import net.minecraft.item.Rarity;

import java.util.Map;

public class TruthseekerItem extends ExtendedSwordItem {
    public TruthseekerItem() {
        super(ExtendedWeaponTiers.TRUTHSEEKER, 11, -3, Map.of(), new Properties().setISTER(() -> ClientHelper.getISTER("truthseeker")).stacksTo(1).rarity(Rarity.UNCOMMON));
    }
}
