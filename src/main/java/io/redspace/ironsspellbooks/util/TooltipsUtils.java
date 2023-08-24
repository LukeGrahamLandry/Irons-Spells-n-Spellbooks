package io.redspace.ironsspellbooks.util;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.CastSource;
import io.redspace.ironsspellbooks.spells.CastType;
import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.util.text.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TooltipsUtils {


    public static List<ITextComponent> formatActiveSpellTooltip(ItemStack stack, CastSource castSource, @Nonnull ClientPlayerEntity player) {
        //var player = Minecraft.getInstance().player;
        AbstractSpell spell = stack.getItem() instanceof SpellBook ? SpellBookData.getSpellBookData(stack).getActiveSpell() : SpellData.getSpellData(stack).getSpell(); //Put me in utils?
        SpellType spellType = spell.getSpellType();
//        var title = Component.translatable("tooltip.irons_spellbooks.selected_spell",
//                spellType.getDisplayName().withStyle(spellType.getSchoolType().getDisplayName().getStyle()),
//                Component.literal("" + spell.getLevel()).withStyle(spellType.getRarity(spell.getLevel()).getDisplayName().getStyle()));
//        var title = Component.translatable("tooltip.irons_spellbooks.selected_spell",
//                spellType.getDisplayName().withStyle(spellType.getSchoolType().getDisplayName().getStyle()),
//                Component.literal("" + spell.getLevel())).withStyle(spellType.getRarity(spell.getLevel()).getDisplayName().getStyle());
        var levelText = getLevelComponenet(spell, player);

        var title = new TranslationTextComponent("tooltip.irons_spellbooks.selected_spell",
                spellType.getDisplayName(),
                levelText).withStyle(spellType.getSchoolType().getDisplayName().getStyle());
        var uniqueInfo = spell.getUniqueInfo(player);
        var manaCost = getManaCostComponent(spell.getCastType(), spell.getManaCost()).withStyle(TextFormatting.BLUE);
        var cooldownTime = new TranslationTextComponent("tooltip.irons_spellbooks.cooldown_length_seconds", Utils.timeFromTicks(MagicManager.getEffectiveSpellCooldown(spellType, player, castSource), 1)).withStyle(TextFormatting.BLUE);

        List<ITextComponent> lines = new ArrayList<>();
        lines.add(StringTextComponent.EMPTY);
        lines.add(title);
        uniqueInfo.forEach((line) -> lines.add(new StringTextComponent(" ").append(line.withStyle(TextFormatting.DARK_GREEN))));
        if (spell.getCastType() != CastType.INSTANT) {
            lines.add(new StringTextComponent(" ").append(getCastTimeComponent(spell.getCastType(), Utils.timeFromTicks(spell.getEffectiveCastTime(player), 1)).withStyle(TextFormatting.BLUE)));
        }
        if (castSource != CastSource.SWORD || ServerConfigs.SWORDS_CONSUME_MANA.get())
            lines.add(manaCost);
        if (castSource != CastSource.SWORD || ServerConfigs.SWORDS_CD_MULTIPLIER.get().floatValue() > 0)
            lines.add(cooldownTime);
        return lines;
    }

    public static List<ITextComponent> formatScrollTooltip(ItemStack stack, @Nonnull ClientPlayerEntity player) {
        AbstractSpell spell = SpellData.getSpellData(stack).getSpell();
        SpellType spellType = spell.getSpellType();
        if (spellType == SpellType.NONE_SPELL)
            return List.of();
        var levelText = getLevelComponenet(spell, player);
        var title = new TranslationTextComponent("tooltip.irons_spellbooks.level", levelText).append(" ").append(new TranslationTextComponent("tooltip.irons_spellbooks.rarity", spell.getRarity().getDisplayName()).withStyle(spell.getRarity().getDisplayName().getStyle())).withStyle(TextFormatting.GRAY);
        var uniqueInfo = spell.getUniqueInfo(player);
        var whenInSpellBook = new TranslationTextComponent("tooltip.irons_spellbooks.scroll_tooltip").withStyle(TextFormatting.GRAY);
        var manaCost = getManaCostComponent(spell.getCastType(), spell.getManaCost()).withStyle(TextFormatting.BLUE);
        var cooldownTime = new TranslationTextComponent("tooltip.irons_spellbooks.cooldown_length_seconds", Utils.timeFromTicks(MagicManager.getEffectiveSpellCooldown(spellType, player, CastSource.SCROLL), 1)).withStyle(TextFormatting.BLUE);

        List<ITextComponent> lines = new ArrayList<>();
        lines.add(new StringTextComponent(" ").append(title));
        uniqueInfo.forEach((line) -> lines.add(new StringTextComponent(" ").append(line.withStyle(TextFormatting.DARK_GREEN))));
        if (spell.getCastType() != CastType.INSTANT) {
            lines.add(new StringTextComponent(" ").append(getCastTimeComponent(spell.getCastType(), Utils.timeFromTicks(spell.getEffectiveCastTime(player), 1)).withStyle(TextFormatting.BLUE)));
        }
        lines.add(StringTextComponent.EMPTY);
        lines.add(whenInSpellBook);
        lines.add(manaCost);
        lines.add(cooldownTime);
        lines.add(spell.getSchoolType().getDisplayName().copy());

        return lines;
    }

    public static IFormattableTextComponent getLevelComponenet(AbstractSpell spell, LivingEntity caster) {
        int levelTotal = spell.getLevel(caster);
        int rawLevel = spell.getRawLevel();
        int diff = levelTotal - rawLevel;
        if (diff > 0)
            return new TranslationTextComponent("tooltip.irons_spellbooks.level_plus", levelTotal, diff);
        else
            return new StringTextComponent("" + levelTotal);
    }

    public static IFormattableTextComponent getCastTimeComponent(CastType type, String castTime) {
        return switch (type) {
            case CONTINUOUS -> new TranslationTextComponent("tooltip.irons_spellbooks.cast_continuous", castTime);
            case LONG -> new TranslationTextComponent("tooltip.irons_spellbooks.cast_long", castTime);
            default -> new TranslationTextComponent("ui.irons_spellbooks.cast_instant");
        };
    }

    public static IFormattableTextComponent getManaCostComponent(CastType castType, int manaCost) {
        if (castType == CastType.CONTINUOUS) {
            return new TranslationTextComponent("tooltip.irons_spellbooks.mana_cost_per_second", manaCost * (20 / MagicManager.CONTINUOUS_CAST_TICK_INTERVAL));
        } else {
            return new TranslationTextComponent("tooltip.irons_spellbooks.mana_cost", manaCost);
        }
    }

    public static List<IReorderingProcessor> createSpellDescriptionTooltip(SpellType spell, FontRenderer font){
        var name = spell.getDisplayName();
        var description = font.split(new TranslationTextComponent(String.format("%s.guide", spell.getComponentId())).withStyle(TextFormatting.GRAY), 180);
        var hoverText = new ArrayList<IReorderingProcessor>();
        hoverText.add(IReorderingProcessor.forward(name.getString(), Style.EMPTY.withUnderlined(true)));
        hoverText.addAll(description);
        return hoverText;
    }
}
