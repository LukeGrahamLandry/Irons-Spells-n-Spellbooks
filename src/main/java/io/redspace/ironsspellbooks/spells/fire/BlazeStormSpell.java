package io.redspace.ironsspellbooks.spells.fire;


import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.fireball.SmallMagicFireball;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BlazeStormSpell extends AbstractSpell {
    public BlazeStormSpell() {
        this(1);
    }
    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.FIRE)
            .setMaxLevel(10)
            .setCooldownSeconds(20)
            .build();

    public BlazeStormSpell(int level) {
        super(SpellType.BLAZE_STORM_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 1;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 80 + 5 * level;
        this.baseManaCost = 5;

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.BLAZE_AMBIENT);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        super.onCast(world, entity, playerMagicData);
    }

    @Override
    public void onServerCastTick(World level, LivingEntity entity, @Nullable PlayerMagicData playerMagicData) {
        if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 5 == 0)
            shootBlazeFireball(level, entity);
    }

    private float getDamage(LivingEntity caster) {
        return getSpellPower(caster) * .4f;
    }

    public void shootBlazeFireball(World world, LivingEntity entity) {
        Vector3d origin = entity.getEyePosition().add(entity.getForward().normalize().scale(.2f));
        SmallMagicFireball fireball = new SmallMagicFireball(world, entity);
        fireball.setPos(origin.subtract(0, fireball.getBbHeight(), 0));
        fireball.shoot(entity.getLookAngle(), .05f);
        fireball.setDamage(getDamage(entity));
        world.playSound(null, origin.x, origin.y, origin.z, SoundEvents.BLAZE_SHOOT, SoundCategory.PLAYERS, 2.0f, 1.0f);
        world.addFreshEntity(fireball);
    }

    @Override
    protected void playSound(Optional<SoundEvent> sound, Entity entity, boolean playDefaultSound) {
        super.playSound(sound, entity, false);
    }
}
