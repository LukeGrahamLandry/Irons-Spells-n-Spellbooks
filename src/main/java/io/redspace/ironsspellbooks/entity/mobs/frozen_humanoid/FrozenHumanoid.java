package io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.icicle.IcicleProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class FrozenHumanoid extends LivingEntity {
    protected static final EntityDataAccessor<Boolean> DATA_IS_BABY = SynchedEntityData.defineId(FrozenHumanoid.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_IS_SITTING = SynchedEntityData.defineId(FrozenHumanoid.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> DATA_FROZEN_SPEED = SynchedEntityData.defineId(FrozenHumanoid.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_LIMB_SWING = SynchedEntityData.defineId(FrozenHumanoid.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_LIMB_SWING_AMOUNT = SynchedEntityData.defineId(FrozenHumanoid.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_ATTACK_TIME = SynchedEntityData.defineId(FrozenHumanoid.class, EntityDataSerializers.FLOAT);

    private float shatterProjectileDamage;
    private int deathTimer = -1;

    public FrozenHumanoid(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
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
    private HumanoidArm mainArm = HumanoidArm.RIGHT;

    public FrozenHumanoid(Level level, LivingEntity entityToCopy) {
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

        IronsSpellbooks.LOGGER.debug("Entity limbSwing: {}", entityToCopy.animationPosition);
        IronsSpellbooks.LOGGER.debug("Entity limbSwingAmount: {}", entityToCopy.animationSpeed);
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

        if (entityToCopy instanceof Player player) {
            this.setCustomName(player.getDisplayName());
            this.setCustomNameVisible(true);
        } else {
            this.setCustomNameVisible(false);
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

    private void spawnIcicleShards(Vec3 origin, float damage) {
        int count = 8;
        int offset = 360 / count;
        for (int i = 0; i < count; i++) {

            Vec3 motion = new Vec3(0, 0, 0.45);
            motion = motion.xRot(30 * Mth.DEG_TO_RAD);
            motion = motion.yRot(offset * i * Mth.DEG_TO_RAD);


            IcicleProjectile shard = new IcicleProjectile(level, this);
            shard.setDamage(damage);
            shard.setDeltaMovement(motion);

            Vec3 spawn = origin.add(motion.multiply(1, 0, 1).normalize().scale(.3f));
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
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return mainArm;
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.FOLLOW_RANGE, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0);
    }
}