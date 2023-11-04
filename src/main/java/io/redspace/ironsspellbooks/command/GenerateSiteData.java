package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.item.crafting.SmithingRecipe;

public class GenerateSiteData {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.irons_spellbooks.generate_recipe_data.failed"));

    private static final String RECIPE_DATA_TEMPLATE = """
            - id: "%s"
              name: "%s"
              path: "%s"
              group: "%s"
              craftingType: "%s"
              item0Id: "%s"
              item0: "%s"
              item0Path: "%s"
              item1Id: "%s"
              item1: "%s"
              item1Path: "%s"
              item2Id: "%s"
              item2: "%s"
              item2Path: "%s"
              item3Id: "%s"
              item3: "%s"
              item3Path: "%s"
              item4Id: "%s"
              item4: "%s"
              item4Path: "%s"
              item5Id: "%s"
              item5: "%s"
              item5Path: "%s"
              item6Id: "%s"
              item6: "%s"
              item6Path: "%s"
              item7Id: "%s"
              item7: "%s"
              item7Path: "%s"
              item8Id: "%s"
              item8: "%s"
              item8Path: "%s"
              tooltip: "%s"
              description: ""
              
                    """;

    private static final String SPELL_DATA_TEMPLATE = """
            - name: "%s"
              school: "%s"
              icon: "%s"
              level: "%d to %d"
              mana: "%d to %d"
              cooldown: "%ds"
              cast_type: "%s"
              rarity: "%s to %s"
              description: "%s"
              u1: "%s"
              u2: "%s"
              u3: "%s"
              u4: "%s"
              
                    """;

    public static void register(CommandDispatcher<CommandSource> pDispatcher) {
        pDispatcher.register(Commands.literal("generateSiteData").requires((p_138819_) -> {
            return p_138819_.hasPermission(2);
        }).executes((commandContext) -> {
            return generateSiteData(commandContext.getSource());
        }));
    }

    private static int generateSiteData(CommandSource source) {
        generateRecipeData(source);
        generateSpellData();

        return 1;
    }

