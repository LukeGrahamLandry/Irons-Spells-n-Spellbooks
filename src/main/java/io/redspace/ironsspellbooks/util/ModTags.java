package io.redspace.ironsspellbooks.util;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModTags {
    public static final TagKey<Item> SCHOOL_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID,"school_focus"));
    public static final TagKey<Item> FIRE_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "fire_focus"));
    public static final TagKey<Item> ICE_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "ice_focus"));
    public static final TagKey<Item> LIGHTNING_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "lightning_focus"));
    public static final TagKey<Item> ENDER_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "ender_focus"));
    public static final TagKey<Item> HOLY_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "holy_focus"));
    public static final TagKey<Item> BLOOD_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "blood_focus"));
    public static final TagKey<Item> EVOCATION_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "evocation_focus"));
    public static final TagKey<Item> VOID_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "void_focus"));
    public static final TagKey<Item> POISON_FOCUS = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "poison_focus"));
    public static final TagKey<Item> CAN_BE_UPGRADED = ItemTags.create(new ResourceLocation(IronsSpellbooks.MODID, "can_be_upgraded"));
    public static final TagKey<Block> SPECTRAL_HAMMER_MINEABLE = BlockTags.create(new ResourceLocation(IronsSpellbooks.MODID, "spectral_hammer_mineable"));

    public static final TagKey<Structure> WAYWARD_COMPASS_LOCATOR = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "wayward_compass_locator"));
    public static final TagKey<Structure> ANTIQUATED_COMPASS_LOCATOR = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "antiquated_compass_locator"));
    public static final RegistryKey<Structure> MAGIC_AURA_TEMP = RegistryKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "citadel"));

    public static final TagKey<EntityType<?>> ALWAYS_HEAL = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "always_heal"));
    public static final TagKey<EntityType<?>> CANT_ROOT = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "cant_root"));
    public static final TagKey<EntityType<?>> VILLAGE_ALLIES = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "village_allies"));

}
