package io.redspace.ironsspellbooks.api.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.SwordItem;
import net.minecraft.item.IItemTier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import net.minecraft.item.Item.Properties;

public class ExtendedSwordItem extends SwordItem {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ExtendedSwordItem(IItemTier tier, double attackDamage, double attackSpeed, Map<Attribute, AttributeModifier> additionalAttributes, Properties properties) {
        super(tier, 3, -2.4f, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        for (Map.Entry<Attribute, AttributeModifier> modifierEntry : additionalAttributes.entrySet()) {
            builder.put(modifierEntry.getKey(), modifierEntry.getValue());
        }
        this.defaultModifiers = builder.build();
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlotType pEquipmentSlot) {
        return pEquipmentSlot == EquipmentSlotType.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }
}
