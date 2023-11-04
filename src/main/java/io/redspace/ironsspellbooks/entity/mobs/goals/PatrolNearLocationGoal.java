package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class PatrolNearLocationGoal extends WaterAvoidingRandomWalkingGoal {

    LazyOptional<Vector3d> origin;
    float radiusSqr;

    public PatrolNearLocationGoal(CreatureEntity pMob, float radius, double pSpeedModifier) {
        super(pMob, pSpeedModifier);
        origin = LazyOptional.of(pMob::position);
        radiusSqr = radius * radius;
    }

    @Nullable
    @Override
    protected Vector3d getPosition() {
        Vector3d f = super.getPosition();
        //IronsSpellbooks.LOGGER.debug("PatrolNearLocationGoal origin: {}", origin.resolve().get());
        // This used to be mob.position().horizontalDistanceSqr() > radiusSqr
        // Which seems wrong. That would be distance from 0,0,0 not from start?
        Vector3d home = origin.resolve().get();
        Vector3d diff = mob.position().subtract(home);
        double horizontalDistanceSqr = diff.x * diff.x + diff.z * diff.z;
        if (horizontalDistanceSqr > radiusSqr)
            f = RandomPositionGenerator.getLandPosTowards(mob, 8, 4, home);

        //IronsSpellbooks.LOGGER.debug("PatrolNearLocationGoal newPosition: {}", f);
        return f;

    }
}