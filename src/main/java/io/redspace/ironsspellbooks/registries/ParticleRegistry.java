package io.redspace.ironsspellbooks.registries;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.particle.FogParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, IronsSpellbooks.MODID);

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

    /*
    To Create Particle:
    - textures + json
    - particle class
    - register it here
    - add it to particle helper
    - register it in client setup
     */

    public static final RegistryObject<BasicParticleType> BLOOD_PARTICLE = PARTICLE_TYPES.register("blood", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> WISP_PARTICLE = PARTICLE_TYPES.register("wisp", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> BLOOD_GROUND_PARTICLE = PARTICLE_TYPES.register("blood_ground", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> SNOWFLAKE_PARTICLE = PARTICLE_TYPES.register("snowflake", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> ELECTRICITY_PARTICLE = PARTICLE_TYPES.register("electricity", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> UNSTABLE_ENDER_PARTICLE = PARTICLE_TYPES.register("unstable_ender", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> DRAGON_FIRE_PARTICLE = PARTICLE_TYPES.register("dragon_fire", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> FIRE_PARTICLE = PARTICLE_TYPES.register("fire", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> EMBER_PARTICLE = PARTICLE_TYPES.register("embers", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> SIPHON_PARTICLE = PARTICLE_TYPES.register("spell", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> ACID_PARTICLE = PARTICLE_TYPES.register("acid", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> ACID_BUBBLE_PARTICLE = PARTICLE_TYPES.register("acid_bubble", () -> new BasicParticleType(false));
    public static final RegistryObject<ParticleType<FogParticleOptions>> FOG_PARTICLE = PARTICLE_TYPES.register("fog", () -> new ParticleType<FogParticleOptions>(false, FogParticleOptions.DESERIALIZER) {
        public Codec<FogParticleOptions> codec() {
            return FogParticleOptions.CODEC;
        }
    });

    public static final RegistryObject<ParticleType<ZapParticleOption>> ZAP_PARTICLE = PARTICLE_TYPES.register("zap", () -> new ParticleType<ZapParticleOption>(false, ZapParticleOption.DESERIALIZER) {
        public Codec<ZapParticleOption> codec() {
            return ZapParticleOption.CODEC;
        }
    });
    public static final RegistryObject<BasicParticleType> FIREFLY_PARTICLE = PARTICLE_TYPES.register("firefly", () -> new BasicParticleType(false));

}
