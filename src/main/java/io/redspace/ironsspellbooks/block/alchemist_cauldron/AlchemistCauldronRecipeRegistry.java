package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import com.google.common.collect.ImmutableList;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class AlchemistCauldronRecipeRegistry {
    private static final List<AlchemistCauldronRecipe> recipes = new ArrayList<>();

    static {
        //No cool recipes for right now :(
        //IronsSpellbooks.LOGGER.debug("creating custom cauldron recipes");
        //recipes.add(new AlchemistCauldronRecipe(ItemRegistry.BLOOD_VIAL.get(), ItemRegistry.HOGSKIN.get(), ItemRegistry.ARCANE_ESSENCE.get()).setBaseRequirement(4).setResultLimit(1));
        //recipes.add(new AlchemistCauldronRecipe(ItemRegistry.INK_EPIC.get(), Items.OBSIDIAN, Items.CRYING_OBSIDIAN));
        //recipes.add(new AlchemistCauldronRecipe(ItemRegistry.INK_LEGENDARY.get(), Items.BLUE_ORCHID, Items.DANDELION).setBaseRequirement(2).setResultLimit(2));
    }

    /**
     * If any modder is crazy enough to want to use this, do it during FMLCommonSetup Event
     */
    public static AlchemistCauldronRecipe addRecipe(AlchemistCauldronRecipe recipe) {
        recipes.add(recipe);
        return recipe;
    }


    /**
     * Searches through registered recipes, and returns the resulting item or ItemStack.EMPTY if there are no matches.
     * It is expected for input to have a consolidated count, and the result can have a count > 1
     */
    public static ItemStack getOutput(ItemStack input, ItemStack ingredient, boolean consumeOnSucces) {
        if (input.isEmpty() || ingredient.isEmpty()) return ItemStack.EMPTY;
        for (AlchemistCauldronRecipe recipe : recipes) {
            ItemStack result = recipe.createOutput(input, ingredient, consumeOnSucces);
            if (!result.isEmpty()) {
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns if any cauldron recipe has this as the ingredient
     */
    public static boolean isValidIngredient(ItemStack itemStack) {
        for (AlchemistCauldronRecipe recipe : recipes) {
            if (ItemStack.isSameItemSameTags(recipe.getIngredient(), itemStack))
                return true;
        }
        return false;
    }

    /**
     * Returns if this combination of items (the count of input matters) yields a result
     */
    public static boolean hasOutput(ItemStack input, ItemStack ingredient) {
        return !getOutput(input, ingredient, false).isEmpty();
    }

    /**
     * Returns an immutable list of all "registered" recipes
     */
    public static ImmutableList<AlchemistCauldronRecipe> getRecipes() {
        return ImmutableList.copyOf(recipes);
    }
}
