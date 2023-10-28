package io.redspace.ironsspellbooks.entity.spells;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraftforge.entity.PartEntity;

public class ShieldPart extends PartEntity<AbstractShieldEntity> {

    public final AbstractShieldEntity parentEntity;
    public final String name;
    private final EntitySize size;
    private final boolean hasCollision;

    public ShieldPart(AbstractShieldEntity shieldEntity, String name, float scaleX, float scaleY, boolean hasCollision) {
        super(shieldEntity);
        this.size = EntitySize.scalable(scaleX, scaleY);
        this.refreshDimensions();
        this.parentEntity = shieldEntity;
        this.name = name;
        this.hasCollision = hasCollision;
    }

    @Override
    public boolean canBeCollidedWith() {
        return hasCollision;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!parentEntity.hurtThisTick) {
            parentEntity.takeDamage(pSource, pAmount, this.getBoundingBox().getCenter());
            parentEntity.hurtThisTick = true;
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundTag) {

    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.parentEntity == entity;
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}