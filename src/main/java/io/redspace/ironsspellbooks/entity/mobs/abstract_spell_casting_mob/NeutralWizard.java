package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RangedInteger;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class NeutralWizard extends AbstractSpellCastingMob implements IAngerable {
    protected NeutralWizard(EntityType<? extends CreatureEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
    }

    public void setRemainingPersistentAngerTime(int pTime) {
        this.remainingPersistentAngerTime = pTime;
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setPersistentAngerTarget(@javax.annotation.Nullable UUID pTarget) {
        this.persistentAngerTarget = pTarget;
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT pCompound) {
        this.addPersistentAngerSaveData(pCompound);
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT pCompound) {
        this.readPersistentAngerSaveData((ServerWorld) this.level, pCompound);
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerWorld)this.level, true);
        }
    }
}
