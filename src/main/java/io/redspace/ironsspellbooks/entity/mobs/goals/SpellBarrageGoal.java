package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class SpellBarrageGoal extends Goal {
    protected static final int interval = 5;
    protected final AbstractSpellCastingMob mob;
    protected LivingEntity target;
    protected final int attackIntervalMin;
    protected final int attackIntervalMax;
    protected final float attackRadius;
    protected final float attackRadiusSqr;
    protected final int projectileCount;
    protected final AbstractSpell spell;
    protected int attackTime;

    protected final int minSpellLevel;
    protected final int maxSpellLevel;

    public SpellBarrageGoal(AbstractSpellCastingMob abstractSpellCastingMob, AbstractSpell spell, int minLevel, int maxLevel, int pAttackIntervalMin, int pAttackIntervalMax, int projectileCount) {
        this.mob = abstractSpellCastingMob;
        this.attackIntervalMin = pAttackIntervalMin;
        this.attackIntervalMax = pAttackIntervalMax;
        this.attackRadius = 20;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.minSpellLevel = minLevel;
        this.maxSpellLevel = maxLevel;
        this.projectileCount = projectileCount;
        this.spell = spell;
        resetAttackTimer();
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        target = this.mob.getTarget();
        if (target == null || mob.isCasting())
            return false;

        if (attackTime <= -interval * (projectileCount - 1)) {
            //IronsSpellbooks.LOGGER.debug("SpellBarrageGoal.canUse resetAttackTimer");
            resetAttackTimer();
        }
        attackTime--;
        //IronsSpellbooks.LOGGER.debug("SpellBarrageGoal.canUse: spell:{}, attackTime:{}, interval:{}, a%i:{}, result:{}", spell, attackTime, interval, attackTime % interval, attackTime <= 0 && attackTime % interval == 0);
        return attackTime <= 0 && attackTime % interval == 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return false;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        //IronsSpellbooks.LOGGER.debug("SpellBarrageGoal.stop");
        this.target = null;
        if (attackTime > 0)
            this.attackTime = -projectileCount * interval - 1;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        //IronsSpellbooks.LOGGER.debug("SpellBarrageGoal.tick: spell:{}, attackTime:{}, interval:{}, a%i:{}, result:{}", spell, attackTime, interval, attackTime % interval, attackTime <= 0 && attackTime % interval == 0);

        if (target == null) {
            return;
        }

        double distanceSquared = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        if (distanceSquared < attackRadiusSqr) {
            //IronsSpellbooks.LOGGER.debug("SpellBarrageGoal ({}) initiate cast on tick {}", this.hashCode(), attackTime);
            this.mob.getLookControl().setLookAt(this.target, 45, 45);
            mob.initiateCastSpell(spell, mob.getRandom().nextInt(minSpellLevel, maxSpellLevel + 1));
            stop();
        }


    }

    protected void resetAttackTimer() {
        this.attackTime = mob.getRandom().nextInt(attackIntervalMin, attackIntervalMax + 1);
    }
}