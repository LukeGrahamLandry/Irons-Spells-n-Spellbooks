package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.network.spell.ClientboundAborptionParticles;
import io.redspace.ironsspellbooks.network.spell.ClientboundFortifyAreaParticles;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FortifySpell extends AbstractSpell {
    public FortifySpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.absorption", Utils.stringTruncation(getSpellPower(caster), 0)),
                new TranslationTextComponent("ui.irons_spellbooks.radius", Utils.stringTruncation(radius, 1))
        );
    }

    public static final float radius = 16;

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.HOLY)
            .setMaxLevel(10)
            .setCooldownSeconds(35)
            .build();

    public FortifySpell(int level) {
        super(SpellType.FORTIFY_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 40;
        this.baseManaCost = 40;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.CLOUD_OF_REGEN_LOOP.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onServerPreCast(World level, LivingEntity entity, @Nullable PlayerMagicData playerMagicData) {
        super.onServerPreCast(level, entity, playerMagicData);
        if (playerMagicData == null)
            return;
        TargetedAreaEntity targetedAreaEntity = TargetedAreaEntity.createTargetAreaEntity(level, entity.position(), radius, 16239960);
        targetedAreaEntity.setOwner(entity);
        playerMagicData.setAdditionalCastData(new TargetAreaCastData(entity.position(), targetedAreaEntity));
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(entity.position().subtract(radius, radius, radius), entity.position().add(radius, radius, radius))).forEach((target) -> {
            if (Utils.shouldHealEntity(entity, target) && entity.distanceTo(target) <= radius) {
                target.addEffect(new EffectInstance(MobEffectRegistry.FORTIFY.get(), 20 * 120, (int) getSpellPower(entity), false, false, true));
                Messages.sendToPlayersTrackingEntity(new ClientboundAborptionParticles(target.position()), entity, true);

            }
        });
        Messages.sendToPlayersTrackingEntity(new ClientboundFortifyAreaParticles(entity.position()), entity, true);

        super.onCast(level, entity, playerMagicData);
    }
}
