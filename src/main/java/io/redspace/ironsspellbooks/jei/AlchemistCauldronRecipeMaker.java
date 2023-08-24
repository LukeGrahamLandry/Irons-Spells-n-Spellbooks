package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronRecipeRegistry;
import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronTile;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.spells.SpellRarity;
import io.redspace.ironsspellbooks.spells.SpellType;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class AlchemistCauldronRecipeMaker {
    private AlchemistCauldronRecipeMaker() {
        //private constructor prevents anyone from instantiating this class
    }

    public static List<AlchemistCauldronJeiRecipe> getRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        return Stream.of(
                        getScrollRecipes(vanillaRecipeFactory, ingredientManager),
                        getPotionRecipes(vanillaRecipeFactory, ingredientManager),
                        getCustomRecipes(vanillaRecipeFactory, ingredientManager))
                .flatMap(x -> x)
                .toList();
    }

    private static Stream<AlchemistCauldronJeiRecipe> getScrollRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        return Arrays.stream(SpellRarity.values())
                .map(AlchemistCauldronRecipeMaker::enumerateSpellsForRarity);
    }

    private static Stream<AlchemistCauldronJeiRecipe> getCustomRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        var recipes = AlchemistCauldronRecipeRegistry.getRecipes();
        List<ItemStack> reagents = ingredientManager.getAllItemStacks().stream()
                .filter(AlchemistCauldronRecipeRegistry::isValidIngredient)
                .toList();
        //List<ItemStack> reagents = new ArrayList<>();
        //List<ItemStack> catalysts = new ArrayList<>();
        //List<ItemStack> outputs = new ArrayList<>();
        //for(AlchemistCauldronRecipe recipe : recipes){
        //    reagents.add(recipe.getIngredient());
        //    catalysts.add(recipe.getInput());
        //    outputs.add(recipe.getResult());
        //}
        //return new AlchemistCauldronJeiRecipe(reagents, outputs, catalysts);
        return reagents.stream().map((reagentStack) -> {
            List<ItemStack> catalysts = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();
            AlchemistCauldronRecipeRegistry.getRecipes().forEach((recipe) -> {
                if (ItemStack.isSameItemSameTags(reagentStack, recipe.getIngredient())) {
                    catalysts.add(recipe.getInput());
                    ItemStack result = recipe.getResult();
                    if (result.getCount() == 4)
                        result.setCount(1);

                    outputs.add(result);
                }
            });
            return new AlchemistCauldronJeiRecipe(List.of(reagentStack), outputs, catalysts);
        });
    }

    private static Stream<AlchemistCauldronJeiRecipe> getPotionRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        if(!ServerConfigs.ALLOW_CAULDRON_BREWING.get()) {
            return Stream.of();
        }

        List<ItemStack> potionReagents = ingredientManager.getAllItemStacks().stream()
                .filter(AlchemistCauldronRecipeMaker::isIngredient)
                .toList();

        //All in one
//        List<ItemStack> inputs = new ArrayList<>();
//        List<ItemStack> catalysts = new ArrayList<>();
//        List<ItemStack> outputs = new ArrayList<>();
//        potionReagents.forEach(
//                (reagentStack) -> ingredientManager.getAllItemStacks().stream().filter((itemStack) -> BrewingRecipeRegistry.hasOutput(itemStack, reagentStack)).forEach((baseStack) -> {
//                    inputs.add(reagentStack);
//                    catalysts.add(baseStack);
//                    outputs.add(BrewingRecipeRegistry.getOutput(baseStack, reagentStack));
//                })
//        );
//        return Stream.of(new AlchemistCauldronRecipe(inputs, outputs, catalysts));

        //Grouped by reagent
        return potionReagents.stream().map((reagentStack) -> {
            List<ItemStack> catalysts = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();
            ingredientManager.getAllItemStacks().stream().filter((itemStack) -> itemStack.getItem() instanceof PotionItem && BrewingRecipeRegistry.hasOutput(itemStack, reagentStack)).forEach((baseStack) -> {
                catalysts.add(baseStack);
                outputs.add(BrewingRecipeRegistry.getOutput(baseStack, reagentStack));
            });
            return new AlchemistCauldronJeiRecipe(List.of(reagentStack), outputs, catalysts);
        });
        //Grouped by catalyst
//        List<ItemStack> potionCatalysts = ingredientManager.getAllItemStacks().stream()
//                .filter((itemStack) -> {
//                    for (ItemStack reagentStack : potionReagents)
//                        if (BrewingRecipeRegistry.hasOutput(itemStack, reagentStack))
//                            return true;
//                    return false;
//                })
//                .toList();
//        return potionCatalysts.stream().map((catalystStack) -> {
//            List<ItemStack> reagents = new ArrayList<>();
//            List<ItemStack> outputs = new ArrayList<>();
//            ingredientManager.getAllItemStacks().stream().filter((reagentStack) -> BrewingRecipeRegistry.hasOutput(catalystStack, reagentStack)).forEach((baseStack) -> {
//                //inputs.add(reagentStack);
//                reagents.add(baseStack);
//                outputs.add(BrewingRecipeRegistry.getOutput(catalystStack, baseStack));
//            });
//            return new AlchemistCauldronRecipe(reagents, outputs, List.of(catalystStack));
//        });


    }

    private static List<ItemStack> enumerateScrollLevels(SpellType spellType) {
        var scrollStack = new ItemStack(ItemRegistry.SCROLL.get());

        var scrolls = new ArrayList<ItemStack>();

        IntStream.rangeClosed(spellType.getMinLevel(), spellType.getMaxLevel())
                .forEach((spellLevel) -> {
                    scrolls.add(getScrollStack(scrollStack, spellType, spellLevel));
                });

        return scrolls;
    }

    private static AlchemistCauldronJeiRecipe enumerateSpellsForRarity(SpellRarity spellRarity) {

        var inputs = new ArrayList<ItemStack>();
        var catalysts = new ArrayList<ItemStack>();
        var outputs = new ArrayList<ItemStack>();

        Arrays.stream(SpellType.values()).filter((spellType -> spellType.isEnabled() && spellType != SpellType.NONE_SPELL)).forEach((spellType -> {
            inputs.addAll(enumerateScrollLevels(spellType).stream().filter((scrollStack) -> SpellData.getSpellData(scrollStack).getSpell().getRarity() == spellRarity).toList());
        }));

        inputs.forEach((itemStack -> {
            catalysts.add(ItemStack.EMPTY);
            outputs.add(new ItemStack(AlchemistCauldronTile.getInkFromScroll(itemStack)));
        }));

        return new AlchemistCauldronJeiRecipe(inputs, outputs, catalysts);
    }

    private static ItemStack getScrollStack(ItemStack stack, SpellType spellType, int spellLevel) {
        var scrollStack = stack.copy();
        SpellData.setSpellData(scrollStack, spellType, spellLevel);
        return scrollStack;
    }

    private static boolean isIngredient(ItemStack itemStack) {
        try {
            return PotionBrewing.isIngredient(itemStack);
        } catch (RuntimeException | LinkageError e) {
            IronsSpellbooks.LOGGER.error("Failed to check if item is a potion reagent {}.", itemStack.toString(), e);
            return false;
        }
    }
}
