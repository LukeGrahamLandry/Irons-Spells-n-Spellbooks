package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.registries.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.IServerWorld;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.EquipmentSlotType;

public class DeadKingBoss extends AbstractSpellCastingMob implements IMob {
    public enum Phases {
        FirstPhase(0),
        Transitioning(1),
        FinalPhase(2);
        final int value;

        Phases(int value) {
            this.value = value;
        }
    }

    private final ServerBossInfo bossEvent = (ServerBossInfo) (new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenScreen(true).setCreateWorldFog(true);
    private final static DataParameter<Integer> PHASE = EntityDataManager.defineId(DeadKingBoss.class, DataSerializers.INT);
    private final static DataParameter<Boolean> NEXT_SLAM = EntityDataManager.defineId(DeadKingBoss.class, DataSerializers.BOOLEAN);
    private int transitionAnimationTime = 140; // Animation Length in ticks
    private boolean isCloseToGround;

    public DeadKingBoss(EntityType<? extends AbstractSpellCastingMob> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        xpReward = 60;
    }

    public DeadKingBoss(World pLevel) {
        this(EntityRegistry.DEAD_KING.get(), pLevel);
    }

    @Override
    protected void registerGoals() {
        setFirstPhaseGoals();

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, VillagerEntity.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractIllagerEntity.class, true));

        //this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        //this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    private DeadKingAnimatedWarlockAttackGoal getCombatGoal() {
        return (DeadKingAnimatedWarlockAttackGoal) new DeadKingAnimatedWarlockAttackGoal(this, 1f, 55, 85, 3.5f).setSpellQuality(.3f, .5f).setSpells(
                List.of(
                        SpellType.RAY_OF_SIPHONING_SPELL,
                        SpellType.BLOOD_SLASH_SPELL, SpellType.BLOOD_SLASH_SPELL,
                        SpellType.WITHER_SKULL_SPELL, SpellType.WITHER_SKULL_SPELL, SpellType.WITHER_SKULL_SPELL,
                        SpellType.FANG_STRIKE_SPELL, SpellType.FANG_STRIKE_SPELL,
                        SpellType.POISON_ARROW_SPELL, SpellType.POISON_ARROW_SPELL,
                        SpellType.BLIGHT_SPELL,
                        SpellType.ACID_ORB_SPELL
                ),
                List.of(SpellType.FANG_WARD_SPELL, SpellType.BLOOD_STEP_SPELL),
                List.of(/*SpellType.BLOOD_STEP_SPELL*/),
                List.of()
        ).setMeleeBias(0.75f);
    }

    protected void setFirstPhaseGoals() {
        this.goalSelector.removeAllGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new SpellBarrageGoal(this, SpellType.WITHER_SKULL_SPELL, 3, 4, 70, 140, 3));
        this.goalSelector.addGoal(2, new SpellBarrageGoal(this, SpellType.RAISE_DEAD_SPELL, 3, 5, 600, 900, 1));
        this.goalSelector.addGoal(3, new SpellBarrageGoal(this, SpellType.BLOOD_STEP_SPELL, 1, 1, 100, 180, 1));
        this.goalSelector.addGoal(4, getCombatGoal().setSingleUseSpell(SpellType.RAISE_DEAD_SPELL, 10, 50, 8, 8));
        this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32, 0.9f));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));

    }

    protected void setFinalPhaseGoals() {
        this.goalSelector.removeAllGoals();
        this.goalSelector.addGoal(1, new SpellBarrageGoal(this, SpellType.WITHER_SKULL_SPELL, 5, 5, 60, 140, 4));
        this.goalSelector.addGoal(2, new SpellBarrageGoal(this, SpellType.SUMMON_VEX_SPELL, 3, 5, 400, 600, 1));
        this.goalSelector.addGoal(3, new SpellBarrageGoal(this, SpellType.BLOOD_STEP_SPELL, 1, 1, 100, 180, 1));
        this.goalSelector.addGoal(4, getCombatGoal().setIsFlying().setSingleUseSpell(SpellType.BLAZE_STORM_SPELL, 10, 30, 10, 10));
        this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32, 0.9f));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.hasUsedSingleAttack = false;
        //this.goalSelector.addGoal(2, new VexRandomMoveGoal());
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundRegistry.DEAD_KING_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.DEAD_KING_DEATH.get();
    }

    @Override
    public boolean isPushable() {
        return !isPhaseTransitioning();
    }

    protected SoundEvent getStepSound() {
        if (isPhase(Phases.FirstPhase))
            return SoundEvents.SKELETON_STEP;
        else
            return SoundEvents.SOUL_ESCAPE;
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld pLevel, DifficultyInstance pDifficulty, SpawnReason pReason, @Nullable ILivingEntityData pSpawnData, @Nullable CompoundNBT pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        return pSpawnData;
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        return super.isAlliedTo(pEntity) || (pEntity instanceof MagicSummon summon && summon.getSummoner() == this);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(ItemRegistry.BLOOD_STAFF.get()));
        this.setDropChance(EquipmentSlotType.OFFHAND, 0f);
