package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.entity.spells.shield.ShieldEntity;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractConeProjectile extends ProjectileEntity implements NoKnockbackProjectile {
    protected static final int FAILSAFE_EXPIRE_TIME = 20 * 20;
    protected int age;
    protected float damage;
    protected boolean dealDamageActive = true;
    protected final ConePart[] subEntities;

    public AbstractConeProjectile(EntityType<? extends AbstractConeProjectile> entityType, World level, LivingEntity entity) {
        this(entityType, level);
        setOwner(entity);
    }

    public AbstractConeProjectile(EntityType<? extends AbstractConeProjectile> entityType, World level) {
        super(entityType, level);
        this.noPhysics = true;
        this.blocksBuilding = false;

        //TODO: dynamically generate cone parts based off of input for overall cone length/width
        this.subEntities = new ConePart[]{
                new ConePart(this, "part1", 1.0F, 1.0F),
                new ConePart(this, "part2", 2.5F, 1.5F),
                new ConePart(this, "part3", 3.5F, 2.0F),
                new ConePart(this, "part4", 4.5F, 3.0F)
        };
        //Ironsspellbooks.logger.debug("AbstractConeProjectile: Creating sub-entities");

        //this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1); // Forge: Fix MC-158205: Make sure part ids are successors of parent mob id
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    public abstract void spawnParticles();

    @Override
    protected abstract void onHitEntity(EntityRayTraceResult entityHitResult);

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.subEntities.length; i++) // Forge: Fix MC-158205: Set part ids to successors of parent mob id
            this.subEntities[i].setId(id + i + 1);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData() {
    }

    protected static Vector3d rayTrace(Entity owner) {
        float f = owner.xRot;
        float f1 = owner.yRot;
        float f2 = MathHelper.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * ((float) Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        return new Vector3d(f6, f5, f7);
    }

    @Override
    public void tick() {
        super.tick();

        if (++age > FAILSAFE_EXPIRE_TIME) {
            //This exists in case there is any bug with removing the cone onCastComplete
            discard();
        }

        //TODO: try this instead of the ray trace
        /*
        So. This is what vectors are for.
        The player has a vector that is their "front" called "LookVec" (Search the EntityPlayer class).
        Take that vector, multiply by 0.5 (or 0.2 or whatever), add their current position, and voila. You have the spot a half-block in front of them.
        */

        Entity owner = this.getOwner();
        if (owner != null) {
            Vector3d rayTraceVector = rayTrace(owner);
            Vector3d ownerEyePos = owner.getEyePosition(1.0f).subtract(0, .8, 0);
            this.setPos(ownerEyePos);
            this.setXRot(owner.xRot);
            this.setYRot(owner.yRot);
            this.yRotO = getYRot();
            this.xRotO = getXRot();
            //setDeltaMovement(ownerEyePos);

            double scale = 1;

            for (int i = 0; i < subEntities.length; i++) {
                ConePart subEntity = subEntities[i];

                double distance = 1 + (i * scale * subEntity.getDimensions(null).width / 2);
                Vector3d newVector = ownerEyePos.add(rayTraceVector.multiply(distance, distance, distance));
                subEntity.setPos(newVector);
                subEntity.setDeltaMovement(newVector);
                Vector3d vec3 = new Vector3d(subEntity.getX(), subEntity.getY(), subEntity.getZ());
                subEntity.xo = vec3.x;
                subEntity.yo = vec3.y;
                subEntity.zo = vec3.z;
                subEntity.xOld = vec3.x;
                subEntity.yOld = vec3.y;
                subEntity.zOld = vec3.z;
            }
        }

        /* Hit Detection */
        if (!level.isClientSide) {
            if (dealDamageActive) {
                for (Entity entity : getSubEntityCollisions()) {
                    //irons_spellbooks.LOGGER.debug("ConeOfColdHit : {}", entity.getName().getString());
                    onHitEntity(new EntityRayTraceResult(entity));
                }
                dealDamageActive = false;
            }
        } else {
            spawnParticles();
        }

    }

    public void setDealDamageActive() {
        this.dealDamageActive = true;
    }

    protected Set<Entity> getSubEntityCollisions() {
        List<Entity> collisions = new ArrayList<>();
        for (Entity conepart : subEntities) {
            collisions.addAll(level.getEntities(conepart, conepart.getBoundingBox()));
        }

        return collisions.stream().filter(target ->
                target != getOwner() && target instanceof LivingEntity && hasLineOfSight(this, target)
        ).collect(Collectors.toSet());
    }

    protected static boolean hasLineOfSight(Entity start, Entity target) {
        Vector3d vec3 = new Vector3d(start.getX(), start.getEyeY(), start.getZ());
        Vector3d vec31 = new Vector3d(target.getX(), target.getEyeY(), target.getZ());

        boolean isShieldBlockingLOS = Utils.raycastForEntity(start.level, start, vec3, vec31, false, 0, (entity) -> entity instanceof ShieldEntity).getType() == RayTraceResult.Type.ENTITY;
        return !isShieldBlockingLOS && start.level.clip(new RayTraceContext(vec3, vec31, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, start)).getType() == RayTraceResult.Type.MISS;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.damage = pCompound.getFloat("Damage");
    }
}
