package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class AcquireTargetNearLocationGoal<T extends LivingEntity> extends TargetGoal {
    private Vector3d targetSearchPos;
    protected final Class<T> targetType;
    protected final int randomInterval;
    @Nullable
    protected LivingEntity target;
    /**
     * This filter is applied to the Entity search. Only matching entities will be targeted.
     */
    protected EntityPredicate targetConditions;

    public AcquireTargetNearLocationGoal(MobEntity pMob, Class<T> pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, Vector3d targetSearchPos, @Nullable Predicate<LivingEntity> pTargetPredicate) {
        super(pMob, pMustSee, pMustReach);
        this.targetType = pTargetType;
        this.randomInterval = reducedTickDelay(pRandomInterval);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.targetConditions = EntityPredicate.forCombat().range(this.getFollowDistance()).selector(pTargetPredicate);
        this.targetSearchPos = targetSearchPos;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            //irons_spellbooks.LOGGER.debug("AcquireTargetNearLocationGoal.canUse: false");
            return false;
        } else {
            this.findTarget();
            //irons_spellbooks.LOGGER.debug("AcquireTargetNearLocationGoal.canUse: {}", this.target);
            return this.target != null;
        }
    }

    protected AxisAlignedBB getTargetSearchArea(double pTargetDistance) {
        return this.mob.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
    }

    protected void findTarget() {
        if (this.targetType != PlayerEntity.class && this.targetType != ServerPlayerEntity.class) {
            var targetSearchArea = this.getTargetSearchArea(this.getFollowDistance());
            //irons_spellbooks.LOGGER.debug("AcquireTargetNearLocationGoal.findTarget.1 {}", targetSearchArea);
            var entitiesOfClass = this.mob.level.getEntitiesOfClass(this.targetType, targetSearchArea, (potentialTarget) -> {
                //irons_spellbooks.LOGGER.debug("AcquireTargetNearLocationGoal.findTarget.2 {}", potentialTarget.getName().getString());
                return true;
            });
            this.target = this.mob.level.getNearestEntity(entitiesOfClass, this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            //irons_spellbooks.LOGGER.debug("AcquireTargetNearLocationGoal.findTarget.6");
            this.target = this.mob.level.getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity pTarget) {
        this.target = pTarget;
    }
}