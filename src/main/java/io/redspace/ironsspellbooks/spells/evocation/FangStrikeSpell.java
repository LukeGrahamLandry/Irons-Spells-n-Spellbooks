package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.spells.ExtendedEvokerFang;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class FangStrikeSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "fang_strike");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.fang_count", getCount(spellLevel, caster)),
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)));
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(5)
            .build();

    public FangStrikeSpell() {
        this.manaCostPerLevel = 3;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 15;
        this.baseManaCost = 30;
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
        Vector3d forward = entity.getForward().multiply(1, 0, 1).normalize();
        Vector3d start = entity.getEyePosition(0).add(forward.scale(1.5));

        for (int i = 0; i < getCount(spellLevel, entity); i++) {
            Vector3d spawn = start.add(forward.scale(i));
            spawn = new Vector3d(spawn.x, getGroundLevel(world, spawn, 8), spawn.z);
            if (!world.getBlockState(new BlockPos(spawn).below()).isAir()) {
                int delay = i / 3;
                ExtendedEvokerFang fang = new ExtendedEvokerFang(world, spawn.x, spawn.y, spawn.z, (entity.yRot - 90) * Utils.DEG_TO_RAD, delay, entity, getDamage(spellLevel, entity));
                world.addFreshEntity(fang);
            }
        }
        super.onCast(world, spellLevel, entity, playerMagicData);
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
    public boolean shouldAIStopCasting(int spellLevel, MobEntity mob, LivingEntity target) {
        float f = this.getCount(spellLevel, mob) * 1.2f;
        return mob.distanceToSqr(target) > (f * f);
    }

    private int getCount(int spellLevel, LivingEntity entity) {
        return 7 + getLevel(spellLevel, entity);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity);
    }
}
