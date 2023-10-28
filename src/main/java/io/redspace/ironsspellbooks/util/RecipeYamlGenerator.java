package io.redspace.ironsspellbooks.util;

import com.google.gson.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class RecipeYamlGenerator {
    private static final class ItemInfo {
        private final String name;
        private final String path;

        private ItemInfo(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String name() {
            return name;
        }

        public String path() {
            return path;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ItemInfo) obj;
            return Objects.equals(this.name, that.name) &&
                    Objects.equals(this.path, that.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, path);
        }

        @Override
        public String toString() {
            return "ItemInfo[" +
                    "name=" + name + ", " +
                    "path=" + path + ']';
        }

        }

    private static final String RECIPE_DATA_TEMPLATE = """
            - name: "%s"
              path: "%s"
              item0: "%s"
              item0Path: "%s"
              item1: "%s"
              item1Path: "%s"
              item2: "%s"
              item2Path: "%s"
              item3: "%s"
              item3Path: "%s"
              item4: "%s"
              item4Path: "%s"
              item5: "%s"
              item5Path: "%s"
              item6: "%s"
              item6Path: "%s"
              item7: "%s"
              item7Path: "%s"
              item8: "%s"
              item8Path: "%s"
              item9: "%s"
              item9Path: "%s"
             
                    """;

    public static void main(String[] args) {
        String baseDir = System.getProperty("user.dir");
        String recipesDir = "src/main/resources/data/irons_spellbooks/recipes/";

        StringBuilder sb = new StringBuilder();
        Gson g = new Gson();

        try {
            Files.walk(Path.of(baseDir, recipesDir)).forEach(path -> {
                System.out.println(path);

                if (path.toFile().isFile()) {
                    try {
                        JsonObject jsonObject = JsonParser.parseReader(new FileReader(path.toFile())).getAsJsonObject();
                        String type = jsonObject.get("type").getAsString();
                        if (type.equals("minecraft:crafting_shaped")) {
                            String result = jsonObject.get("result").getAsJsonObject().get("item").getAsString();
                            System.out.println(result);
                            ItemInfo[] itemInfoArray = new ItemInfo[9];
                            JsonArray pattern = jsonObject.get("pattern").getAsJsonArray();
                            Map<String, JsonElement> keyMap = jsonObject.get("key").getAsJsonObject().asMap();


                            int count = 0;
                            for (int i = 0; i < pattern.size(); i++) {
                                String slots = pattern.get(i).getAsString();
                                for (int j = 0; j < slots.length(); j++) {
                                    char key = slots.charAt(j);
                                    if (key != ' ') {
                                        JsonElement item = keyMap.get(String.valueOf(key)).getAsJsonObject().get("item");
                                        if (item == null) {
                                            item = keyMap.get(String.valueOf(key)).getAsJsonObject().get("tag");
                                        }
                                        itemInfoArray[count] = new ItemInfo(item.getAsString(), item.getAsString());
                                    }
                                    count++;
                                }
                            }

                            for (int i = 0; i < itemInfoArray.length; i++) {
                                System.out.println(String.format("%d: %s", i, itemInfoArray[i]));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        Arrays.stream(e.getStackTrace()).forEach(System.out::println);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
