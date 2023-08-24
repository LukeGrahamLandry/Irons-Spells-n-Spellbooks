package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
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
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class FangWardSpell extends AbstractSpell {
    public FangWardSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(ITextComponent.translatable("ui.irons_spellbooks.ring_count", getRings(caster)),
                ITextComponent.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.EVOCATION)
            .setMaxLevel(8)
            .setCooldownSeconds(15)
            .build();

    public FangWardSpell(int level) {
        super(SpellType.FANG_WARD_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 1;
        this.castTime = 15;
        this.baseManaCost = 45;
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
        int rings = getRings(entity);
        int count = 5;
        Vector3d center = entity.getEyePosition();

        for (int r = 0; r < rings; r++) {
            float fangs = count + r * r;
            for (int i = 0; i < fangs; i++) {
                Vector3d spawn = center.add(new Vector3d(0, 0, 1.5 * (r + 1)).yRot(entity.getYRot() * MathHelper.DEG_TO_RAD + ((6.281f / fangs) * i)));
                spawn = new Vector3d(spawn.x, Utils.findRelativeGroundLevel(world, spawn, 5), spawn.z);
                if (!world.getBlockState(new BlockPos(spawn).below()).isAir()) {
                    ExtendedEvokerFang fang = new ExtendedEvokerFang(world, spawn.x, spawn.y, spawn.z, get2DAngle(center, spawn), r, entity, getDamage(entity));
                    world.addFreshEntity(fang);
                }
            }
        }
        super.onCast(world, entity, playerMagicData);
    }

    private float get2DAngle(Vector3d a, Vector3d b) {
        return Utils.getAngle(new Vector2f((float) a.x, (float) a.z), new Vector2f((float) b.x, (float) b.z));
    }

//    private int getGroundLevel(Level level, Vec3 start, int maxSteps) {
//        if (!level.getBlockState(new BlockPos(start)).isAir()) {
//            for (int i = 0; i < maxSteps; i++) {
//                start = start.add(0, 1, 0);
//                if (level.getBlockState(new BlockPos(start)).isAir())
//                    break;
//            }
//        }
//        //Vec3 upper = level.clip(new ClipContext(start, start.add(0, maxSteps, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getLocation();
//        Vec3 lower = level.clip(new ClipContext(start, start.add(0, maxSteps * -2, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getLocation();
//        return (int) lower.y;
//    }

    private float getDamage(LivingEntity entity) {
        return getSpellPower(entity);
    }

    private int getRings(LivingEntity entity) {
        return 2 + (getLevel(entity) - 1) / 3;
    }
}
