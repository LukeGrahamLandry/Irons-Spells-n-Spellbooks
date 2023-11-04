package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class GenericDefendVillageTargetGoal extends TargetGoal {
    private final MobEntity protector;
    @Nullable
    private LivingEntity potentialTarget;
    private final EntityPredicate attackTargeting = (new EntityPredicate()).range(64.0D);

    public GenericDefendVillageTargetGoal(MobEntity mob) {
        super(mob, false, true);
        this.protector = mob;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        AxisAlignedBB aabb = this.protector.getBoundingBox().inflate(10.0D, 8.0D, 10.0D);
        List<? extends LivingEntity> list = this.protector.level.getNearbyEntities(VillagerEntity.class, this.attackTargeting, this.protector, aabb);
        List<PlayerEntity> list1 = this.protector.level.getNearbyPlayers(this.attackTargeting, this.protector, aabb);

        for(LivingEntity livingentity : list) {
            VillagerEntity villager = (VillagerEntity)livingentity;

            for(PlayerEntity player : list1) {
                int i = villager.getPlayerReputation(player);
                if (i <= -100) {
                    this.potentialTarget = player;
                }
            }
        }

        if (this.potentialTarget == null) {
            return false;
        } else {
            return !(this.potentialTarget instanceof PlayerEntity) || !this.potentialTarget.isSpectator() && !((PlayerEntity)this.potentialTarget).isCreative();
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.protector.setTarget(this.potentialTarget);
        super.start();
    }
}
