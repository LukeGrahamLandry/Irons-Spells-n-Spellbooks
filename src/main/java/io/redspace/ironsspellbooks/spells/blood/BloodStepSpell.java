package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BloodStepSpell extends AbstractSpell {
    public BloodStepSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(ITextComponent.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getDistance(caster), 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.BLOOD)
            .setMaxLevel(5)
            .setCooldownSeconds(5)
            .build();

    public BloodStepSpell(int level) {
        super(SpellType.BLOOD_STEP_SPELL);
        this.setLevel(level);
        this.baseSpellPower = 12;
        this.spellPowerPerLevel = 4;
        this.baseManaCost = 30;
        this.manaCostPerLevel = 10;
        this.castTime = 0;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.BLOOD_STEP.get());
    }

    @Override
    public void onClientPreCast(World level, LivingEntity entity, Hand hand, @Nullable PlayerMagicData playerMagicData) {
        super.onClientPreCast(level, entity, hand, playerMagicData);
        Vector3d forward = entity.getForward().normalize();
        for (int i = 0; i < 35; i++) {
            Vector3d motion = forward.scale(level.random.nextDouble() * .25f);
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, entity.getRandomX(.4f), entity.getRandomY(), entity.getRandomZ(.4f), motion.x, motion.y, motion.z);
        }
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        Vector3d dest = null;
        var teleportData = (TeleportSpell.TeleportData) playerMagicData.getAdditionalCastData();
        if (teleportData != null) {
            var potentialTarget = teleportData.getTeleportTargetPosition();
            if (potentialTarget != null) {
                dest = potentialTarget;
                entity.teleportTo(dest.x, dest.y, dest.z);
            }
        }else{
            RayTraceResult hitResult = Utils.raycastForEntity(level, entity, getDistance(entity), true);
            if (entity.isPassenger()) {
                entity.stopRiding();
            }
            if (hitResult.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) hitResult).getEntity() instanceof LivingEntity target) {
                //dest = target.position().subtract(new Vec3(0, 0, 1.5).yRot(target.getYRot()));
                for (int i = 0; i < 8; i++) {
                    dest = target.position().subtract(new Vector3d(0, 0, 1.5).yRot(-(target.getYRot() + i * 45) * MathHelper.DEG_TO_RAD));
                    if (level.getBlockState(new BlockPos(dest).above()).isAir())
                        break;

                }
                entity.teleportTo(dest.x, dest.y + 1f, dest.z);
                entity.lookAt(EntityAnchorArgument.Type.EYES, target.getEyePosition().subtract(0, .15, 0));
            } else {
                dest = TeleportSpell.findTeleportLocation(level, entity, getDistance(entity));
                entity.teleportTo(dest.x, dest.y, dest.z);

            }
        }
        entity.resetFallDistance();
        level.playSound(null, dest.x, dest.y, dest.z, getCastFinishSound().get(), SoundCategory.NEUTRAL, 1f, 1f);

        //Invis take 1 tick to set in
        entity.setInvisible(true);
        entity.addEffect(new EffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), 100, 0, false, false, true));


        super.onCast(level, entity, playerMagicData);
    }

    private float getDistance(LivingEntity sourceEntity) {
        return getSpellPower(sourceEntity);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }

}
