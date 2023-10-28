package io.redspace.ironsspellbooks.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.item.curios.AffinityRing;
import io.redspace.ironsspellbooks.api.item.curios.RingData;
import io.redspace.ironsspellbooks.registries.LootRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;

public class RandomizeRingEnhancementFunction extends LootFunction {
    final SpellFilter spellFilter;

    protected RandomizeRingEnhancementFunction(ILootCondition[] lootConditions, SpellFilter spellFilter) {
        super(lootConditions);
        this.spellFilter = spellFilter;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        //irons_spellbooks.LOGGER.debug("RandomizeScrollFunction.run {}", itemStack.hashCode());
        if (itemStack.getItem() instanceof AffinityRing) {
            RingData.setRingData(itemStack, spellFilter.getRandomSpell(lootContext.getRandom(), (spell -> spell.isEnabled() && spell != SpellRegistry.none())));
        }
        return itemStack;
    }

    @Override
    public LootFunctionType getType() {
        return LootRegistry.RANDOMIZE_SPELL_RING_FUNCTION.get();
    }

    public static class Serializer extends LootFunction.Serializer<RandomizeRingEnhancementFunction> {
        public void serialize(JsonObject json, RandomizeRingEnhancementFunction scrollFunction, JsonSerializationContext jsonDeserializationContext) {
            super.serialize(json, scrollFunction, jsonDeserializationContext);
        }

        public RandomizeRingEnhancementFunction deserialize(JsonObject json, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] lootConditions) {
            var applicableSpells = SpellFilter.deserializeSpellFilter(json);
            return new RandomizeRingEnhancementFunction(lootConditions, applicableSpells);
        }
    }
}
