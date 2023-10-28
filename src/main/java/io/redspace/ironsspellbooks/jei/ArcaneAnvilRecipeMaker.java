package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * - Upgrade scroll: (scroll level x) + (scroll level x) = (scroll level x+1)
 * - Imbue Weapon:   weapon + scroll = imbued weapon with spell/level of scroll
 * - Upgrade item:   item + upgrade orb =
 **/
public final class ArcaneAnvilRecipeMaker {
    private ArcaneAnvilRecipeMaker() {
        //private constructor prevents anyone from instantiating this class
    }

    public static List<ArcaneAnvilRecipe> getRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        return Stream.of(
                        getScrollRecipes(vanillaRecipeFactory, ingredientManager),
                        getImbueRecipes(vanillaRecipeFactory, ingredientManager),
                        getUpgradeRecipes(vanillaRecipeFactory, ingredientManager))
                .flatMap(x -> x)
                .toList();
    }

    private static Stream<ArcaneAnvilRecipe> getScrollRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        return SpellRegistry.REGISTRY.get().getValues().stream()
                .filter(spell -> spell != SpellRegistry.none() && spell.isEnabled())
                .sorted(Comparator.comparing(AbstractSpell::getSpellId))
                .map(ArcaneAnvilRecipeMaker::enumerateScrollCombinations)
                .filter(ArcaneAnvilRecipe::isValid); //Filter out any blank recipes created where min and max spell level are equal
    }

    private static Stream<ArcaneAnvilRecipe> getImbueRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        var scrollStack = new ItemStack(ItemRegistry.SCROLL.get());
        var leftInputs = new ArrayList<ItemStack>();
        var rightInputs = new ArrayList<ItemStack>();
        var outputs = new ArrayList<ItemStack>();

        SpellRegistry.REGISTRY.get().getValues().stream()
                .filter(spell -> spell != SpellRegistry.none() && spell.isEnabled())
                .sorted(Comparator.comparing(AbstractSpell::getSpellId))
                .forEach((spellType) -> {
                    Registry.ITEM.stream().filter((k) -> k instanceof SwordItem && k.getItemCategory() != null).forEach((swordItem) -> {
                        var inputSwordStack = new ItemStack(swordItem);
                        IntStream.rangeClosed(spellType.getMinLevel(), spellType.getMaxLevel())
                                .forEach((spellLevel) -> {
                                    leftInputs.add(inputSwordStack);
                                    rightInputs.add(getScrollStack(scrollStack, spellType, spellLevel));
                                    outputs.add(getScrollStack(inputSwordStack, spellType, spellLevel));
                                });
                    });
                });

        return Stream.of(new ArcaneAnvilRecipe(leftInputs, rightInputs, outputs));
    }

    private static Stream<ArcaneAnvilRecipe> getUpgradeRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        return Stream.empty();
    }

    private static ArcaneAnvilRecipe enumerateScrollCombinations(AbstractSpell spell) {
        var scrollStack = new ItemStack(ItemRegistry.SCROLL.get());

        var leftInputs = new ArrayList<ItemStack>();
        var rightInputs = new ArrayList<ItemStack>();
        var outputs = new ArrayList<ItemStack>();

        IntStream.range(spell.getMinLevel(), spell.getMaxLevel())
                .forEach((spellLevel) -> {
                    leftInputs.add(getScrollStack(scrollStack, spell, spellLevel));
                    rightInputs.add(getScrollStack(scrollStack, spell, spellLevel));
                    outputs.add(getScrollStack(scrollStack, spell, spellLevel + 1));
                });

        return new ArcaneAnvilRecipe(leftInputs, rightInputs, outputs);
    }

    private static ItemStack getScrollStack(ItemStack stack, AbstractSpell spell, int spellLevel) {
        var scrollStack = stack.copy();
        SpellData.setSpellData(scrollStack, spell, spellLevel);
        return scrollStack;
    }
}
