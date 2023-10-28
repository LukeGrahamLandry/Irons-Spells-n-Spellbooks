package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ModTags;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * - Upgrade scroll: (scroll level x) + (scroll level x) = (scroll level x+1)
 * - Imbue Weapon:   weapon + scroll = imbued weapon with spell/level of scroll
 * - Upgrade item:   item + upgrade orb =
 **/
public final class ScrollForgeRecipeMaker {
    private static final class FocusToSchool {
        private final Item item;
        private final SchoolType schoolType;

            public FocusToSchool(Item item, SchoolType schoolType) {
                this.item = item;
                this.schoolType = schoolType;
            }

        public Item item() {
            return item;
        }

        public SchoolType schoolType() {
            return schoolType;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (FocusToSchool) obj;
            return Objects.equals(this.item, that.item) &&
                    Objects.equals(this.schoolType, that.schoolType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, schoolType);
        }

        @Override
        public String toString() {
            return "FocusToSchool[" +
                    "item=" + item + ", " +
                    "schoolType=" + schoolType + ']';
        }

        }

    private ScrollForgeRecipeMaker() {
        //private constructor prevents anyone from instantiating this class
    }

    public static List<ScrollForgeRecipe> getRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        List<InkItem> inkItems = ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof InkItem).map(item -> (InkItem) item).toList();
        Stream<ScrollForgeRecipe> recipes = ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item.builtInRegistryHolder().is(ModTags.SCHOOL_FOCUS))
                .map(item -> {
                    ItemStack paperInput = new ItemStack(Items.PAPER);
                    ItemStack focusInput = new ItemStack(item);
                    SchoolType school = SchoolRegistry.getSchoolFromFocus(focusInput);
                    List<AbstractSpell> spells = SpellRegistry.getSpellsForSchool(school);
                    ArrayList<ItemStack> scrollOutputs = new ArrayList<ItemStack>();
                    ArrayList<ItemStack> inkOutputs = new ArrayList<ItemStack>();

                    inkItems.forEach(ink -> {
                        //var string = new StringBuilder();
                        //SpellRegistry.REGISTRY.get().getValues().forEach((AbstractSpell)-> string.append(AbstractSpell.getSpellId()).append(", "));
                        for (AbstractSpell spell : spells) {
                            if (spell.isEnabled()) {
                                int spellLevel = spell.getMinLevelForRarity(ink.getRarity());
                                if (spellLevel > 0 && spell != SpellRegistry.none()) {
                                    inkOutputs.add(new ItemStack(ink));
                                    scrollOutputs.add(getScrollStack(spell, spell.getLevel(spellLevel, null)));
                                }
                            }
                        }
                    });

                    return new ScrollForgeRecipe(inkOutputs, paperInput, focusInput, scrollOutputs);
                });

        return recipes.toList();
    }

    private static ItemStack getScrollStack(AbstractSpell spell, int spellLevel) {
        ItemStack scrollStack = new ItemStack(ItemRegistry.SCROLL.get());
        SpellData.setSpellData(scrollStack, spell, spellLevel);
        return scrollStack;
    }
}
