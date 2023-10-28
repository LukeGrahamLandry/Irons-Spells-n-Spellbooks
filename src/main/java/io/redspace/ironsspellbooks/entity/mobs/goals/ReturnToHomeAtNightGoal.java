package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

public class ReturnToHomeAtNightGoal<T extends CreatureEntity & HomeOwner> extends WaterAvoidingRandomWalkingGoal {

    T homeOwnerMob;

    public ReturnToHomeAtNightGoal(T pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier);
        this.homeOwnerMob = pMob;
    }

    @Override
    public boolean canUse() {
        return homeOwnerMob.getHome() != null && !mob.level.isDay() && super.canUse();
    }

    @Nullable
    @Override
    protected Vector3d getPosition() {
        return homeOwnerMob.getHome() == null ? super.getPosition() : Vector3d.atBottomCenterOf(homeOwnerMob.getHome());
    }
}