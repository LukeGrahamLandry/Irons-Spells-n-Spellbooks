package io.redspace.ironsspellbooks.spells;

import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.item.ItemStack;

public enum SchoolType {
    FIRE(0),
    ICE(1),
    LIGHTNING(2),
    HOLY(3),
    ENDER(4),
    BLOOD(5),
    EVOCATION(6),
    VOID(7),
    POISON(8);

    private final int value;

    SchoolType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

    public ITextComponent getDisplayName() {
        return DISPLAYS[getValue()];
    }

    public static SchoolType getSchoolFromItem(ItemStack stack) {
        if (stack.is(ModTags.FIRE_FOCUS)) {
            return FIRE;
        } else if (stack.is(ModTags.ICE_FOCUS)) {
            return ICE;
        } else if (stack.is(ModTags.LIGHTNING_FOCUS)) {
            return LIGHTNING;
        } else if (stack.is(ModTags.HOLY_FOCUS)) {
            return HOLY;
        } else if (stack.is(ModTags.ENDER_FOCUS)) {
            return ENDER;
        } else if (stack.is(ModTags.BLOOD_FOCUS)) {
            return BLOOD;
        } else if (stack.is(ModTags.EVOCATION_FOCUS)) {
            return EVOCATION;
        }else if (stack.is(ModTags.VOID_FOCUS)) {
            return VOID;
        }else if (stack.is(ModTags.POISON_FOCUS)) {
            return POISON;
        } else return null;
    }

    public static final ITextComponent DISPLAY_FIRE = ITextComponent.translatable("school.irons_spellbooks.fire").withStyle(TextFormatting.GOLD);
    public static final ITextComponent DISPLAY_ICE = ITextComponent.translatable("school.irons_spellbooks.ice").withStyle(Style.EMPTY.withColor(0xd0f9ff));
    public static final ITextComponent DISPLAY_LIGHTNING = ITextComponent.translatable("school.irons_spellbooks.lightning").withStyle(TextFormatting.AQUA);
    public static final ITextComponent DISPLAY_HOLY = ITextComponent.translatable("school.irons_spellbooks.holy").withStyle(Style.EMPTY.withColor(0xfff8d4));
    public static final ITextComponent DISPLAY_ENDER = ITextComponent.translatable("school.irons_spellbooks.ender").withStyle(TextFormatting.LIGHT_PURPLE);
    public static final ITextComponent DISPLAY_BLOOD = ITextComponent.translatable("school.irons_spellbooks.blood").withStyle(TextFormatting.DARK_RED);
    public static final ITextComponent DISPLAY_EVOCATION = ITextComponent.translatable("school.irons_spellbooks.evocation").withStyle(TextFormatting.WHITE);
    public static final ITextComponent DISPLAY_VOID = ITextComponent.translatable("school.irons_spellbooks.void").withStyle(Style.EMPTY.withColor(0x490059));
    public static final ITextComponent DISPLAY_POISON = ITextComponent.translatable("school.irons_spellbooks.poison").withStyle(TextFormatting.GREEN);
    public static final ITextComponent[] DISPLAYS = {DISPLAY_FIRE, DISPLAY_ICE, DISPLAY_LIGHTNING, DISPLAY_HOLY, DISPLAY_ENDER, DISPLAY_BLOOD, DISPLAY_EVOCATION, DISPLAY_VOID, DISPLAY_POISON};


}
