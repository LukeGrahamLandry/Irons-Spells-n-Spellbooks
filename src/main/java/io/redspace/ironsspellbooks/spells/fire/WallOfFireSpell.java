package io.redspace.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.ISpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.wall_of_fire.WallOfFireEntity;
import io.redspace.ironsspellbooks.network.ServerboundCancelCast;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class WallOfFireSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "wall_of_fire");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.distance", Utils.stringTruncation(getWallLength(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(20)
            .build();

    public WallOfFireSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 4;
        this.spellPowerPerLevel = 1;
        this.castTime = 100;
        this.baseManaCost = 10;
    }

    @Override
    public CastType getCastType() {
        return CastType.CONTINUOUS;
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
        return Optional.empty();
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (playerMagicData.isCasting() && playerMagicData.getCastingSpellId().equals(this.getSpellId()) && playerMagicData.getAdditionalCastData() == null) {
            //IronsSpellbooks.LOGGER.debug("WallOfFireSpell: creating new data");
            FireWallData fireWallData = new FireWallData(getWallLength(spellLevel, entity));
            playerMagicData.setAdditionalCastData(fireWallData);

        }
        //IronsSpellbooks.LOGGER.debug(playerMagicData.toString());
        //if (playerMagicData.getAdditionalCastData() instanceof FireWallData fireWallData)

        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    @Override
    public void onServerCastTick(World level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        //IronsSpellbooks.LOGGER.debug("WallOfFireSpell.onServerCastTick");
        if (playerMagicData.getAdditionalCastData() instanceof FireWallData) {
            FireWallData fireWallData = (FireWallData) playerMagicData.getAdditionalCastData();
            //IronsSpellbooks.LOGGER.debug("WallOfFireSpell.onServerCastTick {}", fireWallData.ticks);
            if (fireWallData.ticks++ % 4 == 0) {
                addAnchor(fireWallData, level, entity);
            }
        }

    }

    @Override
    public void onServerCastComplete(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
        //IronsSpellbooks.LOGGER.debug("WallOfFireSpell.onServerCastComplete.1");
        if (playerMagicData.getAdditionalCastData() instanceof FireWallData) {
            FireWallData fireWallData = (FireWallData) playerMagicData.getAdditionalCastData();
            //IronsSpellbooks.LOGGER.debug("WallOfFireSpell.onServerCastComplete.2");
            if (fireWallData.anchors.size() == 1) {
                //IronsSpellbooks.LOGGER.debug("WallOfFireSpell.onServerCastComplete.3");
                addAnchor(fireWallData, level, entity);
            }

            if (fireWallData.anchors.size() > 0) {
                //IronsSpellbooks.LOGGER.debug("WallOfFireSpell.onServerCastComplete.4");
                WallOfFireEntity fireWall = new WallOfFireEntity(level, entity, fireWallData.anchors, getDamage(spellLevel, entity));
                Vector3d origin = fireWallData.anchors.get(0);
                for (int i = 1; i < fireWallData.anchors.size(); i++) {
                    origin.add(fireWallData.anchors.get(i));
                }
                origin.scale(1 / (float) fireWallData.anchors.size());
                fireWall.setPos(origin);
                level.addFreshEntity(fireWall);
            }
        }
        super.onServerCastComplete(level, spellLevel, entity, playerMagicData, false);
    }

    @Override
    public DamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return ((ISpellDamageSource) super.getDamageSource(projectile, attacker)).setFireTime(4).get();
    }

    private float getWallLength(int spellLevel, LivingEntity entity) {
        return 10 + getLevel(spellLevel, entity) * 2;
    }

    private float getDamage(int spellLevel, LivingEntity sourceEntity) {
        return getSpellPower(spellLevel, sourceEntity);
    }

    public void addAnchor(FireWallData fireWallData, World level, LivingEntity entity) {
        Vector3d anchor = Utils.getTargetBlock(level, entity, RayTraceContext.FluidMode.ANY, 20).getLocation();

        anchor = setOnGround(anchor, level);
        List<Vector3d> anchorPoints = fireWallData.anchors;
        if (anchorPoints.size() == 0) {
            anchorPoints.add(anchor);

        } else {
            int i = anchorPoints.size();
            float distance = (float) anchorPoints.get(i - 1).distanceTo(anchor);
            float maxDistance = fireWallData.maxTotalDistance - fireWallData.accumulatedDistance;
            if (distance <= maxDistance) {
                //point fits, continue
                fireWallData.accumulatedDistance += distance;
                anchorPoints.add(anchor);
                //irons_spellbooks.LOGGER.debug("WallOfFire: this anchor fits (length {})", distance);

            } else {
                //too long, clip and cancel spell
                anchor = anchorPoints.get(i - 1).add(anchor.subtract(anchorPoints.get(i - 1)).normalize().scale(maxDistance));
                anchor = setOnGround(anchor, level);
                anchorPoints.add(anchor);
                if (entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
                    MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
                    boolean triggerCooldown = playerMagicData.getCastSource() != CastSource.SCROLL;
                    ServerboundCancelCast.cancelCast(serverPlayer, triggerCooldown);
                }
            }
            //irons_spellbooks.LOGGER.debug("WallOfFire.maxDistance: {}", this.maxTotalDistance);
            //irons_spellbooks.LOGGER.debug("WallOfFire.currentDistance: {}", this.accumulatedDistance);
        }
        MagicManager.spawnParticles(level, ParticleTypes.FLAME, anchor.x, anchor.y + 1.5, anchor.z, 5, .05, .25, .05, 0, true);
        //IronsSpellbooks.LOGGER.debug("WallOfFireSpell: adding anchor");
    }

    private Vector3d setOnGround(Vector3d in, World level) {
        if (level.getBlockState(new BlockPos(in.x, in.y + .5f, in.z)).isAir()) {
            for (int i = 0; i < 15; i++) {
                if (!level.getBlockState(new BlockPos(in.x, in.y - i, in.z)).isAir()) {
                    return new Vector3d(in.x, in.y - i + 1, in.z);
                }
            }
            return new Vector3d(in.x, in.y - 15, in.z);
        } else {
            double y = level.getHeight(Heightmap.Type.MOTION_BLOCKING, (int) in.x, (int) in.z);
            return new Vector3d(in.x, y, in.z);
        }
    }

    public class FireWallData implements ICastData {
        private Entity castingEntity;
        public List<Vector3d> anchors = new ArrayList<>();
        public float maxTotalDistance;
        public float accumulatedDistance;
        public int ticks;

        FireWallData(float maxTotalDistance) {
            this.maxTotalDistance = maxTotalDistance;
        }

        @Override
        public void reset() {

        }
    }
}
