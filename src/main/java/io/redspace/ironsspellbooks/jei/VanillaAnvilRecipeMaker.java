package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class VanillaAnvilRecipeMaker {

    public static List<IJeiAnvilRecipe> getAnvilRepairRecipes(IVanillaRecipeFactory vanillaRecipeFactory) {
        return Stream.concat(
                getArmorRepairRecipes(vanillaRecipeFactory),
                getItemRepairRecipes(vanillaRecipeFactory)
        ).toList();
    }

    public static Stream<IJeiAnvilRecipe> getItemRepairRecipes(IVanillaRecipeFactory vanillaRecipeFactory) {
        List<TieredItem> repairableItems = getTieredItems();
        return repairableItems.stream()
                .mapMulti((item, consumer) -> {
                    ItemStack damagedThreeQuarters = new ItemStack(item);
                    damagedThreeQuarters.setDamageValue(damagedThreeQuarters.getMaxDamage() * 3 / 4);
                    ItemStack damagedHalf = new ItemStack(item);
                    damagedHalf.setDamageValue(damagedHalf.getMaxDamage() / 2);

                    IJeiAnvilRecipe repairWithSame = vanillaRecipeFactory.createAnvilRecipe(List.of(damagedThreeQuarters), List.of(damagedThreeQuarters), List.of(damagedHalf));
                    consumer.accept(repairWithSame);

                    List<ItemStack> repairMaterials = Arrays.stream(item.getTier().getRepairIngredient().getItems()).toList();
                    ItemStack damagedFully = new ItemStack(item);
                    damagedFully.setDamageValue(damagedFully.getMaxDamage());
                    IJeiAnvilRecipe repairWithMaterial = vanillaRecipeFactory.createAnvilRecipe(List.of(damagedFully), repairMaterials, List.of(damagedThreeQuarters));
                    consumer.accept(repairWithMaterial);
                });
    }

    public static Stream<IJeiAnvilRecipe> getArmorRepairRecipes(IVanillaRecipeFactory vanillaRecipeFactory) {
        List<ArmorItem> repairableItems = getArmorItems();
        return repairableItems.stream()
                .mapMulti((item, consumer) -> {
                    ItemStack damagedThreeQuarters = new ItemStack(item);
                    damagedThreeQuarters.setDamageValue(damagedThreeQuarters.getMaxDamage() * 3 / 4);
                    ItemStack damagedHalf = new ItemStack(item);
                    damagedHalf.setDamageValue(damagedHalf.getMaxDamage() / 2);

                    IJeiAnvilRecipe repairWithSame = vanillaRecipeFactory.createAnvilRecipe(List.of(damagedThreeQuarters), List.of(damagedThreeQuarters), List.of(damagedHalf));
                    consumer.accept(repairWithSame);

                    List<ItemStack> repairMaterials = Arrays.stream(item.getMaterial().getRepairIngredient().getItems()).toList();
                    ItemStack damagedFully = new ItemStack(item);
                    damagedFully.setDamageValue(damagedFully.getMaxDamage());
                    IJeiAnvilRecipe repairWithMaterial = vanillaRecipeFactory.createAnvilRecipe(List.of(damagedFully), repairMaterials, List.of(damagedThreeQuarters));
                    consumer.accept(repairWithMaterial);
                });
    }

    public static List<TieredItem> getTieredItems() {
        Collection<RegistryObject<Item>> registryItems = ItemRegistry.getIronsItems();
        List<TieredItem> items = new ArrayList<>();
        for (RegistryObject<Item> item : registryItems)
            if (item.get() instanceof TieredItem && ((TieredItem) item.get()).getItemCategory() != null) {
                TieredItem tieredItem = (TieredItem) item.get();
                items.add(tieredItem);
            }
        return items;
    }

    public static List<ArmorItem> getArmorItems() {
        Collection<RegistryObject<Item>> registryItems = ItemRegistry.getIronsItems();
        List<ArmorItem> items = new ArrayList<>();
        for (RegistryObject<Item> item : registryItems)
            if (item.get() instanceof ArmorItem && ((ArmorItem) item.get()).getItemCategory() != null) {
                ArmorItem tieredItem = (ArmorItem) item.get();
                items.add(tieredItem);
            }
        return items;
    }
}
