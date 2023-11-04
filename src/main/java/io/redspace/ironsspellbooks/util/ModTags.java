package io.redspace.ironsspellbooks.util;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class ModTags {
    public static final IOptionalNamedTag<Item> SCHOOL_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID,"school_focus"));
    public static final IOptionalNamedTag<Item> FIRE_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "fire_focus"));
    public static final IOptionalNamedTag<Item> ICE_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "ice_focus"));
    public static final IOptionalNamedTag<Item> LIGHTNING_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "lightning_focus"));
    public static final IOptionalNamedTag<Item> ENDER_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "ender_focus"));
    public static final IOptionalNamedTag<Item> HOLY_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "holy_focus"));
    public static final IOptionalNamedTag<Item> BLOOD_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "blood_focus"));
    public static final IOptionalNamedTag<Item> EVOCATION_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "evocation_focus"));
    public static final IOptionalNamedTag<Item> VOID_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "void_focus"));
    public static final IOptionalNamedTag<Item> NATURE_FOCUS = ItemTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "nature_focus"));
    public static final IOptionalNamedTag<Block> SPECTRAL_HAMMER_MINEABLE = BlockTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "spectral_hammer_mineable"));

    // TODO: not coping with world gen yet
//    public static final TagKey<Structure> WAYWARD_COMPASS_LOCATOR = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "wayward_compass_locator"));
//    public static final TagKey<Structure> ANTIQUATED_COMPASS_LOCATOR = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "antiquated_compass_locator"));
//    public static final RegistryKey<Structure> MAGIC_AURA_TEMP = RegistryKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "citadel"));

    public static final IOptionalNamedTag<EntityType<?>> ALWAYS_HEAL = EntityTypeTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "always_heal"));
    public static final IOptionalNamedTag<EntityType<?>> CANT_ROOT = EntityTypeTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "cant_root"));
    public static final IOptionalNamedTag<EntityType<?>> VILLAGE_ALLIES = EntityTypeTags.createOptional(new ResourceLocation(IronsSpellbooks.MODID, "village_allies"));

}
