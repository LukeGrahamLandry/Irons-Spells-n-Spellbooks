package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.spells.ExtendedEvokerFang;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class FangStrikeSpell extends AbstractSpell {
    public FangStrikeSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.fang_count", getCount(caster)),
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.EVOCATION)
            .setMaxLevel(10)
            .setCooldownSeconds(5)
            .build();

    public FangStrikeSpell(int level) {
        super(SpellType.FANG_STRIKE_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 3;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 15;
        this.baseManaCost = 30;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_ATTACK);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        Vector3d forward = entity.getForward().multiply(1, 0, 1).normalize();
        Vector3d start = entity.getEyePosition().add(forward.scale(1.5));

        for (int i = 0; i < getCount(entity); i++) {
            Vector3d spawn = start.add(forward.scale(i));
            spawn = new Vector3d(spawn.x, getGroundLevel(world, spawn, 8), spawn.z);
            if (!world.getBlockState(new BlockPos(spawn).below()).isAir()) {
                int delay = i / 3;
                ExtendedEvokerFang fang = new ExtendedEvokerFang(world, spawn.x, spawn.y, spawn.z, (entity.getYRot() - 90) * MathHelper.DEG_TO_RAD, delay, entity, getDamage(entity));
                world.addFreshEntity(fang);
            }

        }
        super.onCast(world, entity, playerMagicData);
    }

    private int getGroundLevel(World level, Vector3d start, int maxSteps) {
        if (!level.getBlockState(new BlockPos(start)).isAir()) {
            for (int i = 0; i < maxSteps; i++) {
                start = start.add(0, 1, 0);
                if (level.getBlockState(new BlockPos(start)).isAir())
                    break;
            }
        }
        //Vec3 upper = level.clip(new ClipContext(start, start.add(0, maxSteps, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getLocation();
        Vector3d lower = level.clip(new RayTraceContext(start, start.add(0, maxSteps * -2, 0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null)).getLocation();
        return (int) lower.y;
    }

    @Override
    public boolean shouldAIStopCasting(AbstractSpellCastingMob mob, LivingEntity target) {
        float f = this.getCount(mob) * 1.2f;
        return mob.distanceToSqr(target) > (f * f);
    }

    private int getCount(LivingEntity entity) {
        return 7 + getLevel(entity);
    }

    private float getDamage(LivingEntity entity) {
        return getSpellPower(entity);
    }
}
