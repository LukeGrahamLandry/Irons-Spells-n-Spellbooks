// TODO: figure out worldgen stuff

//package io.redspace.ironsspellbooks.registries;
//
//import com.google.common.base.Suppliers;
//import io.redspace.ironsspellbooks.IronsSpellbooks;
//import net.minecraft.util.registry.Registry;
//import net.minecraft.world.gen.feature.ConfiguredFeature;
//import net.minecraft.world.gen.feature.Feature;
//import net.minecraft.world.gen.feature.OreFeatureConfig;
//import net.minecraftforge.eventbus.api.IEventBus;
//import net.minecraftforge.registries.DeferredRegister;
//import net.minecraftforge.fml.RegistryObject;
//
//import java.util.List;
//import java.util.function.Supplier;
//
//public class FeatureRegistry {
//    private static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, IronsSpellbooks.MODID);
//    private static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, IronsSpellbooks.MODID);
//
//    public static void register(IEventBus eventBus) {
//        CONFIGURED_FEATURES.register(eventBus);
//        PLACED_FEATURES.register(eventBus);
//    }
//
//    /*
//        Arcane Debris
//     */
//    //What blocks the ore can generate in
//    public static final Supplier<List<OreFeatureConfig.TargetBlockState>> ARCANE_DEBRIS_ORE_TARGET = Suppliers.memoize(() -> Arrays.asList(
//            OreFeatureConfig.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, BlockRegistry.ARCANE_DEBRIS.get().defaultBlockState())
//    ));
//    //Vein size/conditions (this ore cannot spawn exposed to air)
//    public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_ARCANE_DEBRIS = CONFIGURED_FEATURES.register("ore_arcane_debris",
//            () -> new ConfiguredFeature<>(Feature.SCATTERED_ORE, new OreFeatureConfig(ARCANE_DEBRIS_ORE_TARGET.get(), 3, 1.0f)));
//
//    public static final RegistryObject<PlacedFeature> ORE_ARCANE_DEBRIS_FEATURE = PLACED_FEATURES.register("ore_arcane_debris_feature",
//            () -> new PlacedFeature(ORE_ARCANE_DEBRIS.getHolder().get(),
//                    Arrays.asList(InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-38)))));
//
//    //Copied private helpers from OrePlacements
//    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
//        return Arrays.asList(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
//    }
//
//    private static List<PlacementModifier> commonOrePlacement(int pCount, PlacementModifier pHeightRange) {
//        return orePlacement(CountPlacement.of(pCount), pHeightRange);
//    }
//
//    private static List<PlacementModifier> rareOrePlacement(int pChance, PlacementModifier pHeightRange) {
//        return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
//    }
//}
