package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.item.Item.Properties;

public class ElectromancerArmorItem extends ExtendedArmorItem implements ArmorCapeProvider {
    public ElectromancerArmorItem(EquipmentSlotType slot, Properties settings) {
        super(ExtendedArmorMaterials.ELECTROMANCER, slot, settings);
    }

    @Override
    public ResourceLocation getCapeResourceLocation() {
        return new ResourceLocation(IronsSpellbooks.MODID, "textures/models/armor/electromancer_cape.png");
    }
}
