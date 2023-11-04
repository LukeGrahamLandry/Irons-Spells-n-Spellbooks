package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ParticleTypes;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class BloodStepSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "blood_step");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(5)
            .build();

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList(new TranslationTextComponent("ui.irons_spellbooks.distance", Utils.stringTruncation(getDistance(spellLevel, caster), 1)));
    }

    public BloodStepSpell() {
        this.baseSpellPower = 12;
        this.spellPowerPerLevel = 4;
        this.baseManaCost = 30;
        this.manaCostPerLevel = 10;
        this.castTime = 0;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
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
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.BLOOD_STEP.get());
    }

    @Override
    public void onClientPreCast(World level, int spellLevel, LivingEntity entity, Hand hand, @Nullable MagicData playerMagicData) {
        super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
        Vector3d forward = entity.getForward().normalize();
        for (int i = 0; i < 35; i++) {
            Vector3d motion = forward.scale(Utils.random.nextDouble() * .25f);
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, entity.getRandomX(.4f), entity.getRandomY(), entity.getRandomZ(.4f), motion.x, motion.y, motion.z);
        }
    }

    @Override
    public void onCast(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Vector3d dest = null;
        TeleportSpell.TeleportData teleportData = (TeleportSpell.TeleportData) playerMagicData.getAdditionalCastData();
        if (teleportData != null) {
            Vector3d potentialTarget = teleportData.getTeleportTargetPosition();
            if (potentialTarget != null) {
                dest = potentialTarget;
                entity.teleportTo(dest.x, dest.y, dest.z);
            }
        } else {
            RayTraceResult hitResult = Utils.raycastForEntity(level, entity, getDistance(spellLevel, entity), true);
            if (entity.isPassenger()) {
                entity.stopRiding();
            }
            if (hitResult.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) hitResult).getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) ((EntityRayTraceResult) hitResult).getEntity();
                //dest = target.position().subtract(new Vec3(0, 0, 1.5).yRot(target.yRot));
                for (int i = 0; i < 8; i++) {
                    dest = target.position().subtract(new Vector3d(0, 0, 1.5).yRot(-(target.yRot + i * 45) * Utils.DEG_TO_RAD));
                    if (level.getBlockState(new BlockPos(dest).above()).isAir())
                        break;

                }
                entity.teleportTo(dest.x, dest.y + 1f, dest.z);
                entity.lookAt(EntityAnchorArgument.Type.EYES, target.getEyePosition(0).subtract(0, .15, 0));
            } else {
                dest = TeleportSpell.findTeleportLocation(level, entity, getDistance(spellLevel, entity));
                entity.teleportTo(dest.x, dest.y, dest.z);

            }
        }
        entity.fallDistance = 0;
        level.playSound(null, dest.x, dest.y, dest.z, getCastFinishSound().get(), SoundCategory.NEUTRAL, 1f, 1f);

        //Invis take 1 tick to set in
        entity.setInvisible(true);
        entity.addEffect(new EffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), 100, 0, false, false, true));


        super.onCast(level, spellLevel, entity, playerMagicData);
    }

    private float getDistance(int spellLevel, LivingEntity sourceEntity) {
        return getSpellPower(spellLevel, sourceEntity);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }

}
