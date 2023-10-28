package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.spells.ICastData;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.UUID;

public class CastTargetingData implements ICastData {
    //private Entity castingEntity;
    //private LivingEntity targetEntity;
    private UUID targetUUID;

    public CastTargetingData(LivingEntity target) {
        //this.targetEntity = target;
        this.targetUUID = target.getUUID();
    }

    @Override
    public void reset() {

    }

    @Nullable
    public LivingEntity getTarget(ServerWorld level) {
        return (LivingEntity) level.getEntity(targetUUID);
    }

    @Nullable
    public Vector3d getTargetPosition(ServerWorld level) {
        return getTarget(level) == null ? null : getTarget(level).position();
    }
}