package io.redspace.ironsspellbooks.spells.nature;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrb;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class AcidOrbSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "acid_orb");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                ITextComponent.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(spellLevel, caster), 1)),
                ITextComponent.translatable("ui.irons_spellbooks.rend", Utils.stringTruncation((getRendAmplifier(spellLevel, caster) + 1) * 5, 1)),
                ITextComponent.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getRendDuration(spellLevel, caster), 1)));
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(15)
            .build();

    public AcidOrbSpell() {
        this.manaCostPerLevel = 3;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 15;
        this.baseManaCost = 30;
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
        return Optional.of(SoundRegistry.ACID_ORB_CHARGE.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.ACID_ORB_CAST.get());
    }

    @Override
    public void onCast(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        AcidOrb orb = new AcidOrb(level, entity);
        orb.setPos(entity.position().add(0, entity.getEyeHeight() - orb.getBoundingBox().getYsize() * .5f, 0).add(entity.getForward()));
        orb.shoot(entity.getLookAngle());
        orb.setDeltaMovement(orb.getDeltaMovement().add(0, 0.2, 0));
        orb.setExplosionRadius(getRadius(spellLevel, entity));
        orb.setRendLevel(getRendAmplifier(spellLevel, entity));
        orb.setRendDuration(getRendDuration(spellLevel, entity));
        level.addFreshEntity(orb);
        super.onCast(level, spellLevel, entity, playerMagicData);
    }

    public float getRadius(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) * 3;
    }

    public int getRendAmplifier(int spellLevel, LivingEntity caster) {
        return (int) (getSpellPower(spellLevel, caster) * this.getLevel(spellLevel, caster) - 1);
    }

    public int getRendDuration(int spellLevel, LivingEntity caster) {
        return (int) (getSpellPower(spellLevel, caster) * 20 * 15);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_SPIT_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.SPIT_FINISH_ANIMATION;
    }
}
