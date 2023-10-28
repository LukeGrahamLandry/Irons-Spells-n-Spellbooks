package io.redspace.ironsspellbooks.spells;

import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import net.minecraft.util.math.vector.Vector3d;

public class TargetAreaCastData extends EntityCastData {

    Vector3d center;

    public TargetAreaCastData(Vector3d center, TargetedAreaEntity entity) {
        super(entity);
        this.center = center;
    }

    public Vector3d getCenter() {
        return center;
    }

}