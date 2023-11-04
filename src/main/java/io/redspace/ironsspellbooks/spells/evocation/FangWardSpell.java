package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.spells.ExtendedEvokerFang;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.BlockPos;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class FangWardSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "fang_ward");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList(new TranslationTextComponent("ui.irons_spellbooks.ring_count", getRings(spellLevel, caster)),
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)));
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(8)
            .setCooldownSeconds(15)
            .build();

    public FangWardSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 1;
        this.castTime = 15;
        this.baseManaCost = 45;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
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
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        int rings = getRings(spellLevel, entity);
        int count = 5;
        Vector3d center = entity.getEyePosition(0);

        for (int r = 0; r < rings; r++) {
            float fangs = count + r * r;
            for (int i = 0; i < fangs; i++) {
                Vector3d spawn = center.add(new Vector3d(0, 0, 1.5 * (r + 1)).yRot(entity.yRot * Utils.DEG_TO_RAD + ((6.281f / fangs) * i)));
                spawn = Utils.moveToRelativeGroundLevel(world, spawn, 5);
                if (!world.getBlockState(new BlockPos(spawn).below()).isAir()) {
                    ExtendedEvokerFang fang = new ExtendedEvokerFang(world, spawn.x, spawn.y, spawn.z, get2DAngle(center, spawn), r, entity, getDamage(spellLevel, entity));
                    world.addFreshEntity(fang);
                }
            }
        }
        super.onCast(world, spellLevel, entity, playerMagicData);
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

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity);
    }

    private int getRings(int spellLevel, LivingEntity entity) {
        return 2 + (getLevel(spellLevel, entity) - 1) / 3;
    }
}
