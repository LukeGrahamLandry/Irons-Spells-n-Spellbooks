package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.caelus.api.CaelusApi;

public class MobEffectRegistry {
    public static final DeferredRegister<Effect> MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registry.MOB_EFFECT_REGISTRY, IronsSpellbooks.MODID);

    public static void register(IEventBus eventBus) {
        MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
    }

    //public static final RegistryObject<MobEffect> BLOOD_SLASHED = MOB_EFFECT_DEFERRED_REGISTER.register("blood_slashed", () -> new BloodSlashed(MobEffectCategory.HARMFUL, 0xff4800));
    public static final RegistryObject<Effect> ANGEL_WINGS = MOB_EFFECT_DEFERRED_REGISTER.register("angel_wings", () -> new AngelWingsEffect(EffectType.BENEFICIAL, 0xbea925).addAttributeModifier(CaelusApi.getInstance().getFlightAttribute(), "748D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 1, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<Effect> EVASION = MOB_EFFECT_DEFERRED_REGISTER.register("evasion", () -> new EvasionEffect(EffectType.BENEFICIAL, 0x9f0be3));
    public static final RegistryObject<Effect> HEARTSTOP = MOB_EFFECT_DEFERRED_REGISTER.register("heartstop", () -> new HeartstopEffect(EffectType.BENEFICIAL, 4393481));
    //public static final RegistryObject<MobEffect> SUMMON_TIMER = MOB_EFFECT_DEFERRED_REGISTER.register("summon_timer", () -> new SummonTimer(MobEffectCategory.NEUTRAL, 0xbea925));
    public static final RegistryObject<SummonTimer> VEX_TIMER = MOB_EFFECT_DEFERRED_REGISTER.register("vex_timer", () -> new SummonTimer(EffectType.BENEFICIAL, 0xbea925));
    public static final RegistryObject<SummonTimer> POLAR_BEAR_TIMER = MOB_EFFECT_DEFERRED_REGISTER.register("polar_bear_timer", () -> new SummonTimer(EffectType.BENEFICIAL, 0xbea925));
    public static final RegistryObject<SummonTimer> RAISE_DEAD_TIMER = MOB_EFFECT_DEFERRED_REGISTER.register("raise_dead_timer", () -> new SummonTimer(EffectType.BENEFICIAL, 0xbea925));
    public static final RegistryObject<SummonTimer> SUMMON_HORSE_TIMER = MOB_EFFECT_DEFERRED_REGISTER.register("summon_horse_timer", () -> new SummonTimer(EffectType.BENEFICIAL, 0xbea925));
    public static final RegistryObject<Effect> ABYSSAL_SHROUD = MOB_EFFECT_DEFERRED_REGISTER.register("abyssal_shroud", () -> new AbyssalShroudEffect(EffectType.BENEFICIAL, 0));
    public static final RegistryObject<Effect> ASCENSION = MOB_EFFECT_DEFERRED_REGISTER.register("ascension", () -> new AscensionEffect(EffectType.BENEFICIAL, 0xbea925).addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(), "467D7064-6A45-4F59-8ABE-C2C93A6DD7A9", -.85f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<Effect> CHARGED = MOB_EFFECT_DEFERRED_REGISTER.register("charged", () -> new ChargeEffect(EffectType.BENEFICIAL, 3311322).addAttributeModifier(Attributes.ATTACK_DAMAGE, "87733c95-909c-4fc3-9780-e35a89565666", ChargeEffect.ATTACK_DAMAGE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.MOVEMENT_SPEED, "87733c95-909c-4fc3-9780-e35a89565666", ChargeEffect.SPEED_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(AttributeRegistry.SPELL_POWER.get(), "87733c95-909c-4fc3-9780-e35a89565666", ChargeEffect.SPELL_POWER_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<Effect> TRUE_INVISIBILITY = MOB_EFFECT_DEFERRED_REGISTER.register("true_invisibility", () -> new TrueInvisibilityEffect(EffectType.BENEFICIAL, 8356754));
    public static final RegistryObject<Effect> FORTIFY = MOB_EFFECT_DEFERRED_REGISTER.register("fortify", () -> new FortifyEffect(EffectType.BENEFICIAL, 16239960));
    public static final RegistryObject<Effect> REND = MOB_EFFECT_DEFERRED_REGISTER.register("rend", () -> new RendEffect(EffectType.HARMFUL, 4800826).addAttributeModifier(Attributes.ARMOR, "01efe86c-d40e-4199-b635-1782f9fcbe03", RendEffect.ARMOR_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<Effect> SPIDER_ASPECT = MOB_EFFECT_DEFERRED_REGISTER.register("spider_aspect", () -> new SpiderAspectEffect(EffectType.BENEFICIAL, 4800826));
    public static final RegistryObject<Effect> BLIGHT = MOB_EFFECT_DEFERRED_REGISTER.register("blight", () -> new BlightEffect(EffectType.HARMFUL, 0xdfff2b));
    public static final RegistryObject<Effect> GUIDING_BOLT = MOB_EFFECT_DEFERRED_REGISTER.register("guided", () -> new GuidingBoltEffect(EffectType.HARMFUL, 16239960));
    public static final RegistryObject<Effect> AIRBORNE = MOB_EFFECT_DEFERRED_REGISTER.register("airborne", () -> new AirborneEffect(EffectType.HARMFUL, 0xFFFFFF));
    public static final RegistryObject<Effect> VIGOR = MOB_EFFECT_DEFERRED_REGISTER.register("vigor", () -> new Effect(EffectType.BENEFICIAL, 0x850d0d) {}.addAttributeModifier(Attributes.MAX_HEALTH, "d2b7228b-3ded-412e-940b-8f9f1e2cf882", 2, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<Effect> INSTANT_MANA = MOB_EFFECT_DEFERRED_REGISTER.register("instant_mana", () -> new InstantManaEffect(EffectType.BENEFICIAL, 0x00b7ec) );
    public static final RegistryObject<Effect> OAKSKIN = MOB_EFFECT_DEFERRED_REGISTER.register("oakskin", () -> new OakskinEffect(EffectType.BENEFICIAL, 0xffef95) );
    //public static final RegistryObject<MobEffect> ROOT = MOB_EFFECT_DEFERRED_REGISTER.register("root", () -> new RootEffect(MobEffectCategory.HARMFUL, 0x604730));
    //public static final RegistryObject<MobEffect> ENCHANTED_WARD = MOB_EFFECT_DEFERRED_REGISTER.register("enchanted_ward", () -> new EnchantedWardEffect(MobEffectCategory.HARMFUL, 3311322));
}

