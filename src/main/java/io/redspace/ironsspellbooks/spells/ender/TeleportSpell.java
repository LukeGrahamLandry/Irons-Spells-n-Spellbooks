package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.network.spell.ClientboundTeleportParticles;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.Log;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class TeleportSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "teleport");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(3)
            .build();

    public TeleportSpell() {
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 10;
        this.baseManaCost = 20;
        this.manaCostPerLevel = 2;
        this.castTime = 0;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
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
        return Optional.of(SoundEvents.ENDERMAN_TELEPORT);
    }

    @Override
    public void onCast(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("TeleportSpell.onCast isClient:{}, entity:{}, pmd:{}", level.isClientSide, entity, playerMagicData);
        }
        var teleportData = (TeleportData) playerMagicData.getAdditionalCastData();

        Vector3d dest = null;
        if (teleportData != null) {
            var potentialTarget = teleportData.getTeleportTargetPosition();
            if (potentialTarget != null) {
                dest = potentialTarget;
            }
        }

        if (dest == null) {
            dest = findTeleportLocation(level, entity, getDistance(spellLevel, entity));
        }

        Messages.sendToPlayersTrackingEntity(new ClientboundTeleportParticles(entity.position(), dest), entity, true);
        if (entity.isPassenger()) {
            entity.stopRiding();
        }
        entity.teleportTo(dest.x, dest.y, dest.z);
        entity.resetFallDistance();

        playerMagicData.resetAdditionalCastData();

//        level.playSound(null, dest.x, dest.y, dest.z, getCastFinishSound().get(), SoundSource.NEUTRAL, 1f, 1f);
        entity.playSound(getCastFinishSound().get(), 2.0f, 1.0f);

        super.onCast(level, spellLevel, entity, playerMagicData);
    }

    public static Vector3d findTeleportLocation(World level, LivingEntity entity, float maxDistance) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("TeleportSpell.findTeleportLocation isClient:{}, entity:{}", level.isClientSide, entity);
        }

        var blockHitResult = Utils.getTargetBlock(level, entity, RayTraceContext.FluidMode.NONE, maxDistance);
        var pos = blockHitResult.getBlockPos();

        Vector3d bbOffset = entity.getForward().normalize().multiply(entity.getBbWidth() / 3, 0, entity.getBbHeight() / 3);
        Vector3d bbImpact = blockHitResult.getLocation().subtract(bbOffset);
        //        Vec3 lower = level.clip(new ClipContext(start, start.add(0, maxSteps * -2, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getLocation();
        int ledgeY = (int) level.clip(new RayTraceContext(Vector3d.atBottomCenterOf(pos).add(0, 3, 0), Vector3d.atBottomCenterOf(pos), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null)).getLocation().y;
        Vector3d correctedPos = new Vector3d(pos.getX(), ledgeY, pos.getZ());
        boolean isAir = level.getBlockState(new BlockPos(correctedPos)).isAir();
        boolean los = level.clip(new RayTraceContext(bbImpact, bbImpact.add(0, ledgeY - pos.getY(), 0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS;

        if (isAir && los && Math.abs(ledgeY - pos.getY()) <= 3) {
            return correctedPos.add(0.5, 0.076, 0.5);
        } else {
            return level.clip(new RayTraceContext(bbImpact, bbImpact.add(0, -entity.getEyeHeight(), 0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getLocation().add(0, 0.076, 0);
        }

    }

    public static void particleCloud(World level, Vector3d pos) {
        if (level.isClientSide) {
            double width = 0.5;
            float height = 1;
            for (int i = 0; i < 55; i++) {
                double x = pos.x + Utils.random.nextDouble() * width * 2 - width;
                double y = pos.y + height + Utils.random.nextDouble() * height * 1.2 * 2 - height * 1.2;
                double z = pos.z + Utils.random.nextDouble() * width * 2 - width;
                double dx = Utils.random.nextDouble() * .1 * (Utils.random.nextBoolean() ? 1 : -1);
                double dy = Utils.random.nextDouble() * .1 * (Utils.random.nextBoolean() ? 1 : -1);
                double dz = Utils.random.nextDouble() * .1 * (Utils.random.nextBoolean() ? 1 : -1);
                level.addParticle(ParticleTypes.PORTAL, true, x, y, z, dx, dy, dz);
            }
        }
    }

    private float getDistance(int spellLevel, LivingEntity sourceEntity) {
        return getSpellPower(spellLevel, sourceEntity);
    }

    public static class TeleportData implements ICastData {
        private Vector3d teleportTargetPosition;

        public TeleportData(Vector3d teleportTargetPosition) {
            this.teleportTargetPosition = teleportTargetPosition;
        }

        public void setTeleportTargetPosition(Vector3d targetPosition) {
            this.teleportTargetPosition = targetPosition;
        }

        public Vector3d getTeleportTargetPosition() {
            return this.teleportTargetPosition;
        }

        @Override
        public void reset() {
            //Nothing needed here for teleport
        }
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(ITextComponent.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getDistance(spellLevel, caster), 1)));
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }

}
