package io.redspace.ironsspellbooks.entity.spells.ray_of_frost;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.IPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class RayOfFrostVisualEntity extends Entity implements IEntityAdditionalSpawnData {
    public static final int lifetime = 15;
    public RayOfFrostVisualEntity(EntityType<?> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    public float distance;

    public RayOfFrostVisualEntity(World level, Vector3d start, Vector3d end, LivingEntity owner) {
        super(EntityRegistry.RAY_OF_FROST_VISUAL_ENTITY.get(), level);
        this.setPos(start.subtract(0, .75f, 0));
        this.distance = (float) start.distanceTo(end);
        this.setRot(owner.getYRot(), owner.getXRot());

    }


    @Override
    public void tick() {
        if (++tickCount > lifetime) {
            this.discard();
        }
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt((int) (distance * 10));
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.distance = additionalData.readInt() / 10f;
    }
}
