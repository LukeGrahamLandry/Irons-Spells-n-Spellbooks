package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
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
        if (mob.position().horizontalDistanceSqr() > radiusSqr)
            f = LandRandomPos.getPosTowards(mob, 8, 4, origin.resolve().get());

        //IronsSpellbooks.LOGGER.debug("PatrolNearLocationGoal newPosition: {}", f);
        return f;

    }
}