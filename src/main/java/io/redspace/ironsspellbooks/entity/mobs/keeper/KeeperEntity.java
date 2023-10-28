package io.redspace.ironsspellbooks.entity.mobs.keeper;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.AttackAnimationData;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IServerWorld;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nullable;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.EquipmentSlotType;

public class KeeperEntity extends AbstractSpellCastingMob implements IMob, AnimatedAttacker {

    //private static final EntityDataAccessor<Integer> DATA_ATTACK_TYPE = SynchedEntityData.defineId(KeeperEntity.class, EntityDataSerializers.INT);

    public enum AttackType {
        //data measured from blockbench
        Double_Slash(43, "sword_double_slash", 13, 29),
        //Triple_Slash(41, "sword_triple_slash", 11, 21, 35),
        //Slash_Stab(38, "sword_slash_stab", 13, 33),
        Single_Upward(26, "sword_single_upward", 13),
        Single_Horizontal(28, "sword_single_horizontal", 12),
        Single_Horizontal_Fast(24, "sword_single_horizontal_fast", 12),
        Single_Stab(21, "sword_stab", 11),
        Lunge(76, "sword_lunge", 56, 57, 58, 59, 60, 61, 62, 63, 64);

        AttackType(int lengthInTicks, String animationId, int... attackTimestamps) {
            this.data = new AttackAnimationData(lengthInTicks, animationId, attackTimestamps);
        }

        public final AttackAnimationData data;

    }

    public KeeperEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        xpReward = 25;
        maxUpStep = 1f;
        this.lookControl = createLookControl();
        this.moveControl = createMoveControl();
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntitySize pDimensions) {
        return pDimensions.height;
    }

    public KeeperEntity(World pLevel) {
        this(EntityRegistry.KEEPER.get(), pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(4, new KeeperAnimatedWarlockAttackGoal(this, 1f, 10, 30, 3.5f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, true, (entity) -> !(entity instanceof KeeperEntity)));
    }

    @Override
    protected BodyController createBodyControl() {
        return new BodyController(this);
    }

    protected LookController createLookControl() {
        return new LookController(this) {
            //This allows us to more rapidly turn towards our target. Helps to make sure his targets are aligned with his swing animations
            @Override
            protected float rotateTowards(float pFrom, float pTo, float pMaxDelta) {
                return super.rotateTowards(pFrom, pTo, pMaxDelta * 2.5f);
            }
        };
    }

    protected MovementController createMoveControl() {
        return new MovementController(this) {
            //This fixes a bug where a mob tries to path into the block it's already standing, and spins around trying to look "forward"
            //We nullify our rotation calculation if we are close to block we are trying to get to
            @Override
            protected float rotlerp(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                if (d0 * d0 + d1 * d1 < .5f) {
                    return pSourceAngle;
                } else {
                    return super.rotlerp(pSourceAngle, pTargetAngle, pMaximumChange * .25f);
                }
            }
        };
    }

    @NotNull
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.KEEPER_IDLE.get();
    }

    @Override
    public void playAmbientSound() {
        this.playSound(getAmbientSound(), 1, MathHelper.randomBetweenInclusive(getRandom(), 5, 10) * .1f);
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundRegistry.KEEPER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.KEEPER_DEATH.get();
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld pLevel, DifficultyInstance pDifficulty, SpawnReason pReason, @Nullable ILivingEntityData pSpawnData, @Nullable CompoundNBT pDataTag) {
        RandomSource randomsource = Utils.random;
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        return pSpawnData;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemRegistry.KEEPER_FLAMBERGE.get()));
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.FOLLOW_RANGE, 25.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .add(Attributes.MOVEMENT_SPEED, .19);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }


    private final AnimationController<KeeperEntity> meleeController = new AnimationController<>(this, "keeper_animations", 0, this::predicate);

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getDirectEntity() instanceof ProjectileEntity projectile) {
            pAmount *= .75f;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        super.playStepSound(pPos, pState);
        if (!pState.getMaterial().isLiquid()) {
            this.playSound(SoundRegistry.KEEPER_STEP.get(), .25f, 1f);
        }
    }

    @Override
    protected float nextStep() {
        return moveDist + .8f;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return super.isInvulnerableTo(pSource) || pSource.isFall();
    }

    AnimationBuilder animationToPlay = null;

    @Override
    public void playAnimation(int animationId) {
        if (animationId >= 0 && animationId < AttackType.values().length)
            animationToPlay = new AnimationBuilder().addAnimation(AttackType.values()[animationId].data.animationId, ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    }

    private PlayState predicate(AnimationEvent<KeeperEntity> animationEvent) {
        var controller = animationEvent.getController();

        if (this.animationToPlay != null) {
            controller.markNeedsReload();
            controller.setAnimation(animationToPlay);
            animationToPlay = null;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(meleeController);
        super.registerControllers(data);
    }


    @Override
    public boolean isAnimating() {
        return meleeController.getAnimationState() != AnimationState.Stopped || super.isAnimating();
    }

    @Override
    public boolean shouldAlwaysAnimateLegs() {
        return false;
    }

    //    @Override
//    public boolean doHurtTarget(Entity pEntity) {
//        level.playSound(null, getX(), getY(), getZ(), SoundRegistry.DEAD_KING_HIT.get(), SoundSource.HOSTILE, 1, 1);
//        return super.doHurtTarget(pEntity);
//    }

}
