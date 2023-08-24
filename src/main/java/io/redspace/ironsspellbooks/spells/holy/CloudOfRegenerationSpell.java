package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.network.spell.ClientboundHealParticles;
import io.redspace.ironsspellbooks.network.spell.ClientboundRegenCloudParticles;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CloudOfRegenerationSpell extends AbstractSpell {
    public CloudOfRegenerationSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.healing", Utils.stringTruncation(getHealing(caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.radius", Utils.stringTruncation(radius, 1))
        );
    }

    public static final float radius = 5;

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.HOLY)
            .setMaxLevel(5)
            .setCooldownSeconds(35)
            .build();

    public CloudOfRegenerationSpell(int level) {
        super(SpellType.CLOUD_OF_REGENERATION_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 3;
        this.baseSpellPower = 2;
        this.spellPowerPerLevel = 1;
        this.castTime = 200;
        this.baseManaCost = 10;

    }

    private float getHealing(LivingEntity caster) {
        return getSpellPower(caster) * .5f;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.HOLY_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.CLOUD_OF_REGEN_LOOP.get());
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius)).forEach((target) -> {
            if (target.distanceToSqr(entity.position()) < radius * radius && Utils.shouldHealEntity(entity, target)) {
                target.heal(getHealing(entity));
                Messages.sendToPlayersTrackingEntity(new ClientboundHealParticles(target.position()), entity,true);
            }
        });
        Messages.sendToPlayersTrackingEntity(new ClientboundRegenCloudParticles(entity.position()), entity,true);

        super.onCast(level, entity, playerMagicData);
    }

    @Override
    public void onClientPreCast(World level, LivingEntity entity, Hand hand, @Nullable PlayerMagicData playerMagicData) {
        super.onClientPreCast(level, entity, hand, playerMagicData);
    }
}
