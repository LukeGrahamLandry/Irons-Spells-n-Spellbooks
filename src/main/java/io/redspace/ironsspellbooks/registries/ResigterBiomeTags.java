package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.biome.Biome;

public class ResigterBiomeTags {
    public static final TagKey<Biome> HAS_TOWER = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(IronsSpellbooks.MODID, "has_structure/tower"));

}
