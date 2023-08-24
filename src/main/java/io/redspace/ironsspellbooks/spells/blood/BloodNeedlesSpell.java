package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class BloodNeedlesSpell extends AbstractSpell {
    public BloodNeedlesSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(ITextComponent.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 2)),
                ITextComponent.translatable("ui.irons_spellbooks.projectile_count", getCount()));

    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.BLOOD)
            .setMaxLevel(10)
            .setCooldownSeconds(10)
            .build();

    public BloodNeedlesSpell(int level) {
        super(SpellType.BlOOD_NEEDLES_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 1;
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
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        int count = getCount();
        float damage = getDamage(entity);
        int degreesPerNeedle = 360 / count;
        var raycast = Utils.raycastForEntity(world, entity, 32, true);
        for (int i = 0; i < count; i++) {
            BloodNeedle needle = new BloodNeedle(world, entity);
            int rotation = degreesPerNeedle * i - (degreesPerNeedle / 2);
            needle.setDamage(damage);
            needle.setZRot(rotation);
            Vector3d spawn = entity.getEyePosition().add(new Vector3d(0, 1.5, 0).zRot(rotation * MathHelper.DEG_TO_RAD).xRot(-entity.getXRot() * MathHelper.DEG_TO_RAD).yRot(-entity.getYRot() * MathHelper.DEG_TO_RAD));
            needle.moveTo(spawn);
            needle.shoot(raycast.getLocation().subtract(spawn).normalize());
            world.addFreshEntity(needle);
        }
        super.onCast(world, entity, playerMagicData);
    }

//    public static final AnimationHolder SLASH_ANIMATION = new AnimationHolder("instant_slash", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
//
//    @Override
//    public AnimationHolder getCastStartAnimation() {
//        return SLASH_ANIMATION;
//    }

    private int getCount() {
        return 5;
//        return this.getRarity().getValue() + 3;
    }

    private float getDamage(LivingEntity caster) {
        return getSpellPower(caster) * .25f;
    }
}
