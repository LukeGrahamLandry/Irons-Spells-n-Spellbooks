package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class AcupunctureSpell extends AbstractSpell {
    public AcupunctureSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 2)),
                new TranslationTextComponent("ui.irons_spellbooks.projectile_count", getCount(caster)));

    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchool(SchoolType.BLOOD)
            .setMaxLevel(10)
            .setCooldownSeconds(20)
            .build();

    public AcupunctureSpell(int level) {
        super(SpellType.ACUPUNCTURE_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 0;
        this.baseManaCost = 25;


    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public boolean checkPreCastConditions(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, getSpellType(), 32, .15f);
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData targetData) {
            var targetEntity = targetData.getTarget((ServerWorld) world);
            if (targetEntity != null) {
                int count = getCount(entity);
                float damage = getDamage(entity);
                Vector3d center = targetEntity.position().add(0, targetEntity.getEyeHeight() / 2, 0);
                float degreesPerNeedle = 360f / count;
                for (int i = 0; i < count; i++) {
                    Vector3d offset = new Vector3d(0, Math.random(), .55).normalize().scale(targetEntity.getBbWidth() + 2.75f).yRot(degreesPerNeedle * i * MathHelper.DEG_TO_RAD);
                    Vector3d spawn = center.add(offset);
                    Vector3d motion = center.subtract(spawn).normalize();

                    BloodNeedle needle = new BloodNeedle(world, entity);
                    needle.moveTo(spawn);
                    needle.shoot(motion.scale(.35f));
                    needle.setDamage(damage);
                    needle.setScale(.4f);
                    world.addFreshEntity(needle);
                }
            }
        }

        super.onCast(world, entity, playerMagicData);
    }


    private int getCount(LivingEntity caster) {
        return (int) ((4 + getLevel(caster)) * getSpellPower(caster));
    }

    private float getDamage(LivingEntity caster) {
        return 1 + getSpellPower(caster);
    }
}
