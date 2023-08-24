package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;

public class PoisonwardRing extends SimpleDescriptiveCurio {
    public PoisonwardRing() {
        super(new Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), "ring");
    }
    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        super.curioTick(identifier, index, livingEntity, stack);
        livingEntity.removeEffect(Effects.POISON);
    }
}
