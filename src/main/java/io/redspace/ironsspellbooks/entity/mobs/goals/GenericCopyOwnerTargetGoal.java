package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

public class GenericCopyOwnerTargetGoal extends TargetGoal {
    private final OwnerGetter ownerGetter;

    public GenericCopyOwnerTargetGoal(CreatureEntity pMob, OwnerGetter ownerGetter) {
        super(pMob, false);
        this.ownerGetter = ownerGetter;

    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        return ownerGetter.get() instanceof MobEntity && ((MobEntity) ownerGetter.get()).getTarget() != null && !(((MobEntity) ownerGetter.get()).getTarget() instanceof MagicSummon && ((MagicSummon) ((MobEntity) ownerGetter.get()).getTarget()).getSummoner() == (MobEntity) ownerGetter.get());
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        LivingEntity target = ((MobEntity) ownerGetter.get()).getTarget();
        mob.setTarget(target);
        this.mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, target, 200L);

        super.start();
    }
}