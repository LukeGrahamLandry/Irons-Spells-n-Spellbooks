package io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid;

import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.UUID;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.HandSide;

public class FrozenHumanoid extends LivingEntity {
    protected static final DataParameter<Boolean> DATA_IS_BABY = EntityDataManager.defineId(FrozenHumanoid.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> DATA_IS_SITTING = EntityDataManager.defineId(FrozenHumanoid.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> DATA_FROZEN_SPEED = EntityDataManager.defineId(FrozenHumanoid.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> DATA_LIMB_SWING = EntityDataManager.defineId(FrozenHumanoid.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> DATA_LIMB_SWING_AMOUNT = EntityDataManager.defineId(FrozenHumanoid.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> DATA_ATTACK_TIME = EntityDataManager.defineId(FrozenHumanoid.class, DataSerializers.FLOAT);

    private float shatterProjectileDamage;
    private int deathTimer = -1;
    private UUID summonerUUID;
    private LivingEntity cachedSummoner;

    public FrozenHumanoid(EntityType<? extends LivingEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_BABY, false);
        this.entityData.define(DATA_IS_SITTING, false);
        this.entityData.define(DATA_FROZEN_SPEED, 0f);
        this.entityData.define(DATA_LIMB_SWING, 0f);
        this.entityData.define(DATA_LIMB_SWING_AMOUNT, 0f);
        this.entityData.define(DATA_ATTACK_TIME, 0f);
    }
//
//    @Override
//    public float getAttackAnim(float pPartialTick) {
//        return stationaryAttackAnim;
//    }

    private boolean isAutoSpinAttack;
    private HandSide mainArm = HandSide.RIGHT;

    public FrozenHumanoid(World level, LivingEntity entityToCopy) {
        this(EntityRegistry.FROZEN_HUMANOID.get(), level);
        //this.swingingArm = entityToCopy.swingingArm;
        //this.swinging = entityToCopy.swinging;
        //this.setRot(entityToCopy.getXRot(),entityToCopy.getYRot());
        this.moveTo(entityToCopy.getX(), entityToCopy.getY(), entityToCopy.getZ(), entityToCopy.getYRot(), entityToCopy.getXRot());
        //irons_spellbooks.LOGGER.debug("yRot: {}", entityToCopy.getYRot());
        //irons_spellbooks.LOGGER.debug("yBodyRot: {}", entityToCopy.yBodyRot);
//        var y = entityToCopy.getYRot();
//        this.setYRot(y);
//        this.setYBodyRot(y);
//        this.setOldPosAndRot();
        if (entityToCopy.isBaby())
            this.entityData.set(DATA_IS_BABY, true);
        if (entityToCopy.isPassenger() && (entityToCopy.getVehicle() != null && entityToCopy.getVehicle().shouldRiderSit()))
            this.entityData.set(DATA_IS_SITTING, true);

        this.setYBodyRot(entityToCopy.yBodyRot);
        this.yBodyRotO = this.yBodyRot;
        this.setYHeadRot(entityToCopy.getYHeadRot());
        this.yHeadRotO = this.yHeadRot;


        //this.animationPosition = entityToCopy.animationPosition;
        //this.animationSpeed = 0;
        float limbSwing = entityToCopy.animationPosition;
        float limbSwingAmount = entityToCopy.animationSpeed;

 //Ironsspellbooks.logger.debug("Entity limbSwing: {}", entityToCopy.animationPosition);
 //Ironsspellbooks.logger.debug("Entity limbSwingAmount: {}", entityToCopy.animationSpeed);
        //irons_spellbooks.LOGGER.debug("My limbSwing: {}", limbSwing);
//
//        this.entityData.set(DATA_FROZEN_SPEED, speed);
//        irons_spellbooks.LOGGER.debug("{}", speed);

        this.entityData.set(DATA_LIMB_SWING, limbSwing);
        this.entityData.set(DATA_LIMB_SWING_AMOUNT, limbSwingAmount);


        //this.setYBodyRot(entityToCopy.yBodyRot-entityToCopy.getYRot());
        //this.attackAnim = entityToCopy.attackAnim;
        this.entityData.set(DATA_ATTACK_TIME, entityToCopy.attackAnim);
        this.setPose(entityToCopy.getPose());
        this.isAutoSpinAttack = entityToCopy.isAutoSpinAttack();
        this.mainArm = entityToCopy.getMainArm();

        if (entityToCopy instanceof PlayerEntity player) {
            this.setCustomName(player.getDisplayName());
            this.setCustomNameVisible(true);
        } else {
            this.setCustomNameVisible(false);
        }

        setSummoner(entityToCopy);
    }

    public void setSummoner(@javax.annotation.Nullable LivingEntity owner) {
        if (owner != null) {
            this.summonerUUID = owner.getUUID();
            this.cachedSummoner = owner;
        }
    }

    public LivingEntity getSummoner() {
        if (this.cachedSummoner != null && this.cachedSummoner.isAlive()) {
            return this.cachedSummoner;
        } else if (this.summonerUUID != null && this.level instanceof ServerWorld) {
            if (((ServerWorld) this.level).getEntity(this.summonerUUID) instanceof LivingEntity livingEntity)
                this.cachedSummoner = livingEntity;
            return this.cachedSummoner;
        } else {
            return null;
        }
    }

    public boolean isSitting() {
        return this.entityData.get(DATA_IS_SITTING);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(DATA_IS_BABY);
    }

    public float getLimbSwing() {
        return this.entityData.get(DATA_LIMB_SWING);
    }

//    public float getFrozenSpeed() {
//        return this.entityData.get(DATA_FROZEN_SPEED);
//    }

    public float getLimbSwingAmount() {
        return this.entityData.get(DATA_LIMB_SWING_AMOUNT);
    }

    @Override
    public void tick() {
        super.tick();
        if (deathTimer > 0) {
            deathTimer--;

        }
        if (deathTimer == 0)
            this.hurt(DamageSource.OUT_OF_WORLD, 100);
    }

    public void setDeathTimer(int timeInTicks) {
        this.deathTimer = timeInTicks;
    }

    public float getAttacktime() {
        return this.entityData.get(DATA_ATTACK_TIME);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.GLASS_BREAK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GLASS_BREAK;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (level.isClientSide || this.isInvulnerableTo(pSource))
            return false;

        spawnIcicleShards(this.getEyePosition(), this.shatterProjectileDamage);
        this.playHurtSound(pSource);
        this.discard();
        return true;
    }

    private void spawnIcicleShards(Vector3d origin, float damage) {
        int count = 8;
        int offset = 360 / count;
        for (int i = 0; i < count; i++) {

            Vector3d motion = new Vector3d(0, 0, 0.55);
            motion = motion.xRot(30 * MathHelper.DEG_TO_RAD);
            motion = motion.yRot(offset * i * MathHelper.DEG_TO_RAD);


            IcicleProjectile shard = new IcicleProjectile(level, getSummoner());
            shard.setDamage(damage);
            shard.setDeltaMovement(motion);

            Vector3d spawn = origin.add(motion.multiply(1, 0, 1).normalize().scale(.5f));
            var angle = Utils.rotationFromDirection(motion);

            shard.moveTo(spawn.x, spawn.y - shard.getBoundingBox().getYsize() / 2, spawn.z, angle.y, angle.x);
            level.addFreshEntity(shard);
        }
    }

    public void setShatterDamage(float damage) {
        this.shatterProjectileDamage = damage;
    }

    @Override
    public boolean isAutoSpinAttack() {
        return this.isAutoSpinAttack;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.singleton(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType pSlot, ItemStack pStack) {

    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        //irons_spellbooks.LOGGER.debug("Reading Summoned Vex save data");

        if (compoundTag.hasUUID("Summoner")) {
            this.summonerUUID = compoundTag.getUUID("Summoner");
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        //irons_spellbooks.LOGGER.debug("Writing Summoned Vex save data");

        if (this.summonerUUID != null) {
            compoundTag.putUUID("Summoner", this.summonerUUID);
        }
    }

    @Override
    public HandSide getMainArm() {
        return mainArm;
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.FOLLOW_RANGE, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0);
    }
}
