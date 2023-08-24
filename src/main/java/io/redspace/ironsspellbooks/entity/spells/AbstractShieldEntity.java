package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractShieldEntity extends Entity implements AntiMagicSusceptible {
    private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.defineId(AbstractShieldEntity.class, DataSerializers.FLOAT);

    public boolean hurtThisTick;

    public AbstractShieldEntity(EntityType<?> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
//        width = 3;
//        height = 3;
//        subEntities = new ShieldPart[width * height];
//        subPositions = new Vec3[width * height];
//        this.setHealth(100);
//        //this.setXRot(45);
//        //this.setYRot(45);
//        LIFETIME = 20 * 20;
//        createShield();

    }

    public AbstractShieldEntity(World level, float health) {
        this(EntityRegistry.SHIELD_ENTITY.get(), level);
        this.setHealth(health);
    }

    protected abstract void createShield();

//    public void setRotation(float x, float y) {
//        this.setXRot(x);
//        this.xRotO = x;
//        this.setYRot(y);
//        this.yRotO = y;
//    }

    public abstract void takeDamage(DamageSource source, float amount, @Nullable Vector3d location);

    @Override
    public void tick() {
        hurtThisTick = false;
        for (PartEntity<?> subEntity : getParts()) {
            Vector3d pos = subEntity.position();
            subEntity.setPos(pos);
            subEntity.xo = pos.x;
            subEntity.yo = pos.y;
            subEntity.zo = pos.z;
            subEntity.xOld = pos.x;
            subEntity.yOld = pos.y;
            subEntity.zOld = pos.z;
        }
    }

    protected void destroy() {
        kill();
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public abstract PartEntity<?>[] getParts();

    @Override
    public void setId(int id) {
        super.setId(id);
        var subEntities = getParts();
        for (int i = 0; i < subEntities.length; i++) // Forge: Fix MC-158205: Set part ids to successors of parent mob id
            subEntities[i].setId(id + i + 1);
    }

    public float getHealth() {
        return this.entityData.get(DATA_HEALTH_ID);
    }

    public void setHealth(float pHealth) {
        this.entityData.set(DATA_HEALTH_ID, pHealth);
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_HEALTH_ID, 1.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        if (pCompound.contains("Health", 99)) {
            this.setHealth(pCompound.getFloat("Health"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        pCompound.putFloat("Health", this.getHealth());

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        //TODO: fill this out with real info
        return new SSpawnObjectPacket(this);
    }

    public List<VoxelShape> getVoxels() {
        List<VoxelShape> voxels = new ArrayList<>();
        for (PartEntity<?> shieldPart : getParts())
            voxels.add(VoxelShapes.create(shieldPart.getBoundingBox()));
        return voxels;
    }

    @Override
    public void onAntiMagic(PlayerMagicData playerMagicData) {
        this.discard();
    }
}
