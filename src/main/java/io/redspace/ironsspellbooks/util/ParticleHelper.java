package io.redspace.ironsspellbooks.util;

import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.particle.FogParticleOptions;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.particles.IParticleData;

public class ParticleHelper {
    //public static final ParticleOptions DRAGON_FIRE = ParticleRegistry.DRAGON_FIRE_PARTICLE.get();
    public static final IParticleData FIRE = ParticleRegistry.FIRE_PARTICLE.get();
    public static final IParticleData BLOOD = ParticleRegistry.BLOOD_PARTICLE.get();
    public static final IParticleData WISP = ParticleRegistry.WISP_PARTICLE.get();
    public static final IParticleData BLOOD_GROUND = ParticleRegistry.BLOOD_GROUND_PARTICLE.get();
    public static final IParticleData SNOWFLAKE = ParticleRegistry.SNOWFLAKE_PARTICLE.get();
    public static final IParticleData ELECTRICITY = ParticleRegistry.ELECTRICITY_PARTICLE.get();
    public static final IParticleData UNSTABLE_ENDER = ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get();
    public static final IParticleData EMBERS = ParticleRegistry.EMBER_PARTICLE.get();
    public static final IParticleData SIPHON = ParticleRegistry.SIPHON_PARTICLE.get();
    public static final IParticleData ACID = ParticleRegistry.ACID_PARTICLE.get();
    public static final IParticleData ACID_BUBBLE = ParticleRegistry.ACID_BUBBLE_PARTICLE.get();
    public static final IParticleData FOG = new FogParticleOptions(new Vector3f(1, 1, 1), 1);
    public static final IParticleData VOID_TENTACLE_FOG = new FogParticleOptions(new Vector3f(.09f, 0.075f, .11f), 2);
    public static final IParticleData ROOT_FOG = new FogParticleOptions(new Vector3f(61 / 255f, 40 / 255f, 18 / 255f), .4f);
    public static final IParticleData COMET_FOG = new FogParticleOptions(new Vector3f(.75f, .55f, 1f), 1.5f);
    public static final IParticleData POISON_CLOUD = new FogParticleOptions(new Vector3f(.08f, 0.64f, .16f), 1f);
    public static final IParticleData SUNBEAM = new FogParticleOptions(new Vector3f(0.95f, 0.97f, 0.36f), 1f);
}
