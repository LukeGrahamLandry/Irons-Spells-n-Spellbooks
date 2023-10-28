package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.worldgen.RemoveWaterProcessor;
import net.minecraft.util.registry.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.RegistryObject;

public class StructureProcessorRegistry {

    public static final DeferredRegister<IStructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registry.STRUCTURE_PROCESSOR_REGISTRY, IronsSpellbooks.MODID);

    public static void register(IEventBus eventBus) {
        PROCESSORS.register(eventBus);
    }

    public static final RegistryObject<IStructureProcessorType<RemoveWaterProcessor>> REMOVE_WATER = PROCESSORS.register("remove_water", () -> () -> RemoveWaterProcessor.CODEC);
}
