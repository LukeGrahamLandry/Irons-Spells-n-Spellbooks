package io.redspace.ironsspellbooks.entity.spells;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraftforge.entity.PartEntity;

public class ConePart extends PartEntity<AbstractConeProjectile> {

    public final AbstractConeProjectile parentEntity;
    public final String name;
    private final EntitySize size;

    public ConePart(AbstractConeProjectile coneProjectile, String name, float scaleX, float scaleY) {
        super(coneProjectile);
        this.size = EntitySize.scalable(scaleX, scaleY);
        this.refreshDimensions();
        this.parentEntity = coneProjectile;
        this.name = name;
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