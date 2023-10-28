package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilMenu;
import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilScreen;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeMenu;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeScreen;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MenuRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.stream.IntStream;

import net.minecraft.item.ItemStack;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    public static final ResourceLocation RECIPE_GUI_VANILLA = new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/gui_vanilla.png");
    public static final ResourceLocation ALCHEMIST_CAULDRON_GUI = new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/jei_alchemist_cauldron.png");
    public static final ResourceLocation SCROLL_FORGE_GUI = new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/scroll_forge.png");

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(IronsSpellbooks.MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ItemRegistry.SCROLL.get(), SCROLL_INTERPRETER);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new ArcaneAnvilRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ScrollForgeRecipeCategory(guiHelper));
        registration.addRecipeCategories(new AlchemistCauldronRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IIngredientManager ingredientManager = registration.getIngredientManager();
        IVanillaRecipeFactory vanillaRecipeFactory = registration.getVanillaRecipeFactory();
        registration.addRecipes(ArcaneAnvilRecipeCategory.ARCANE_ANVIL_RECIPE_RECIPE_TYPE, ArcaneAnvilRecipeMaker.getRecipes(vanillaRecipeFactory, ingredientManager));
        registration.addRecipes(ScrollForgeRecipeCategory.SCROLL_FORGE_RECIPE_RECIPE_TYPE, ScrollForgeRecipeMaker.getRecipes(vanillaRecipeFactory, ingredientManager));
        registration.addRecipes(AlchemistCauldronRecipeCategory.ALCHEMIST_CAULDRON_RECIPE_TYPE, AlchemistCauldronRecipeMaker.getRecipes(vanillaRecipeFactory, ingredientManager));
        registration.addRecipes(RecipeTypes.ANVIL, VanillaAnvilRecipeMaker.getAnvilRepairRecipes(vanillaRecipeFactory));

        SpellRegistry.REGISTRY.get().getValues().stream().forEach(spell -> {
            if (spell.isEnabled() && spell != SpellRegistry.none()) {
                ArrayList<ItemStack> list = new ArrayList<ItemStack>();
                IntStream.rangeClosed(spell.getMinLevel(), spell.getMaxLevel())
                        .forEach((spellLevel) -> {
                            ItemStack scrollStack = new ItemStack(ItemRegistry.SCROLL.get());
                            SpellData.setSpellData(scrollStack, spell, spellLevel);
                            list.add(scrollStack);
                        });
                registration.addIngredientInfo(list, VanillaTypes.ITEM_STACK, ITextComponent.translatable(String.format("%s.guide", spell.getComponentId())));
            }
        });
        registration.addItemStackInfo(new ItemStack(ItemRegistry.LIGHTNING_BOTTLE.get()), ITextComponent.translatable("item.irons_spellbooks.lightning_bottle.guide"));
        registration.addItemStackInfo(new ItemStack(ItemRegistry.BLOOD_VIAL.get()), ITextComponent.translatable("item.irons_spellbooks.blood_vial.guide"));
        registration.addItemStackInfo(new ItemStack(ItemRegistry.FROZEN_BONE_SHARD.get()), ITextComponent.translatable("item.irons_spellbooks.frozen_bone.guide"));
        registration.addItemStackInfo(new ItemStack(ItemRegistry.HOGSKIN.get()), ITextComponent.translatable("item.irons_spellbooks.hogskin.guide"));
        registration.addItemStackInfo(new ItemStack(ItemRegistry.DRAGONSKIN.get()), ITextComponent.translatable("item.irons_spellbooks.dragonskin.guide"));
        registration.addItemStackInfo(new ItemStack(ItemRegistry.RUINED_BOOK.get()), ITextComponent.translatable("item.irons_spellbooks.ruined_book.guide"));
        registration.addItemStackInfo(new ItemStack(ItemRegistry.CINDER_ESSENCE.get()), ITextComponent.translatable("item.irons_spellbooks.cinder_essence.guide"));
    }


    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ArcaneAnvilScreen.class, 102, 48, 22, 15, ArcaneAnvilRecipeCategory.ARCANE_ANVIL_RECIPE_RECIPE_TYPE);
        registration.addRecipeClickArea(ScrollForgeScreen.class, 1, 1, 76, 14, ScrollForgeRecipeCategory.SCROLL_FORGE_RECIPE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(ArcaneAnvilMenu.class, MenuRegistry.ARCANE_ANVIL_MENU.get(), ArcaneAnvilRecipeCategory.ARCANE_ANVIL_RECIPE_RECIPE_TYPE, 0, 2, 3, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.ARCANE_ANVIL_BLOCK.get()), ArcaneAnvilRecipeCategory.ARCANE_ANVIL_RECIPE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.SCROLL_FORGE_BLOCK.get()), ScrollForgeRecipeCategory.SCROLL_FORGE_RECIPE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.ALCHEMIST_CAULDRON.get()), AlchemistCauldronRecipeCategory.ALCHEMIST_CAULDRON_RECIPE_TYPE);
    }

    private static final IIngredientSubtypeInterpreter<ItemStack> SCROLL_INTERPRETER = (stack, context) -> {
        if (stack.hasTag()) {
            SpellData spellData = SpellData.getSpellData(stack);
            return String.format("scroll:%s:%d", spellData.getSpell().getSpellId(), spellData.getLevel());
        }

        return IIngredientSubtypeInterpreter.NONE;
    };
}
