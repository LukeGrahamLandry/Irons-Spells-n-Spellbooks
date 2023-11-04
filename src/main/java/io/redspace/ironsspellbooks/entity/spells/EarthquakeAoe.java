package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.VisualFallingBlockEntity;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EarthquakeAoe extends AoeEntity implements AntiMagicSusceptible {
    public static Map<UUID, EarthquakeAoe> clientEarthquakeOrigins = new HashMap<>();

    public EarthquakeAoe(EntityType<? extends ProjectileEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        this.reapplicationDelay = 25;
        this.setCircular();
    }

    public EarthquakeAoe(World level) {
        this(EntityRegistry.EARTHQUAKE_AOE.get(), level);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        DamageSource damageSource = SpellRegistry.EARTHQUAKE_SPELL.get().getDamageSource(this, getOwner());
        DamageSources.ignoreNextKnockback(target);
        if (target.hurt(damageSource, getDamage())) {
            target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 120, slownessAmplifier));
            target.setDeltaMovement(target.getDeltaMovement().add(0, .5, 0));
            target.hurtMarked = true;
        }
    }

    private CameraShakeData cameraShakeData;
    private int slownessAmplifier;

    public int getSlownessAmplifier() {
        return slownessAmplifier;
    }

    public void setSlownessAmplifier(int slownessAmplifier) {
        this.slownessAmplifier = slownessAmplifier;
    }

    @Override
    public float getParticleCount() {
        return 0f;
    }

    @Override
    public void ambientParticles() {

    }

    int waveAnim = -1;

    @Override
    public void tick() {
        super.tick();
        if (tickCount == 1) {
            createScreenShake();
        }
        if (tickCount % 20 == 1) {
            this.playSound(SoundRegistry.EARTHQUAKE_LOOP.get(), 2f, .9f + random.nextFloat() * .15f);
        }
        if (tickCount % reapplicationDelay == 1) {
            //aligns with damage tick
            waveAnim = 0;
            this.playSound(SoundRegistry.EARTHQUAKE_IMPACT.get(), 1.5f, .9f + random.nextFloat() * .2f);
        }
        if (!level.isClientSide) {
            float radius = this.getRadius();
            World level = this.level;
            int intensity = (int) (radius * radius * .09f);
            for (int i = 0; i < intensity; i++) {
                Vector3d vec3 = this.position().add(uniformlyDistributedPointInRadius(radius));
                BlockPos blockPos = new BlockPos(Utils.moveToRelativeGroundLevel(level, vec3, 4)).below();
                createTremorBlock(blockPos, .1f + random.nextFloat() * .2f);
            }
            if (waveAnim >= 0) {
                float circumference = waveAnim * 2 * 3.14f;
                int blocks = (int) circumference;
                float anglePerBlock = 360f / blocks;
                for (int i = 0; i < blocks; i++) {
                    Vector3d vec3 = new Vector3d(
                            waveAnim * MathHelper.cos(anglePerBlock * i),
                            0,
                            waveAnim * MathHelper.sin(anglePerBlock * i)
                    );
                    BlockPos blockPos = new BlockPos(Utils.moveToRelativeGroundLevel(level, position().add(vec3), 4)).below();
                    createTremorBlock(blockPos, .1f + random.nextFloat() * .2f);
                }
                if (waveAnim++ >= radius) {
                    waveAnim = -1;
                    if (tickCount + reapplicationDelay >= duration) {
                        this.remove();
                        //end ourselves smoothly with the last bang instead of timing out awkwardly
                    }
                }
            }
        }
    }

    @Override
    protected boolean canHitTargetForGroundContext(LivingEntity target) {
        return true;
    }

    @Override
    protected Vector3d getInflation() {
        return new Vector3d(0, 5, 0);
    }

    protected void createTremorBlock(BlockPos blockPos, float impulseStrength) {
        VisualFallingBlockEntity fallingblockentity = new VisualFallingBlockEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), level.getBlockState(blockPos), 20);
        fallingblockentity.setDeltaMovement(0, impulseStrength, 0);
        level.addFreshEntity(fallingblockentity);
        if (!level.getBlockState(blockPos.above()).isAir()) {
            createTremorBlock(blockPos.above(), impulseStrength);
        }
    }

    protected void createScreenShake() {
        if (!this.level.isClientSide && !this.isRemoved()) {
            this.cameraShakeData = new CameraShakeData(this.duration - this.tickCount, this.position());
            CameraShakeManager.addCameraShake(cameraShakeData);
        }
    }

    protected Vector3d uniformlyDistributedPointInRadius(float r) {
        float distance = r * (1 - this.random.nextFloat() * this.random.nextFloat());
        float theta = this.random.nextFloat() * 6.282f; // two pi :nerd:
        return new Vector3d(
                distance * MathHelper.cos(theta),
                .2f,
                distance * MathHelper.sin(theta)
        );
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        if (!level.isClientSide) {
            CameraShakeManager.removeCameraShake(this.cameraShakeData);
        }
    }

    @Override
    public EntitySize getDimensions(Pose pPose) {
        return EntitySize.scalable(this.getRadius() * 2.0F, 3F);
    }

    @Override
    public IParticleData getParticle() {
        return ParticleTypes.ENTITY_EFFECT;
    }

    @Override
    public void onAntiMagic(MagicData magicData) {
        this.remove();
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Slowness", slownessAmplifier);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.slownessAmplifier = pCompound.getInt("Slowness");
        IronsSpellbooks.LOGGER.debug("EarthquakeAoe readAdditionalSaveData");
        createScreenShake();
    }
}
