package io.redspace.ironsspellbooks.api.registry;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.render.AffinityRingRenderer;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SchoolRegistry {
    public static final RegistryKey<Registry<SchoolType>> SCHOOL_REGISTRY_KEY = RegistryKey.createRegistryKey(new ResourceLocation(IronsSpellbooks.MODID, "schools"));
    private static final DeferredRegister<SchoolType> SCHOOLS = DeferredRegister.create(SCHOOL_REGISTRY_KEY, IronsSpellbooks.MODID);
    public static final Supplier<IForgeRegistry<SchoolType>> REGISTRY = SCHOOLS.makeRegistry(() -> new RegistryBuilder<SchoolType>().disableSaving().disableOverrides());

    public static void register(IEventBus eventBus) {
        SCHOOLS.register(eventBus);
        eventBus.addListener(SchoolRegistry::clientSetup);
    }

    private static RegistryObject<SchoolType> registerSchool(SchoolType schoolType) {
        return SCHOOLS.register(schoolType.getId().getPath(), () -> schoolType);
    }

    public static SchoolType getSchool(ResourceLocation resourceLocation) {
        return REGISTRY.get().getValue(resourceLocation);
    }

    public static final ResourceLocation FIRE_RESOURCE = IronsSpellbooks.id("fire");
    public static final ResourceLocation ICE_RESOURCE = IronsSpellbooks.id("ice");
    public static final ResourceLocation LIGHTNING_RESOURCE = IronsSpellbooks.id("lightning");
    public static final ResourceLocation HOLY_RESOURCE = IronsSpellbooks.id("holy");
    public static final ResourceLocation ENDER_RESOURCE = IronsSpellbooks.id("ender");
    public static final ResourceLocation BLOOD_RESOURCE = IronsSpellbooks.id("blood");
    public static final ResourceLocation EVOCATION_RESOURCE = IronsSpellbooks.id("evocation");
    public static final ResourceLocation NATURE_RESOURCE = IronsSpellbooks.id("nature");

    public static final RegistryObject<SchoolType> FIRE = registerSchool(new SchoolType(
            FIRE_RESOURCE,
            ModTags.FIRE_FOCUS,
            ITextComponent.translatable("school.irons_spellbooks.fire").withStyle(TextFormatting.GOLD),
            LazyOptional.of(AttributeRegistry.FIRE_SPELL_POWER::get),
            LazyOptional.of(AttributeRegistry.FIRE_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.FIRE_CAST::get)));

    public static final RegistryObject<SchoolType> ICE = registerSchool(new SchoolType(
            ICE_RESOURCE,
            ModTags.ICE_FOCUS,
            ITextComponent.translatable("school.irons_spellbooks.ice").withStyle(Style.EMPTY.withColor(0xd0f9ff)),
            LazyOptional.of(AttributeRegistry.ICE_SPELL_POWER::get),
            LazyOptional.of(AttributeRegistry.ICE_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.ICE_CAST::get)
    ));

    public static final RegistryObject<SchoolType> LIGHTNING = registerSchool(new SchoolType(
            LIGHTNING_RESOURCE,
            ModTags.LIGHTNING_FOCUS,
            ITextComponent.translatable("school.irons_spellbooks.lightning").withStyle(TextFormatting.AQUA),
            LazyOptional.of(AttributeRegistry.LIGHTNING_SPELL_POWER::get),
            LazyOptional.of(AttributeRegistry.LIGHTNING_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.LIGHTNING_CAST::get)
    ));

    public static final RegistryObject<SchoolType> HOLY = registerSchool(new SchoolType(
            HOLY_RESOURCE,
            ModTags.HOLY_FOCUS,
            ITextComponent.translatable("school.irons_spellbooks.holy").withStyle(Style.EMPTY.withColor(0xfff8d4)),
            LazyOptional.of(AttributeRegistry.HOLY_SPELL_POWER::get),
            LazyOptional.of(AttributeRegistry.HOLY_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.HOLY_CAST::get)
    ));

    public static final RegistryObject<SchoolType> ENDER = registerSchool(new SchoolType(
            ENDER_RESOURCE,
            ModTags.ENDER_FOCUS,
            ITextComponent.translatable("school.irons_spellbooks.ender").withStyle(TextFormatting.LIGHT_PURPLE),
            LazyOptional.of(AttributeRegistry.ENDER_SPELL_POWER::get),
            LazyOptional.of(AttributeRegistry.ENDER_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.ENDER_CAST::get)
    ));

    public static final RegistryObject<SchoolType> BLOOD = registerSchool(new SchoolType(
            BLOOD_RESOURCE,
            ModTags.BLOOD_FOCUS,
            ITextComponent.translatable("school.irons_spellbooks.blood").withStyle(TextFormatting.DARK_RED),
            LazyOptional.of(AttributeRegistry.BLOOD_SPELL_POWER::get),
            LazyOptional.of(AttributeRegistry.BLOOD_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.BLOOD_CAST::get)));

    public static final RegistryObject<SchoolType> EVOCATION = registerSchool(new SchoolType(
            EVOCATION_RESOURCE,
            ModTags.EVOCATION_FOCUS,
            ITextComponent.translatable("school.irons_spellbooks.evocation").withStyle(TextFormatting.WHITE),
            LazyOptional.of(AttributeRegistry.EVOCATION_SPELL_POWER::get),
            LazyOptional.of(AttributeRegistry.EVOCATION_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.EVOCATION_CAST::get)
    ));

    public static final RegistryObject<SchoolType> NATURE = registerSchool(new SchoolType(
            NATURE_RESOURCE,
            ModTags.NATURE_FOCUS,
            ITextComponent.translatable("school.irons_spellbooks.nature").withStyle(TextFormatting.GREEN),
            LazyOptional.of(AttributeRegistry.NATURE_SPELL_POWER::get),
            LazyOptional.of(AttributeRegistry.NATURE_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.NATURE_CAST::get)
    ));

    @Nullable
    public static SchoolType getSchoolFromFocus(ItemStack focusStack) {
        for (SchoolType school : REGISTRY.get().getValues()) {
            if (school.isFocus(focusStack)) {
                return school;
            }
        }
        return null;
    }

    public static void clientSetup(ModelEvent.RegisterAdditional event) {
        for (SchoolType schoolType : REGISTRY.get().getValues()) {
            event.register(AffinityRingRenderer.getAffinityRingModelLocation(schoolType.getId()));
        }
    }
}
