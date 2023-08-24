package io.redspace.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.FireBomb;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class MagmaBombSpell extends AbstractSpell {

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.aoe_damage", Utils.stringTruncation(getDamage(caster) / 5f, 1)),
                new TranslationTextComponent("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(caster), 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.FIRE)
            .setMaxLevel(8)
            .setCooldownSeconds(12)
            .build();

    public MagmaBombSpell(int level) {
        super(SpellType.MAGMA_BOMB_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 30;

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.FIRE_BOMB_CHARGE.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.FIRE_BOMB_CAST.get());
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        FireBomb orb = new FireBomb(level, entity);
        orb.setPos(entity.position().add(0, entity.getEyeHeight() - orb.getBoundingBox().getYsize() * .5f, 0).add(entity.getForward()));
        orb.shoot(entity.getLookAngle());
        orb.setDeltaMovement(orb.getDeltaMovement().add(0, 0.2, 0));
        orb.setExplosionRadius(getRadius(entity));
        orb.setDamage(getDamage(entity));
        level.addFreshEntity(orb);
        super.onCast(level, entity, playerMagicData);
    }

    public float getRadius(LivingEntity caster) {
        return 5;
    }

    public float getDamage(LivingEntity caster) {
        return getSpellPower(caster);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return ANIMATION_CHARGED_CAST;
    }
}
