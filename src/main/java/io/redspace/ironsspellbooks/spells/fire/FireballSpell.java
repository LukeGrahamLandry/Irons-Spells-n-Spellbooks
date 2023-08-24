package io.redspace.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.fireball.MagicFireball;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class FireballSpell extends AbstractSpell {
    public FireballSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.radius", getRadius(caster))
        );
    }

//    public static DefaultConfig defaultConfig = new DefaultConfig((config) -> {
//        config.minRarity = SpellRarity.EPIC;
//        config.maxLevel = 8;
//    });

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchool(SchoolType.FIRE)
            .setMaxLevel(3)
            .setCooldownSeconds(25)
            .build();

    public FireballSpell(int level) {
        super(SpellType.FIREBALL_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 40;
        this.baseManaCost = 60;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.FIREBALL_START.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        Vector3d origin = entity.getEyePosition();

        MagicFireball fireball = new MagicFireball(world, entity);

        fireball.setDamage(getDamage(entity));
        fireball.setExplosionRadius(getRadius(entity));

        fireball.setPos(origin.add(entity.getForward()).subtract(0, fireball.getBbHeight() / 2, 0));
        fireball.shoot(entity.getLookAngle());

        world.addFreshEntity(fireball);
        super.onCast(world, entity, playerMagicData);
    }

    public float getDamage(LivingEntity caster) {
        return 10 * getSpellPower(caster);
    }

    public int getRadius(LivingEntity caster) {
        return (int) getSpellPower(caster);
    }
}