//        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemRegistry.WANDERING_MAGICIAN_ROBE.get()));
//        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
    }

    //Instead of being undead (smite is ridiculous)
    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void tick() {
        if (isPhase(Phases.FinalPhase)) {
            //vex type beat
            setNoGravity(true);
            //this.noPhysics = true;
            if (tickCount % 10 == 0) {
                isCloseToGround = Utils.raycastForBlock(level, position(), position().subtract(0, 2.5, 0), RayTraceContext.FluidMode.ANY).getType() == RayTraceResult.Type.BLOCK;
            }
            Vector3d woosh = new Vector3d(
                    MathHelper.sin((tickCount * 5) * MathHelper.DEG_TO_RAD),
                    (MathHelper.cos((tickCount * 3 + 986741) * MathHelper.DEG_TO_RAD) + (isCloseToGround ? .05 : -.185)) * .5f,
                    MathHelper.sin((tickCount * 1 + 465) * MathHelper.DEG_TO_RAD)
            );
            if (this.getTarget() == null)
                woosh = woosh.scale(.25f);
            this.setDeltaMovement(getDeltaMovement().add(woosh.scale(.0085f)));
        }
        super.tick();

        if (level.isClientSide) {
            if (isPhase(Phases.FinalPhase)) {
                if (!this.isInvisible()) {
                    float radius = .35f;
                    for (int i = 0; i < 5; i++) {
                        Vector3d random = position().add(new Vector3d(
                                (this.random.nextFloat() * 2 - 1) * radius,
                                1 + (this.random.nextFloat() * 2 - 1) * radius,
                                (this.random.nextFloat() * 2 - 1) * radius
                        ));
                        level.addParticle(ParticleTypes.SMOKE, random.x, random.y, random.z, 0, -.1, 0);

                    }

                }
            }

        } else {
            //irons_spellbooks.LOGGER.debug("DeadKingBoss.tick | Phase: {} | isTransitioning: {} | TransitionTime: {}", getPhase(), isPhaseTransitioning(), transitionAnimationTime);
            float halfHealth = this.getMaxHealth() / 2;
            if (isPhase(Phases.FirstPhase)) {
                this.bossEvent.setProgress((this.getHealth() - halfHealth) / (this.getMaxHealth() - halfHealth));
                if (this.getHealth() <= halfHealth) {
                    setPhase(Phases.Transitioning);
                    var player = level.getNearestPlayer(this, 16);
                    if (player != null)
                        lookAt(player, 360, 360);
                    if (!isDeadOrDying())
                        setHealth(halfHealth);
                    playSound(SoundRegistry.DEAD_KING_FAKE_DEATH.get());
                    //Overriding isInvulnerable just doesn't seem to work
                    setInvulnerable(true);
                }
            } else if (isPhase(Phases.Transitioning)) {
                if (--transitionAnimationTime <= 0) {
                    setPhase(Phases.FinalPhase);
                    MagicManager.spawnParticles(level, ParticleHelper.FIRE, position().x, position().y + 2.5, position().z, 80, .2, .2, .2, .25, true);
                    setFinalPhaseGoals();
                    setNoGravity(true);
                    playSound(SoundRegistry.DEAD_KING_EXPLODE.get());
                    level.getEntities(this, this.getBoundingBox().inflate(5), (entity) -> entity.distanceToSqr(position()) < 5 * 5).forEach(super::doHurtTarget);
                    setInvulnerable(false);
                }
            } else if (isPhase(Phases.FinalPhase)) {
                this.bossEvent.setProgress(this.getHealth() / (this.getMaxHealth() - halfHealth));
            }
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntitySize pDimensions) {
        return pDimensions.height * 0.95F;

    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        if (isPhase(Phases.FinalPhase))
            return false;
        return super.causeFallDamage(pFallDistance, pMultiplier, pSource);
    }

    public boolean isPhase(Phases phase) {
        return phase.value == getPhase();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        //reduces damage of projectiles and summons
        if (pSource instanceof IndirectEntityDamageSource)
            pAmount *= .75f;
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected boolean isImmobile() {
        return isPhase(Phases.Transitioning) || super.isImmobile();
    }

    public boolean isPhaseTransitioning() {
        return isPhase(Phases.Transitioning);
    }

    public void startSeenByPlayer(ServerPlayerEntity pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossEvent.addPlayer(pPlayer);
    }

    public void stopSeenByPlayer(ServerPlayerEntity pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossEvent.removePlayer(pPlayer);
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(AttributeRegistry.SPELL_POWER.get(), 1.15)
                .add(Attributes.ARMOR, 15)
                .add(AttributeRegistry.SPELL_RESIST.get(), 1)
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.ATTACK_KNOCKBACK, .6)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MOVEMENT_SPEED, .155);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent pName) {
        super.setCustomName(pName);
        this.bossEvent.setName(this.getDisplayName());
    }

    private void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    private void setPhase(Phases phase) {
        this.setPhase(phase.value);
    }

    private int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setNextSlam(boolean slam) {
        this.entityData.set(NEXT_SLAM, slam);
    }

    public boolean isNextSlam() {
        return this.entityData.get(NEXT_SLAM);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("phase", getPhase());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
        setPhase(pCompound.getInt("phase"));
        if (isPhase(Phases.FinalPhase))
            setFinalPhaseGoals();

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 0);
        this.entityData.define(NEXT_SLAM, false);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private final AnimationBuilder phase_transition_animation = new AnimationBuilder().addAnimation("dead_king_die", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private final AnimationBuilder idle = new AnimationBuilder().addAnimation("dead_king_idle", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private final AnimationBuilder melee = new AnimationBuilder().addAnimation("dead_king_melee", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private final AnimationBuilder slam = new AnimationBuilder().addAnimation("dead_king_slam", ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    private final AnimationController transitionController = new AnimationController(this, "dead_king_transition", 0, this::transitionPredicate);
    private final AnimationController meleeController = new AnimationController(this, "dead_king_animations", 0, this::predicate);
    private final AnimationController idleController = new AnimationController(this, "dead_king_idle", 0, this::idlePredicate);

    private PlayState predicate(AnimationEvent animationEvent) {
        var controller = animationEvent.getController();
//        if (isPhaseTransitioning() && controller.getAnimationState() == AnimationState.Stopped) {
//            controller.markNeedsReload();
//            controller.setAnimation(phase_transition_animation);
//            return PlayState.CONTINUE;
//        }
        if (this.swinging) {
            controller.markNeedsReload();
            if (isNextSlam()) {
                controller.setAnimation(slam);
            } else {
                controller.setAnimation(melee);
            }
            swinging = false;
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private PlayState transitionPredicate(AnimationEvent animationEvent) {
        var controller = animationEvent.getController();
        if (isPhaseTransitioning()) {
            controller.setAnimation(phase_transition_animation);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    private PlayState idlePredicate(AnimationEvent animationEvent) {
        if (isAnimating())
            return PlayState.STOP;
        if (animationEvent.getController().getAnimationState() == AnimationState.Stopped)
            animationEvent.getController().setAnimation(idle);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(transitionController);
        data.addAnimationController(meleeController);
        data.addAnimationController(idleController);
        super.registerControllers(data);
    }

    @Override
    public boolean shouldAlwaysAnimateHead() {
        return !isPhaseTransitioning();
    }

    @Override
    public boolean isAnimating() {
        return meleeController.getAnimationState() != AnimationState.Stopped || super.isAnimating();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        level.playSound(null, getX(), getY(), getZ(), SoundRegistry.DEAD_KING_HIT.get(), SoundCategory.HOSTILE, 1, 1);
        return super.doHurtTarget(pEntity);
    }

}