    private static void generateRecipeData(CommandSource source) {
        try {
            StringBuilder itemBuilder = new StringBuilder();
            StringBuilder armorBuilder = new StringBuilder();
            StringBuilder spellbookBuilder = new StringBuilder();
            StringBuilder blockBuilder = new StringBuilder();

            List<String> armorTypes = List.of("Archevoker", "Cryomancer", "Cultist", "Electromancer", "Priest", "Pumpkin", "Pyromancer", "Shadow-Walker", "Wandering Magician", "Ring", "Heavy Chain", "Scarecrow", "Plagued");
            Set<Item> itemsTracked = new HashSet<>();
            //This will exclude these items
            itemsTracked.add(ItemRegistry.WIMPY_SPELL_BOOK.get());
            itemsTracked.add(ItemRegistry.LEGENDARY_SPELL_BOOK.get());
            itemsTracked.add(Items.POISONOUS_POTATO);

            source.getLevel().getRecipeManager().getRecipes()
                    .stream()
                    .filter(r -> r.getId().getNamespace().equals("irons_spellbooks") && !r.getId().toString().contains("poisonous_potato"))
                    .sorted(Comparator.comparing(x -> x.getId().toString()))
                    .forEach(recipe -> {
                        //IronsSpellbooks.LOGGER.debug("recipe: {}, {}, {}", recipe.getId(), recipe.getClass(), recipe.getType());
                        //IronsSpellbooks.LOGGER.debug("recipe: resultItem: {}", ForgeRegistries.ITEMS.getKey(recipe.getResultItem().getItem()));

                        ResourceLocation resultItemResourceLocation = ForgeRegistries.ITEMS.getKey(recipe.getResultItem().getItem());
                        ArrayList<RecipeData> recipeData = new ArrayList<RecipeData>(10);
                        recipeData.add(new RecipeData(
                                resultItemResourceLocation.toString(),
                                recipe.getResultItem().getItem().getName(ItemStack.EMPTY).getString(),
                                String.format("/img/items/%s.png", resultItemResourceLocation.getPath()),
                                recipe.getResultItem().getItem())
                        );

                        itemsTracked.add(recipe.getResultItem().getItem());

                        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
                            recipe.getIngredients().forEach(ingredient -> {
                                handleIngeredient(ingredient, recipeData, recipe);
                            });
                        } else if (recipe instanceof SmithingRecipe) {
                            SmithingRecipe upgradeRecipe = (SmithingRecipe) recipe;
                            handleIngeredient(upgradeRecipe.base, recipeData, recipe);
                            handleIngeredient(upgradeRecipe.addition, recipeData, recipe);
                        }

                        String name = getRecipeDataAtIndex(recipeData, 0).name;
                        String tooltip = getTooltip(source.getPlayer(), recipe.getResultItem());

                        if (getRecipeDataAtIndex(recipeData, 0).item instanceof SpellBook || getRecipeDataAtIndex(recipeData, 0).item instanceof ExtendedSwordItem) {
                            appendToBuilder(spellbookBuilder, recipe, recipeData, "", tooltip);
                        } else if (armorTypes.stream().anyMatch(item -> name.contains(item))) {
                            String[] words = name.split(" ");
                            String group = Arrays.stream(words).limit(words.length - 1).collect(Collectors.joining(" "));
                            appendToBuilder(armorBuilder, recipe, recipeData, group, tooltip);
                        } else if (recipe.getResultItem().getItem() instanceof BlockItem) {
                            appendToBuilder(blockBuilder, recipe, recipeData, "", tooltip);
                        } else {
                            appendToBuilder(itemBuilder, recipe, recipeData, "", tooltip);
                        }
                    });

            ForgeRegistries.ITEMS.getValues()
                    .stream()
                    .sorted(Comparator.comparing(Item::getDescriptionId))
                    .forEach(item -> {
                        ResourceLocation itemResource = ForgeRegistries.ITEMS.getKey(item);
                        String tooltip = getTooltip(source.getPlayer(), new ItemStack(item));

                        if (itemResource.toString().contains("irons_spellbooks") && !itemsTracked.contains(item)) {
                            //Non craftable items
                            String name = item.getName(ItemStack.EMPTY).getString();
                            if (item.getDescriptionId().contains("spawn_egg") || item.getDescriptionId().equals("item.irons_spellbooks.scroll")) {
                                //Skip
                            } else if (armorTypes.stream().anyMatch(itemToMatch -> name.contains(itemToMatch))) {
                                appendToBuilder2(armorBuilder, name, itemResource, tooltip);
                            } else if (item instanceof UniqueSpellBook) {
                                appendToBuilder2(spellbookBuilder, name, itemResource, getSpells(new ItemStack(item)));
                            } else if (item instanceof SpellBook || item instanceof ExtendedSwordItem) {
                                appendToBuilder2(spellbookBuilder, name, itemResource, tooltip);
                            } else if (item instanceof BlockItem) {
                                appendToBuilder2(blockBuilder, name, itemResource, tooltip);
                            } else {
                                appendToBuilder2(itemBuilder, name, itemResource, tooltip);
                            }
                            itemsTracked.add(item);

                        }
                    });

            BufferedWriter file = new BufferedWriter(new FileWriter("item_data.yml"));
            file.write(postProcess(itemBuilder));
            file.close();

            file = new BufferedWriter(new FileWriter("armor_data.yml"));
            file.write(postProcess(armorBuilder));
            file.close();

            file = new BufferedWriter(new FileWriter("spellbook_data.yml"));
            file.write(postProcess(spellbookBuilder));
            file.close();

            file = new BufferedWriter(new FileWriter("block_data.yml"));
            file.write(postProcess(blockBuilder));
            file.close();
        } catch (Exception e) {
            IronsSpellbooks.LOGGER.debug(e.getMessage());
        }
    }

    private static String postProcess(StringBuilder sb) {
        return sb.toString()
                .replace("netherite_spell_book.png", "netherite_spell_book.gif")
                .replace("ruined_book.png", "ruined_book.gif")
                .replace("lightning_bottle.png", "lightning_bottle.gif")
                .replace("/upgrade_orb.png", "/upgrade_orb.gif")
                .replace("fire_upgrade_orb.png", "fire_upgrade_orb.gif")
                .replace("holy_upgrade_orb.png", "holy_upgrade_orb.gif")
                .replace("lightning_upgrade_orb.png", "lightning_upgrade_orb.gif")
                .replace("ender_upgrade_orb.png", "ender_upgrade_orb.gif")
                .replace("wayward_compass.png", "wayward_compass.gif");
    }

    private static String getSpells(ItemStack itemStack) {
        if (itemStack.getItem() instanceof SpellBook) {
            return SpellBookData.getSpellBookData(itemStack).getActiveInscribedSpells().stream().map(spell -> {
                return spell.getSpell().getDisplayName().getString() + " (" + spell.getLevel() + ")";
            }).collect(Collectors.joining(", "));
        }
        return "";
    }

    private static String getTooltip(ServerPlayerEntity player, ItemStack itemStack) {
        return Arrays.stream(itemStack.getTooltipLines(player, ITooltipFlag.TooltipFlags.NORMAL)
                        .stream()
                        .skip(1) //First component is always the name. Ignore it
                        .map(ITextComponent::getString)
                        .filter(x -> x.trim().length() > 0)
                        .collect(Collectors.joining(", "))
                        .replace(":,", ": ")
                        .replace("  ", " ")
                        .split(","))
                .filter(item -> !item.contains("Slot"))
                .collect(Collectors.joining(","))
                .trim()
                .replace(":", ":<br>");
    }

    private static void appendToBuilder(StringBuilder sb, IRecipe recipe, List<RecipeData> recipeData, String group, String tooltip) {
        sb.append(String.format(RECIPE_DATA_TEMPLATE,
                getRecipeDataAtIndex(recipeData, 0).id,
                getRecipeDataAtIndex(recipeData, 0).name,
                getRecipeDataAtIndex(recipeData, 0).path,
                group,
                recipe.getType(),
                getRecipeDataAtIndex(recipeData, 1).id,
                getRecipeDataAtIndex(recipeData, 1).name,
                getRecipeDataAtIndex(recipeData, 1).path,
                getRecipeDataAtIndex(recipeData, 2).id,
                getRecipeDataAtIndex(recipeData, 2).name,
                getRecipeDataAtIndex(recipeData, 2).path,
                getRecipeDataAtIndex(recipeData, 3).id,
                getRecipeDataAtIndex(recipeData, 3).name,
                getRecipeDataAtIndex(recipeData, 3).path,
                getRecipeDataAtIndex(recipeData, 4).id,
                getRecipeDataAtIndex(recipeData, 4).name,
                getRecipeDataAtIndex(recipeData, 4).path,
                getRecipeDataAtIndex(recipeData, 5).id,
                getRecipeDataAtIndex(recipeData, 5).name,
                getRecipeDataAtIndex(recipeData, 5).path,
                getRecipeDataAtIndex(recipeData, 6).id,
                getRecipeDataAtIndex(recipeData, 6).name,
                getRecipeDataAtIndex(recipeData, 6).path,
                getRecipeDataAtIndex(recipeData, 7).id,
                getRecipeDataAtIndex(recipeData, 7).name,
                getRecipeDataAtIndex(recipeData, 7).path,
                getRecipeDataAtIndex(recipeData, 8).id,
                getRecipeDataAtIndex(recipeData, 8).name,
                getRecipeDataAtIndex(recipeData, 8).path,
                getRecipeDataAtIndex(recipeData, 9).id,
                getRecipeDataAtIndex(recipeData, 9).name,
                getRecipeDataAtIndex(recipeData, 9).path,
                tooltip
        ));
    }

    private static void appendToBuilder2(StringBuilder sb, String name, ResourceLocation itemResource, String tooltip) {
        sb.append(String.format(RECIPE_DATA_TEMPLATE,
                itemResource.toString(),
                name,
                String.format("/img/items/%s.png", itemResource.getPath()),
                "",
                "none",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", tooltip
        ));
    }

    private static void handleIngeredient(Ingredient ingredient, ArrayList<RecipeData> recipeData, IRecipe recipe) {
        Arrays.stream(ingredient.getItems())
                .findFirst()
                .ifPresentOrElse(itemStack -> {
                    ResourceLocation itemResource = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
                    String path = "";

                    if (itemResource.toString().contains("irons_spellbooks")) {
                        path = String.format("/img/items/%s.png", itemResource.getPath());
                    } else {
                        path = String.format("/img/items/minecraft/%s.png", itemResource.getPath());
                    }

                    recipeData.add(new RecipeData(
                            itemResource.toString(),
                            itemStack.getItem().getName(ItemStack.EMPTY).getString(),
                            path,
                            recipe.getResultItem().getItem()));

                }, () -> {
                    recipeData.add(RecipeData.EMPTY);
                });
    }

    private static RecipeData getRecipeDataAtIndex(List<RecipeData> recipeData, int index) {
        if (index < recipeData.size()) {
            return recipeData.get(index);
        } else {
            return RecipeData.EMPTY;
        }
    }

    private static final class RecipeData {
            public static RecipeData EMPTY = new RecipeData("", "", "", null);
        private final String id;
        private final String name;
        private final String path;
        private final Item item;

        private RecipeData(String id, String name, String path, Item item) {
            this.id = id;
            this.name = name;
            this.path = path;
            this.item = item;
        }

        public String id() {
            return id;
        }

        public String name() {
            return name;
        }

        public String path() {
            return path;
        }

        public Item item() {
            return item;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (RecipeData) obj;
            return Objects.equals(this.id, that.id) &&
                    Objects.equals(this.name, that.name) &&
                    Objects.equals(this.path, that.path) &&
                    Objects.equals(this.item, that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, path, item);
        }

        @Override
        public String toString() {
            return "RecipeData[" +
                    "id=" + id + ", " +
                    "name=" + name + ", " +
                    "path=" + path + ", " +
                    "item=" + item + ']';
        }

        }

    private static void generateSpellData() {
        try {
            StringBuilder sb = new StringBuilder();

            SpellRegistry.REGISTRY.get().getValues().stream()
                    .filter(st -> (st.isEnabled() && st != SpellRegistry.none()))
                    .forEach(spellType -> {
                        int spellMin = spellType.getMinLevel();
                        int spellMax = spellType.getMaxLevel();

                        List<IFormattableTextComponent> uniqueInfo = spellType.getUniqueInfo(spellMin, null);
                        String u1 = uniqueInfo.size() >= 1 ? uniqueInfo.get(0).getString() : "";
                        String u2 = uniqueInfo.size() >= 2 ? uniqueInfo.get(1).getString() : "";
                        String u3 = uniqueInfo.size() >= 3 ? uniqueInfo.get(2).getString() : "";
                        String u4 = uniqueInfo.size() >= 4 ? uniqueInfo.get(3).getString() : "";

                        sb.append(String.format(SPELL_DATA_TEMPLATE,
                                handleCapitalization(spellType.getSpellName()),
                                handleCapitalization(spellType.getSchoolType().getDisplayName().getString()),
                                String.format("/img/spells/%s.png", spellType.getSpellName()),
                                spellType.getMinLevel(),
                                spellType.getMaxLevel(),
                                spellType.getManaCost(spellMin, null),
                                spellType.getManaCost(spellMax, null),
                                spellType.getSpellCooldown(),
                                handleCapitalization(spellType.getCastType().name()),
                                handleCapitalization(spellType.getRarity(spellMin).name()),
                                handleCapitalization(spellType.getRarity(spellMax).name()),
                                new TranslationTextComponent(String.format("%s.guide", spellType.getComponentId())).getString(),
                                u1,
                                u2,
                                u3,
                                u4)
                        );
                    });

            BufferedWriter file = new BufferedWriter(new FileWriter("spell_data.yml"));
            file.write(sb.toString());
            file.close();
        } catch (Exception e) {
            IronsSpellbooks.LOGGER.debug(e.getMessage());
        }
    }

    public static String handleCapitalization(String input) {
        return Arrays.stream(input.toLowerCase().split("[ |_]"))
                .map(word -> {
                    if (word.equals("spell")) {
                        return "";
                    } else {
                        String first = word.substring(0, 1);
                        String rest = word.substring(1);
                        return first.toUpperCase() + rest;
                    }
                })
                .collect(Collectors.joining(" "))
                .trim();
    }

    private enum CraftingType {
        CRAFTING_TABLE,
        SMITHING_TABLE,
        NOT_CRAFTABLE
    }
}
