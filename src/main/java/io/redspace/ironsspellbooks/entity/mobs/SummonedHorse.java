package io.redspace.ironsspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;

public class SummonedHorse extends AbstractHorseEntity implements MagicSummon {
    public SummonedHorse(EntityType<? extends AbstractHorseEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        //randomizeAttributes(Utils.random);

    }

    public SummonedHorse(World pLevel) {
        this(EntityRegistry.SPECTRAL_STEED.get(), pLevel);
        //randomizeAttributes(Utils.random);

    }

    public SummonedHorse(World pLevel, LivingEntity owner) {
        this(pLevel);
        setOwnerUUID(owner.getUUID());
        setSummoner(owner);
    }

    protected LivingEntity cachedSummoner;

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new GenericFollowOwnerGoal(this, this::getSummoner, 0.8f, 12, 4, false, 32));
        this.goalSelector.addGoal(3, new PanicGoal(this, 0.9f));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));

    }

    @Override
    public void openCustomInventoryScreen(PlayerEntity pPlayer) {
        return;
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15)
                .add(Attributes.JUMP_STRENGTH, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    @Override
    public void tick() {
        spawnParticles();
        super.tick();
    }

    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }

    public void onUnSummon() {
        if (!level.isClientSide) {
            MagicManager.spawnParticles(level, ParticleTypes.POOF, getX(), getY(), getZ(), 25, .4, .8, .4, .03, false);
            discard();
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (shouldIgnoreDamage(pSource))
            return false;
        return super.hurt(pSource, pAmount);
    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.HORSE_HURT;
    }

    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.HORSE_ANGRY;
    }

    public void spawnParticles() {

        if (level.isClientSide) {
            if (Utils.random.nextFloat() < .25f) {
                float radius = .75f;
                Vector3d vec = new Vector3d(
                        random.nextFloat() * 2 * radius - radius,
                        random.nextFloat() * 2 * radius - radius,
                        random.nextFloat() * 2 * radius - radius
                );
                level.addParticle(ParticleTypes.ENCHANT, this.getX() + vec.x, this.getY() + vec.y + 1, this.getZ() + vec.z, vec.x * .01f, .08 + vec.y * .01f, vec.z * .01f);
            }
        }
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity pPlayer, Hand pHand) {
        if (this.isVehicle()) {
            return super.mobInteract(pPlayer, pHand);
        }
        if (pPlayer == getSummoner()) {
            this.doPlayerRide(pPlayer);
        } else {
            this.makeMad();
        }
        return ActionResultType.sidedSuccess(this.level.isClientSide);
    }

    @Override
    public LivingEntity getSummoner() {
        return OwnerHelper.getAndCacheOwner(level, cachedSummoner, getOwnerUUID());
    }

    public void setSummoner(@Nullable LivingEntity owner) {
        if (owner != null) {
            setOwnerUUID(owner.getUUID());
            this.cachedSummoner = owner;
        }
    }

    @Override
    public void die(DamageSource pDamageSource) {
        this.onDeathHelper();
        super.die(pDamageSource);
    }

    @Override
    public void onRemovedFromWorld() {
        this.onRemovedHelper(this, MobEffectRegistry.SUMMON_HORSE_TIMER.get());
        super.onRemovedFromWorld();
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setOwnerUUID(OwnerHelper.deserializeOwner(compoundTag));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        OwnerHelper.serializeOwner(compoundTag, getOwnerUUID());
    }

    @Override
    public boolean canBeLeashed(PlayerEntity pPlayer) {
        return false;
    }

    @Override
    protected boolean canParent() {
        return false;
    }

    @Override
    public boolean isSaddled() {
        return true;
    }

    @Override
    public boolean isTamed() {
        return true;
    }


}
