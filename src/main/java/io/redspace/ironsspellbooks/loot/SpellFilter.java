package io.redspace.ironsspellbooks.loot;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpellFilter {
    SchoolType schoolType = null;
    List<AbstractSpell> spells = new ArrayList<>();

    public SpellFilter(SchoolType schoolType) {
        this.schoolType = schoolType;
    }

    public SpellFilter(List<AbstractSpell> spells) {
        this.spells = spells;
    }

    public SpellFilter() {
    }

    public boolean isFiltered() {
        return schoolType != null || spells.size() > 0;
    }

    public List<AbstractSpell> getApplicableSpells() {
        if (spells.size() > 0)
            return spells;
        else if (schoolType != null)
            return SpellRegistry.getSpellsForSchool(schoolType);
        else
            return SpellRegistry.REGISTRY.get().getValues().stream().toList();
    }

    public AbstractSpell getRandomSpell(RandomSource random, Predicate<AbstractSpell> filter) {
        //Will throw a non fatal error if the filter empties the list
        List<AbstractSpell> spells = getApplicableSpells().stream().filter(filter).toList();
        return spells.get(random.nextInt(spells.size()));
    }

    public static SpellFilter deserializeSpellFilter(JsonObject json) {
        if (JSONUtils.isValidNode(json, "school")) {
            String schoolType = JSONUtils.getAsString(json, "school");
            return new SpellFilter(SchoolRegistry.getSchool(new ResourceLocation(schoolType)));
        } else if (JSONUtils.isArrayNode(json, "spells")) {
            JsonArray spellsFromJson = JSONUtils.getAsJsonArray(json, "spells");
            List<AbstractSpell> applicableSpellList = new ArrayList<>();
            for (JsonElement element : spellsFromJson) {
                String spellId = element.getAsString();

                AbstractSpell spell = SpellRegistry.getSpell(spellId);

                if (spell != SpellRegistry.none()) {
                    applicableSpellList.add(spell);
                }
            }
            return new SpellFilter(applicableSpellList);
        } else {
            return new SpellFilter();
//                var nonVoidSpells = new SpellType[SpellType.values().length - SpellType.getSpellsFromSchool(SchoolType.VOID).length];
//                int j = 0;
//                for (int i = 0; i < nonVoidSpells.length; i++) {
//                    if (SpellType.values()[i].getSchoolType() != SchoolType.VOID) {
//                        nonVoidSpells[j++] = SpellType.values()[i];
//                    }
//                }
//                return nonVoidSpells;
        }
    }
}