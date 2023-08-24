package io.redspace.ironsspellbooks.entity.spells.target_area;

import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.entity.*;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;

public class TargetedAreaEntity extends Entity {
    private static final DataParameter<Float> DATA_RADIUS = EntityDataManager.defineId(TargetedAreaEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.defineId(TargetedAreaEntity.class, DataSerializers.INT);


    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    private int duration;

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }

    }

    @Nullable
    public Entity getOwner() {
        return OwnerHelper.getAndCacheOwner(level, cachedOwner, ownerUUID);
    }

    public TargetedAreaEntity(EntityType<TargetedAreaEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        setRadius(3f);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static TargetedAreaEntity createTargetAreaEntity(World level, Vector3d center, float radius, int color) {
        TargetedAreaEntity targetedAreaEntity = new TargetedAreaEntity(level, radius, color);
        targetedAreaEntity.setPos(center);
        level.addFreshEntity(targetedAreaEntity);
        return targetedAreaEntity;
    }

    @Override
    public void tick() {
        var owner = getOwner();
        xOld = getX();
        yOld = getY();
        zOld = getZ();
        if (owner != null) {
            setPos(owner.position());
            this.setDeltaMovement(owner.getDeltaMovement());
        }
        if (!level.isClientSide
                && (duration > 0 && tickCount > duration
                || duration == 0 && tickCount > 20 * 20
                || (owner != null && owner.isRemoved())))
            discard();
    }

    public TargetedAreaEntity(World level, float radius, int color) {
        this(EntityRegistry.TARGET_AREA_ENTITY.get(), level);
        this.setRadius(radius);
        this.setColor(color);
    }

    @Override
    public EntitySize getDimensions(Pose pPose) {
        return EntitySize.scalable(this.getRadius() * 2.0F, 0.8F);
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 2F);
        this.getEntityData().define(DATA_COLOR, 0xFFFFFF);
    }

    public void setRadius(float pRadius) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, MathHelper.clamp(pRadius, 0.0F, 32.0F));
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public void setColor(int color) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_COLOR, color);
        }
    }

    public Vector3f getColor() {
        int color = this.getEntityData().get(DATA_COLOR);
        //Clever color mapping, taken from potionutils get color
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        return new Vector3f(red / 255.0f, green / 255.0f, blue / 255.0f);

    }

    public int getColorRaw() {
        return this.getEntityData().get(DATA_COLOR);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> pKey) {
        if (DATA_RADIUS.equals(pKey)) {
            this.refreshDimensions();
            if (getRadius() < .1f)
                this.discard();
        }
        super.onSyncedDataUpdated(pKey);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    protected void addAdditionalSaveData(CompoundNBT tag) {
        tag.putFloat("Radius", this.getRadius());
        tag.putInt("Color", this.getColorRaw());
        tag.putInt("Age", this.tickCount);
        if (duration > 0)
            tag.putInt("Duration", duration);
        if (ownerUUID != null)
            tag.putUUID("Owner", ownerUUID);
    }

    protected void readAdditionalSaveData(CompoundNBT tag) {
        this.setRadius(tag.getFloat("Radius"));
        this.setColor(tag.getInt("Color"));
        this.tickCount = (tag.getInt("Age"));
        if (tag.contains("Duration"))
            this.duration = tag.getInt("Duration");
        if (tag.contains("Owner"))
            this.ownerUUID = tag.getUUID("Owner");

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return new SSpawnObjectPacket(this);
    }
}
