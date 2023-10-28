package io.redspace.ironsspellbooks.entity.spells.wall_of_fire;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.IPacket;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WallOfFireEntity extends AbstractShieldEntity implements IEntityAdditionalSpawnData {
    protected ShieldPart[] subEntities;
    protected List<Vector3d> partPositions = new ArrayList<>();
    protected List<Vector3d> anchorPoints = new ArrayList<>();

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    protected float damage;

    protected int lifetime = 8 * 20;

    public WallOfFireEntity(EntityType<?> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        //Ironsspellbooks.logger.debug("WallOfFire.attempting to create sub entities");
        subEntities = new ShieldPart[0];

    }

    @Override
    public void takeDamage(DamageSource source, float amount, @Nullable Vector3d location) {

    }

    public WallOfFireEntity(World level, Entity owner, List<Vector3d> anchors, float damage) {
        this(EntityRegistry.WALL_OF_FIRE_ENTITY.get(), level);
        this.anchorPoints = anchors;
        createShield();
        this.damage = damage;
        setOwner(owner);
    }

    @Override
    public void tick() {
        if (anchorPoints.size() <= 1 || subEntities.length <= 1) {
            discard();
            return;
        }

        for (int i = 0, subEntitiesLength = subEntities.length; i < subEntitiesLength; i++) {
            PartEntity<?> subEntity = subEntities[i];
            Vector3d pos = partPositions.get(i);
            subEntity.setPos(pos);
            subEntity.xo = pos.x;
            subEntity.yo = pos.y;
            subEntity.zo = pos.z;
            subEntity.xOld = pos.x;
            subEntity.yOld = pos.y;
            subEntity.zOld = pos.z;
            if (level.isClientSide) {
                for (int j = 0; j < 3; j++) {
                    double offset = .5;
                    double ox = (Math.random() * 2 * offset - offset);
                    double oy = Math.random() * 2 * offset - offset;
                    double oz = Math.random() * 2 * offset - offset;
                    level.addParticle(ParticleTypes.FLAME, pos.x + ox, pos.y + oy - .25, pos.z + oz, 0, Math.random() * .3, 0);
                }

            } else {
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, subEntity.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
                    if (livingentity != getOwner()) {
                        DamageSources.applyDamage(livingentity, damage, SpellRegistry.WALL_OF_FIRE_SPELL.get().getDamageSource(this, getOwner()));
                        livingentity.invulnerableTime = 10;
                    }
                }
            }
        }
        if (!level.isClientSide && --lifetime < 0)
            discard();
    }

    @Override
    public void createShield() {
        //Ironsspellbooks.logger.debug("Attempting to create shield, achor points length: {}", anchorPoints.size());
        float height = 3;
        float step = .8f;
        List<ShieldPart> entitiesList = new ArrayList<>();
        //Ironsspellbooks.logger.debug("WallOfFire:Creating shield");
        for (int i = 0; i < anchorPoints.size() - 1; i++) {
            Vector3d start = anchorPoints.get(i);
            Vector3d end = anchorPoints.get(i + 1);
            Vector3d dirVec = end.subtract(start).normalize().scale(step);
            int steps = (int) ((start.distanceTo(end) + .5) / step);
            for (int currentStep = 0; currentStep < steps; currentStep++) {
                //MagicManager.spawnParticles(level, ParticleTypes.DRAGON_BREATH, start.x + dirVec.x * x, start.y + dirVec.y * x, start.z + dirVec.z * x, 1, 0, 0, 0, 0, true);
                ShieldPart part = new ShieldPart(this, "part" + i * steps + currentStep, .55f, height, false);
                double x = start.x + dirVec.x * currentStep;
                double y = start.y + dirVec.y * currentStep;
                double z = start.z + dirVec.z * currentStep;
                double groundY = level.getHeight(Heightmap.Type.MOTION_BLOCKING, (int) x, (int) z);
                //y += Math.min(5, Math.abs(y - groundY)) * y < groundY ? 1 : -1;

                if (Math.abs(y - groundY) < 2)
                    y += (groundY - y) * .75;
                //Vec3 pos = new Vec3(, start.y + dirVec.y * x, start.z + dirVec.z * x);

                Vector3d pos = new Vector3d(x, y, z);

                partPositions.add(pos);
                //Ironsspellbooks.logger.debug("WallOfFire:Creating shield: new sub entity {}", pos);
                entitiesList.add(part);
            }

        }
        //subEntities = new ShieldPart[entitiesList.size()];
        subEntities = entitiesList.toArray(subEntities);
        //Ironsspellbooks.logger.debug("WallOfFire.createShield (array length: {}, real length: {}),", subEntities.length, entitiesList.size());

    }


    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof ServerWorld) {
            this.cachedOwner = ((ServerWorld) this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public PartEntity<?>[] getParts() {
        return subEntities;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundTag) {
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        compoundTag.putInt("lifetime", lifetime);
        ListNBT anchors = new ListNBT();
        for (Vector3d vec : anchorPoints) {
            CompoundNBT anchor = new CompoundNBT();
            anchor.putFloat("x", (float) vec.x);
            anchor.putFloat("y", (float) vec.y);
            anchor.putFloat("z", (float) vec.z);
            anchors.add(anchor);
        }
        compoundTag.put("Anchors", anchors);
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundTag) {
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        if (compoundTag.contains("lifetime"))
            this.lifetime = compoundTag.getInt("lifetime");

        //9 is list tag id
        anchorPoints = new ArrayList<>();
        if (compoundTag.contains("Anchors", 9)) {
            ListNBT anchors = (ListNBT) compoundTag.get("Anchors");
            for (INBT tag : anchors) {
                if (tag instanceof CompoundNBT) {
                    CompoundNBT anchor = (CompoundNBT) tag;
                    anchorPoints.add(new Vector3d(anchor.getDouble("x"), anchor.getDouble("y"), anchor.getDouble("z")));
                }
            }
        }
        super.readAdditionalSaveData(compoundTag);

    }

    public void writeSpawnData(PacketBuffer buffer) {
        //Ironsspellbooks.logger.debug("WallOfFire.writeSpawnData");
        buffer.writeInt(anchorPoints.size());
        for (Vector3d vec : anchorPoints) {
            buffer.writeFloat((float) vec.x);
            buffer.writeFloat((float) vec.y);
            buffer.writeFloat((float) vec.z);
        }
    }

    public void readSpawnData(PacketBuffer additionalData) {
        //Ironsspellbooks.logger.debug("WallOfFire.readSpawnData");

        anchorPoints = new ArrayList<>();
        int length = additionalData.readInt();
        for (int i = 0; i < length; i++) {
            anchorPoints.add(new Vector3d(additionalData.readFloat(), additionalData.readFloat(), additionalData.readFloat()));
        }
        createShield();
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
