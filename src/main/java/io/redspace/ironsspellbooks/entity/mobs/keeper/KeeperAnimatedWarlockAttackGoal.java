package io.redspace.ironsspellbooks.entity.mobs.keeper;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.ClientboundSyncAnimation;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;

public class KeeperAnimatedWarlockAttackGoal extends WarlockAttackGoal {
    final KeeperEntity keeper;

    public KeeperAnimatedWarlockAttackGoal(KeeperEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval, float meleeRange) {
        super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval, meleeRange);
        keeper = abstractSpellCastingMob;
        nextAttack = randomizeNextAttack(0);
        this.meleeBias = 1f;
        this.wantsToMelee = true;
    }

    int meleeAnimTimer = -1;
    public KeeperEntity.AttackType currentAttack;
    public KeeperEntity.AttackType nextAttack;
    public KeeperEntity.AttackType queueCombo;
    private boolean hasLunged;
    private boolean hasHitLunge;
    private Vector3d oldLungePos;

    @Override
    protected void handleAttackLogic(double distanceSquared) {
        //Handling Animation hit frames
        float distance = MathHelper.sqrt((float) distanceSquared);
        mob.getLookControl().setLookAt(target.position());
        if (meleeAnimTimer > 0) {
            //We are currently attacking and are in a melee animation
            forceFaceTarget();
            meleeAnimTimer--;
            if (currentAttack.data.isHitFrame(meleeAnimTimer - 4)) {
                if (currentAttack != KeeperEntity.AttackType.Lunge) {
                    playSwingSound();
                }
            } else if (currentAttack.data.isHitFrame(meleeAnimTimer)) {
                if(currentAttack != KeeperEntity.AttackType.Lunge){
                    //mob.lookAt(target, 300, 300);
                    Vector3d lunge = target.position().subtract(mob.position()).normalize().scale(.55f)/*.add(0, 0.2, 0)*/;
                    mob.push(lunge.x, lunge.y, lunge.z);
                    if (distance <= meleeRange) {
                        boolean flag = this.mob.doHurtTarget(target);
                        target.invulnerableTime = 0;
                        if (flag) {
                            playImpactSound();
                            if (currentAttack.data.isSingleHit() && ((mob.getRandom().nextFloat() < .75f) || target.isBlocking())) {
                                //Attack again! combos!
                                queueCombo = randomizeNextAttack(0);
                            }
                        }
                    }
                }else{
                    if (!hasLunged) {
                        Vector3d lunge = target.position().subtract(mob.position()).normalize().multiply(2.4, .5, 2.4).add(0, 0.15, 0);
                        mob.push(lunge.x, lunge.y, lunge.z);
                        oldLungePos = mob.position();
                        mob.getNavigation().stop();
                        hasLunged = true;
                        playSwingSound();
                    }
                    if (!hasHitLunge && distance <= meleeRange * .45f) {
                        if (this.mob.doHurtTarget(target)) {
                            playImpactSound();
                        }
                        Vector3d knockback = oldLungePos.subtract(target.position());
                        target.knockback(1, knockback.x, knockback.z);
                        hasHitLunge = true;
                    }
                }
            }
        } else if (queueCombo != null && target != null && !target.isDeadOrDying()) {
            nextAttack = queueCombo;
            queueCombo = null;
            doMeleeAction();
        } else if (meleeAnimTimer == 0) {
            //Reset animations/attack
            nextAttack = randomizeNextAttack(distance);
            resetAttackTimer(distanceSquared);
            meleeAnimTimer = -1;
        } else {
            //Handling attack delay
            if (distance < meleeRange * (nextAttack == KeeperEntity.AttackType.Lunge ? 3 : 1)) {
                if (--this.attackTime == 0) {
                    doMeleeAction();
                } else if (this.attackTime < 0) {
                    resetAttackTimer(distanceSquared);
                }
            } else if (--this.attackTime < 0) {
                //Always keep the clock running, eventually he'll lunge to close distance. Otherwise he can be kited incredibly easily
                resetAttackTimer(distanceSquared);
                nextAttack = randomizeNextAttack(distance);
            }
        }
    }

    private KeeperEntity.AttackType randomizeNextAttack(float distance) {
        //Lunge is the last enum. If we are close, no need to lunge. if we are far, we favor lunging
        int i;
        if (distance < meleeRange * 1.5f) {
            i = KeeperEntity.AttackType.values().length - 1;
        } else if (mob.getRandom().nextFloat() < .25f && distance > meleeRange * 2.5f) {
            return KeeperEntity.AttackType.Lunge;
        } else {
            i = KeeperEntity.AttackType.values().length;
        }
        return KeeperEntity.AttackType.values()[mob.getRandom().nextInt(i)];
    }


    private void forceFaceTarget() {
        if (hasLunged)
            return;
        double d0 = target.getX() - mob.getX();
        double d1 = target.getZ() - mob.getZ();
        float yRot = (float) (MathHelper.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
        mob.setYBodyRot(yRot);
        mob.setYHeadRot(yRot);
        mob.yRot = (yRot);
    }

    @Override
    protected void doMeleeAction() {
        //anim duration
        currentAttack = nextAttack;
        if (currentAttack != null) {
            this.mob.swing(Hand.MAIN_HAND);
            meleeAnimTimer = currentAttack.data.lengthInTicks;
            hasLunged = false;
            hasHitLunge = false;
            Messages.sendToPlayersTrackingEntity(new ClientboundSyncAnimation<>(currentAttack.ordinal(), keeper), keeper);
        }
    }

    @Override
    protected void doMovement(double distanceSquared) {
        if (target.isDeadOrDying()) {
            this.mob.getNavigation().stop();
        } else if (distanceSquared > meleeRange * meleeRange) {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier * 1.3f);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() || meleeAnimTimer > 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.meleeAnimTimer = -1;
        this.queueCombo = null;
    }

    public void playSwingSound() {
        mob.playSound(SoundRegistry.KEEPER_SWING.get(), 1, Utils.randomBetweenInclusive(mob.getRandom(), 9, 13) * .1f);
    }

    public void playImpactSound() {
        mob.playSound(SoundRegistry.KEEPER_SWORD_IMPACT.get(), 1, Utils.randomBetweenInclusive(mob.getRandom(), 9, 13) * .1f);
    }
}
