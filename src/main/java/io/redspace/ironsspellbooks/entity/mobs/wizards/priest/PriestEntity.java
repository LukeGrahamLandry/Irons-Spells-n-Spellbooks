package io.redspace.ironsspellbooks.entity.mobs.wizards.priest;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.SupportMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.*;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IServerWorld;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkNodeProcessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.inventory.EquipmentSlotType;

public class PriestEntity extends NeutralWizard implements IVillagerDataHolder, SupportMob, HomeOwner {
    private static final DataParameter<VillagerData> DATA_VILLAGER_DATA = EntityDataManager.defineId(PriestEntity.class, DataSerializers.VILLAGER_DATA);
    private static final DataParameter<Boolean> DATA_VILLAGER_UNHAPPY = EntityDataManager.defineId(PriestEntity.class, DataSerializers.BOOLEAN);
    public GoalSelector supportTargetSelector;
    private int unhappyTimer;

    public PriestEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        xpReward = 15;

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(1, new GustDefenseGoal(this));
        this.goalSelector.addGoal(2, new WizardSupportGoal<>(this, 1.5f, 100, 180)
                .setSpells(
                        List.of(SpellRegistry.BLESSING_OF_LIFE_SPELL.get(), SpellRegistry.BLESSING_OF_LIFE_SPELL.get(), SpellRegistry.HEALING_CIRCLE_SPELL.get()),
                        List.of(SpellRegistry.FORTIFY_SPELL.get())
                ));
        this.goalSelector.addGoal(3, new WizardAttackGoal(this, 1.5f, 35, 70)
                .setSpells(
                        List.of(SpellRegistry.WISP_SPELL.get(), SpellRegistry.GUIDING_BOLT_SPELL.get(), SpellRegistry.GUIDING_BOLT_SPELL.get()),
                        List.of(SpellRegistry.GUST_SPELL.get()),
                        List.of(),
                        List.of(SpellRegistry.HEAL_SPELL.get()))
                .setSpellQuality(0.3f, 0.5f)
                .setDrinksPotions());
        this.goalSelector.addGoal(5, new RoamVillageGoal(this, 30, 1f));
        this.goalSelector.addGoal(6, new ReturnToHomeAtNightGoal<>(this, 1f));
        this.goalSelector.addGoal(7, new PatrolNearLocationGoal(this, 30, 1f));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(10, new WizardRecoverGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new GenericDefendVillageTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, (mob) -> mob instanceof IMob && !(mob instanceof CreeperEntity)));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));

        this.supportTargetSelector = new GoalSelector(this.level.getProfilerSupplier());
        this.supportTargetSelector.addGoal(0, new FindSupportableTargetGoal<>(this, LivingEntity.class, true,
                (mob) -> !isAngryAt(mob) && mob.getHealth() * 1.25f < mob.getMaxHealth() && (mob.getType().is(ModTags.VILLAGE_ALLIES) || mob instanceof PlayerEntity))
        );
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld pLevel, DifficultyInstance pDifficulty, SpawnReason pReason, @Nullable ILivingEntityData pSpawnData, @Nullable CompoundNBT pDataTag) {
        this.populateDefaultEquipmentSlots(pDifficulty);
        this.setHome(this.blockPosition());
        IronsSpellbooks.LOGGER.debug("Priest new home: {}", this.getHome());
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(ItemRegistry.PRIEST_HELMET.get()));
        this.setItemSlot(EquipmentSlotType.CHEST, new ItemStack(ItemRegistry.PRIEST_CHESTPLATE.get()));
        this.setDropChance(EquipmentSlotType.HEAD, 0.0F);
        this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(AttributeRegistry.CAST_TIME_REDUCTION.get(), 1.5)
                .add(Attributes.MOVEMENT_SPEED, .23);
    }

    @Override
    protected PathNavigator createNavigation(World pLevel) {
        return new GroundPathNavigator(this, pLevel) {
            @Override
            protected PathFinder createPathFinder(int pMaxVisitedNodes) {
                this.nodeEvaluator = new WalkNodeProcessor();
                this.nodeEvaluator.setCanPassDoors(true);
                this.nodeEvaluator.setCanOpenDoors(true);
                return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
            }
//
//            @Override
//            public NodeEvaluator getNodeEvaluator() {
//                var nodeEvalutator = super.getNodeEvaluator();
//                nodeEvalutator.setCanPassDoors(true);
//                nodeEvalutator.setCanOpenDoors(true);
//                return nodeEvalutator;
//            }
        };
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
        this.entityData.define(DATA_VILLAGER_UNHAPPY, false);
    }

    public void setVillagerData(VillagerData villagerdata) {
        //VillagerData villagerdata = this.getVillagerData();
        villagerdata.setProfession(VillagerProfession.NONE);
        this.entityData.set(DATA_VILLAGER_DATA, villagerdata);
    }

    public boolean isUnhappy() {
        return this.entityData.get(DATA_VILLAGER_UNHAPPY);
    }

    public @NotNull VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    LivingEntity supportTarget;

    @org.jetbrains.annotations.Nullable
    @Override
    public LivingEntity getSupportTarget() {
        return supportTarget;
    }

    @Override
    public void setSupportTarget(LivingEntity target) {
        this.supportTarget = target;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        //Vanilla does this seemingly-excessive tick count solution. maybe there's a method to the madness
        if (this.tickCount % 4 == 0 && this.tickCount > 1) {
            this.supportTargetSelector.tick();
        }
        if (this.tickCount % 60 == 0) {
            this.level.getEntities(this, this.getBoundingBox().inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE)), (entity) -> entity instanceof IMob && !(entity instanceof CreeperEntity)).forEach((enemy) -> {
                if (enemy instanceof MobEntity) {
                    MobEntity mob = (MobEntity) enemy;
                    if (mob.getTarget() == null)
                        if ((new EntityPredicate()).test(mob, this))
                            mob.setTarget(this);
                }
            });
        }
        if (unhappyTimer > 0)
            if (--unhappyTimer == 0)
                this.entityData.set(DATA_VILLAGER_UNHAPPY, false);
