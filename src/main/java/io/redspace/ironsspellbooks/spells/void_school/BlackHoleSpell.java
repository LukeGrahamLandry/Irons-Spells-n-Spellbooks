package io.redspace.ironsspellbooks.spells.void_school;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import software.bernie.geckolib3.core.builder.ILoopType;

import java.util.List;
import java.util.Optional;

public class BlackHoleSpell extends AbstractSpell {
    public BlackHoleSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                ITextComponent.translatable("ui.irons_spellbooks.aoe_damage", Utils.stringTruncation(getDamage(caster), 1)),
                ITextComponent.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(caster), 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchool(SchoolType.VOID)
            .setMaxLevel(6)
            .setCooldownSeconds(120)
            .build();

    public BlackHoleSpell(int level) {
        super(SpellType.BLACK_HOLE_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 100;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 100;
        this.baseManaCost = 300;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.BLACK_HOLE_CHARGE.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.BLACK_HOLE_CAST.get());
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        float radius = getRadius(entity);

        RayTraceResult raycast = Utils.raycastForEntity(level, entity, 16 + radius * 1.5f, true);
        Vector3d center = raycast.getLocation();
        if (raycast instanceof BlockRayTraceResult blockHitResult) {
            if (blockHitResult.getDirection().getAxis().isHorizontal())
                center = center.subtract(0, radius, 0);
            else if (blockHitResult.getDirection() == Direction.DOWN)
                center = center.subtract(0, radius * 2, 0);
        }


        level.playSound(null, center.x, center.y, center.z, SoundRegistry.BLACK_HOLE_CAST.get(), SoundCategory.AMBIENT, 4, 1);

        BlackHole blackHole = new BlackHole(level, entity);
        blackHole.setRadius(radius);
        blackHole.setDamage(getDamage(entity));
        blackHole.moveTo(center);
        level.addFreshEntity(blackHole);
        super.onCast(level, entity, playerMagicData);
    }

    private float getDamage(LivingEntity entity) {
        return getSpellPower(entity) * 2;
    }

    private float getRadius(LivingEntity entity) {
        return (2 * getLevel(entity) + 4) * getSpellPower(entity);
    }

    public static final AnimationHolder CHARGE_ANIMATION = new AnimationHolder("charge_black_hole", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    public static final AnimationHolder FINISH_ANIMATION = new AnimationHolder("long_cast_finish", ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    @Override
    public AnimationHolder getCastStartAnimation() {
        return CHARGE_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return FINISH_ANIMATION;
    }
}
