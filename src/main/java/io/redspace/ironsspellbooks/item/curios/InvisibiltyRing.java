package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.Style;
import net.minecraft.item.Item;

public class InvisibiltyRing extends SimpleDescriptiveCurio {
    public InvisibiltyRing() {
        super(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), "ring");
        this.descriptionStyle = Style.EMPTY.withColor(TextFormatting.GRAY).withItalic(true);
    }
}
