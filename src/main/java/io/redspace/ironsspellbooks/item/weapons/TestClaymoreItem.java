package io.redspace.ironsspellbooks.item.weapons;

import net.minecraft.item.Item;

import java.util.Map;

public class TestClaymoreItem extends ExtendedSwordItem {
    public TestClaymoreItem() {
        super(ExtendedWeaponTiers.CLAYMORE, 9, -2.7, Map.of(), new Item.Properties().setISTER(() -> ClientHelper.getISTER("claymore")).stacksTo(1));
    }
}