//        this.level.getProfiler().push("priestBrain");
//        this.getBrain().tick((ServerLevel) this.level, this);
//        this.level.getProfiler().pop();
//        this.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.CORE));

    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity pPlayer, Hand pHand) {
        setUnhappy();
        return super.mobInteract(pPlayer, pHand);
    }

    public void setUnhappy() {
        if (!level.isClientSide) {
            this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
            unhappyTimer = 20;
            this.entityData.set(DATA_VILLAGER_UNHAPPY, true);
        }
    }

    BlockPos homePos;

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockPos getHome() {
        return homePos;
    }

    @Override
    public void setHome(BlockPos homePos) {
        this.homePos = homePos;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        serializeHome(this, pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        deserializeHome(this, pCompound);
    }



    /*
    Brain Testing
     */
/*
    public Brain<PriestEntity> getBrain() {
        return (Brain<PriestEntity>) super.getBrain();
    }

    protected Brain.Provider<PriestEntity> brainProvider() {
        return Brain.provider(
                //Memories
                ImmutableList.of(
                        MemoryModuleType.WALK_TARGET,
                        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                        MemoryModuleType.PATH,
                        //MemoryModuleType.MEETING_POINT,
                        MemoryModuleType.LOOK_TARGET,
                        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES
                ),
                //Sensors
                ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY));
    }
    //ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);

    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<PriestEntity> brain = this.brainProvider().makeBrain(pDynamic);
        this.registerBrainGoals(brain);
        return brain;
    }
//
//    public void refreshBrain(ServerLevel pServerLevel) {
//        Brain<PriestEntity> brain = this.getBrain();
//        brain.stopAll(pServerLevel, this);
//        this.brain = brain.copyWithoutBehaviors();
//        this.registerBrainGoals(this.getBrain());
//    }

    private void registerBrainGoals(Brain<PriestEntity> brain) {

        ImmutableList<Pair<Integer, ? extends Behavior<? super PriestEntity>>> core = ImmutableList.of(
                Pair.of(0, new Swim(0.8F)),
                Pair.of(0, new InteractWithDoor()),

                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(1, new MoveToTargetSink())

//                Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))),
//
//                //Pair.of(0, new SetRaidStatus()),
//                Pair.of(3, new RunOne<>(ImmutableList.of(
//                        Pair.of(new RandomStroll(1.0F), 2),
//                        Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 2),
//                        Pair.of(new DoNothing(30, 60), 1)
//                )))
//                Pair.of(10, new AcquirePoi((p_217499_) -> {
//                    return p_217499_.is(PoiTypes.HOME);
//                }, MemoryModuleType.HOME, false, Optional.of((byte) 14))),
//                Pair.of(10, new AcquirePoi((p_217497_) -> {
//                    return p_217497_.is(PoiTypes.MEETING);
//                }, MemoryModuleType.MEETING_POINT, true, Optional.of((byte) 14)))
        );
        ImmutableList<Pair<Integer, ? extends Behavior<? super PriestEntity>>> idle = ImmutableList.of(
                Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))),

                Pair.of(3, new RunOne<>(ImmutableList.of(
                        Pair.of(new RandomStroll(1.0F), 2),
                        Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 2),
                        Pair.of(new DoNothing(30, 60), 1)
                )))

        );
        brain.addActivity(Activity.CORE, core);
        brain.addActivity(Activity.IDLE, idle);
//        brain.addActivityWithConditions(Activity.MEET, VillagerGoalPackages.getMeetPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
//        brain.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(villagerprofession, 0.5F));
//        brain.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(villagerprofession, 0.5F));
//        brain.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(villagerprofession, 0.5F));
//        brain.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(villagerprofession, 0.5F));
//        brain.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(villagerprofession, 0.5F));
//        brain.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(villagerprofession, 0.5F));
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
//
//        pBrain.addActivity(Activity.CORE, 0,
//                ImmutableList.of(
//                        new Swim(0.8F),
//                        new AnimalPanic(2.0F),
//                        new LookAtTargetSink(45, 90),
//                        new MoveToTargetSink(),
//                        new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
//                        new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS),
//                        new CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS)
//                ));
//
//        pBrain.addActivityWithConditions(Activity.IDLE,
//                ImmutableList.of(
//                        Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))),
//                        Pair.of(0, new AnimalMakeLove(EntityType.GOAT, 1.0F)),
//                        Pair.of(1, new FollowTemptation((p_149446_) -> {
//                            return 1.25F;
//                        })),
//                        Pair.of(2, new BabyFollowAdult<>(ADULT_FOLLOW_RANGE, 1.25F)),
//                        Pair.of(3, new RunOne<>(ImmutableList.of(
//                                Pair.of(new RandomStroll(1.0F), 2),
//                                Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 2),
//                                Pair.of(new DoNothing(30, 60), 1)
//                        )))
//                ),
//                ImmutableSet.of(
//                        Pair.of(MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_ABSENT),
//                        Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT)));
    }
 */
}
