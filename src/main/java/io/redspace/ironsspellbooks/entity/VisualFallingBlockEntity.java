package io.redspace.ironsspellbooks.entity;

import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.phys.Vec3;

public class VisualFallingBlockEntity extends FallingBlockEntity {
    public VisualFallingBlockEntity(EntityType<? extends VisualFallingBlockEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    int maxAge = 200;
    private double originalX;
    private double originalY;
    private double originalZ;
    private double ticks;

    @Override
    public void setOnGround(boolean pOnGround) {
    }

    @Override
    public boolean isOnGround() {
        return tickCount > 1 && (this.position().y <= originalY || this.getDeltaMovement().lengthSqr() < .001f);
    }

    public VisualFallingBlockEntity(World pLevel, double pX, double pY, double pZ, BlockState pState) {
        this(EntityRegistry.FALLING_BLOCK.get(), pLevel);

        originalX = pX;
        originalY = pY;
        originalZ = pZ;
        ticks = 0;

        this.blocksBuilding = false;
        this.blockState = pState;
        this.setPos(pX + .5, pY, pZ + .5);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
        this.setStartPos(this.blockPosition());

        this.dropItem = false;
        this.cancelDrop = true;
    }

    public VisualFallingBlockEntity(World pLevel, double pX, double pY, double pZ, BlockState pState, int maxAge){
        this(pLevel,pX,pY,pZ,pState);
        this.maxAge = maxAge;
    }

    @Override
    public void tick() {
//        super.tick();
        if (this.blockState.isAir() || this.isOnGround() || tickCount > maxAge) {
            this.discard();
        } else {
            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.08D, 0.0D));
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.dropItem = false;
        this.cancelDrop = true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void callOnBrokenAfterFall(Block pBlock, BlockPos pPos) {
        return;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

}
