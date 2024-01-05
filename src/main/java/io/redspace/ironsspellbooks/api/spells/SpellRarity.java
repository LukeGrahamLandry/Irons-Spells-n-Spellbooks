package io.redspace.ironsspellbooks.api.spells;

import com.google.common.util.concurrent.AtomicDouble;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import net.minecraft.util.text.TextFormatting;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum SpellRarity {
    COMMON(0),
    UNCOMMON(1),
    RARE(2),
    EPIC(3),
    LEGENDARY(4)/*,
    MYTHIC(5),
    ANCIENT(6)*/;

    private final int value;

    SpellRarity(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return this.value;
    }

    public IFormattableTextComponent getDisplayName() {
        return DISPLAYS[getValue()];
    }

    private final static LazyOptional<List<Double>> rawRarityConfig = LazyOptional.of(SpellRarity::getRawRarityConfigInternal);
    private static List<Double> rarityConfig = null;

    public static List<Double> getRawRarityConfig() {
        return rawRarityConfig.resolve().get();
    }

    private static List<Double> getRawRarityConfigInternal() {
        List<Double> fromConfig = (List<Double>) ServerConfigs.RARITY_CONFIG.get();

        if (fromConfig.size() != 5) {
            List<Double> configDefault = ServerConfigs.RARITY_CONFIG_DEFAULT;
            IronsSpellbooks.LOGGER.info("INVALID RARITY CONFIG FOUND (Size != 5): {} FALLING BACK TO DEFAULT: {}", fromConfig, configDefault);
            return configDefault;
        }

        if (fromConfig.stream().mapToDouble(a -> a).sum() != 1) {
            List<Double> configDefault = (List<Double>) ServerConfigs.RARITY_CONFIG_DEFAULT;
            IronsSpellbooks.LOGGER.info("INVALID RARITY CONFIG FOUND (Values must add up to 1): {} FALLING BACK TO DEFAULT: {}", fromConfig, configDefault);
            return configDefault;
        }

        return fromConfig;
    }

    public static List<Double> getRarityConfig() {
        if (rarityConfig == null) {
            AtomicDouble counter = new AtomicDouble();
            rarityConfig = new ArrayList<>();
            getRawRarityConfig().forEach(item -> {
                rarityConfig.add(counter.addAndGet(item));
            });
        }

        return rarityConfig;
    }


    /**
     * @return Returns positive if the other is less rare, negative if it is more rare, and zero if they are equal
     */
    public int compareRarity(SpellRarity other) {
        return Integer.compare(this.getValue(), other.getValue());
    }

    public static void rarityTest() {
        StringBuilder sb = new StringBuilder();
        SpellRegistry.REGISTRY.get().getValues().forEach(s -> {
            sb.append(String.format("\nSpellType:%s\n", s));
            sb.append(String.format("\tMinRarity:%s, MaxRarity:%s\n", s.getMinRarity(), s.getMaxRarity()));
            sb.append(String.format("\tMinLevel:%s, MaxLevel:%s\n", s.getMinLevel(), s.getMaxLevel()));
            sb.append(String.format("\tRawRarityConfig:%s\n", getRawRarityConfig().stream().map(Object::toString).collect(Collectors.joining(","))));
            sb.append(String.format("\tRarityConfig:%s\n", getRarityConfig().stream().map(Object::toString).collect(Collectors.joining(","))));

            for (int i = s.getMinLevel(); i <= s.getMaxLevel(); i++) {
                sb.append(String.format("\t\tLevel %s -> %s\n", i, s.getRarity(i)));
            }

            sb.append("\n");

            for (int i = s.getMinRarity(); i <= s.getMaxRarity(); i++) {
                sb.append(String.format("\t\t%s -> Level %s\n", SpellRarity.values()[i], s.getMinLevelForRarity(SpellRarity.values()[i])));
            }
        });

        //Ironsspellbooks.logger.debug(sb.toString());
    }

    public TextFormatting getChatFormatting() {
        switch (this) {
            case COMMON:
                return TextFormatting.GRAY;
            case UNCOMMON:
                return TextFormatting.GREEN;
            case RARE:
                return TextFormatting.AQUA;
            case EPIC:
                return TextFormatting.LIGHT_PURPLE;
            case LEGENDARY:
                return TextFormatting.GOLD;
            default:
                throw new IllegalArgumentException();
        }
    }

    private final IFormattableTextComponent[] DISPLAYS = {
            new TranslationTextComponent("rarity.irons_spellbooks.common").withStyle(TextFormatting.GRAY),
            new TranslationTextComponent("rarity.irons_spellbooks.uncommon").withStyle(TextFormatting.GREEN),
            new TranslationTextComponent("rarity.irons_spellbooks.rare").withStyle(TextFormatting.AQUA),
            new TranslationTextComponent("rarity.irons_spellbooks.epic").withStyle(TextFormatting.LIGHT_PURPLE),
            new TranslationTextComponent("rarity.irons_spellbooks.legendary").withStyle(TextFormatting.GOLD),
            new TranslationTextComponent("rarity.irons_spellbooks.mythic").withStyle(TextFormatting.GOLD),
            new TranslationTextComponent("rarity.irons_spellbooks.ancient").withStyle(TextFormatting.GOLD),
    };
}
