package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.entity.mobs.SummonedVex;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class SummonVexSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "summon_vex");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.summon_count", getLevel(spellLevel, caster))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(150)
            .build();

    public SummonVexSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 20;
        this.baseManaCost = 50;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.EVOKER_CAST_SPELL);
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        int summonTime = 20 * 60 * 10;

        int level = getLevel(spellLevel, entity);
        for (int i = 0; i < this.getLevel(spellLevel, entity); i++) {
            SummonedVex vex = new SummonedVex(world, entity);
            vex.moveTo(entity.getEyePosition(0).add(new Vector3d(Utils.getRandomScaled(2), 1, Utils.getRandomScaled(2))));
            vex.finalizeSpawn((ServerWorld) world, world.getCurrentDifficultyAt(vex.getOnPos()), SpawnReason.MOB_SUMMONED, null, null);
            vex.addEffect(new EffectInstance(MobEffectRegistry.VEX_TIMER.get(), summonTime, 0, false, false, false));
            world.addFreshEntity(vex);
        }
        int effectAmplifier = level - 1;
        if (entity.hasEffect(MobEffectRegistry.VEX_TIMER.get()))
            effectAmplifier += entity.getEffect(MobEffectRegistry.VEX_TIMER.get()).getAmplifier() + 1;
        entity.addEffect(new EffectInstance(MobEffectRegistry.VEX_TIMER.get(), summonTime, effectAmplifier, false, false, true));
        super.onCast(world, spellLevel, entity, playerMagicData);
    }
}
