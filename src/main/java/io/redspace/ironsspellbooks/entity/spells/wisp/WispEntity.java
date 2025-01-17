package io.redspace.ironsspellbooks.entity.spells.wisp;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.goals.WispAttackGoal;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.spells.holy.WispSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.HandSide;

public class WispEntity extends CreatureEntity implements IAnimatable {

    @Nullable
    private UUID ownerUUID;

    @Nullable
    private Entity cachedOwner;

    @SuppressWarnings("removal")
    private final AnimationFactory factory = new AnimationFactory(this);

    @SuppressWarnings("removal")
    private final AnimationBuilder animationBuilder = new AnimationBuilder().addAnimation("animation.wisp.flying", true);

    private Vector3d targetSearchStart;
    private Vector3d lastTickPos;
    private float damageAmount;

    public WispEntity(EntityType<? extends WispEntity> entityType, World level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public WispEntity(World levelIn, LivingEntity owner, float damageAmount) {
        this(EntityRegistry.WISP.get(), levelIn);
        this.moveControl = new FlyingMovementController(this, 20, true);
        //this.targetSearchStart = targetSearchStart;
        this.damageAmount = damageAmount;

        setOwner(owner);

        float xRot = owner.xRot;
        float yRot = owner.yRot;
        float yHeadRot = owner.getYHeadRot();

        this.yRot = (yRot);
        this.xRot = (xRot);
        this.setYBodyRot(yRot);
        this.setYHeadRot(yHeadRot);
        this.lastTickPos = this.position();

 //Ironsspellbooks.logger.debug("WispEntity: Owner - xRot:{}, yRot:{}, yHeadRot:{}", xRot, yRot, yHeadRot);
 //Ironsspellbooks.logger.debug("WispEntity: Wisp - xRot:{}, yRot:{}, look:{}", this.xRot, this.yRot, this.getLookAngle());
    }

    @Override
    protected void registerGoals() {
        //IronsSpellbooks.LOGGER.debug("WispEntity.registerGoals");
        this.goalSelector.addGoal(2, new WispAttackGoal(this, 1));
//        this.targetSelector.addGoal(1, new AcquireTargetNearLocationGoal<>(
//                this,
//                LivingEntity.class,
//                0,
//                false,
//                true,
//                targetSearchStart,
//                WispEntity::isValidTarget));
    }

    public static boolean isValidTarget(@Nullable Entity entity) {
        if (entity instanceof LivingEntity &&
                ((LivingEntity) entity).isAlive() &&
                (LivingEntity) entity instanceof IMob) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return true;
        }
        return false;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public LivingEntity getTarget() {
        return super.getTarget();
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            spawnParticles();
        } else {
            LivingEntity target = this.getTarget();
            if (target != null) {
                if (this.getBoundingBox().intersects(target.getBoundingBox())) {
                   // IronsSpellbooks.LOGGER.debug("WispEntity.tick applyDamage: {}", damageAmount);
                    DamageSources.applyDamage(target, damageAmount, SpellRegistry.WISP_SPELL.get().getDamageSource(this,cachedOwner), SpellRegistry.WISP_SPELL.get().getSchoolType());
                    this.playSound(WispSpell.getImpactSound(), 1.0f, 1.0f);
                    Vector3d p = target.getEyePosition(0);
                    MagicManager.spawnParticles(level, ParticleHelper.WISP, p.x, p.y, p.z, 25, 0, 0, 0, .18, true);
                    this.remove();
                }
            }
        }
        lastTickPos = this.position();
    }

    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Override
    protected @NotNull PathNavigator createNavigation(World pLevel) {
        FlyingPathNavigator flyingpathnavigation = new FlyingPathNavigator(this, pLevel) {
            public boolean isStableDestination(BlockPos blockPos) {
                return !this.level.getBlockState(blockPos.below()).isAir();
            }

            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    protected float getStandingEyeHeight(Pose pPose, EntitySize pDimensions) {
        return pDimensions.height * 0.6F;
    }

    @Override
    public void travel(Vector3d pTravelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.91F));
            }
        }

        this.calculateEntityAnimation(this, false);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public void setTarget(@org.jetbrains.annotations.Nullable LivingEntity target) {
        super.setTarget(target);

        //irons_spellbooks.LOGGER.debug("WispEntity.setTarget: {}", target);
    }

    @Override
    protected void customServerAiStep() {
        if (this.cachedOwner == null || !this.cachedOwner.isAlive()) {
            this.remove();
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(animationBuilder);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.FLYING_SPEED, .2)
                .add(Attributes.MOVEMENT_SPEED, .2);

    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.singleton(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType pSlot, ItemStack pStack) {

    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.level.isClientSide) {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((ServerWorld)this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.remove();
        }

        return true;
    }

    @Override
    public HandSide getMainArm() {
        return HandSide.LEFT;
    }

    //https://forge.gemwire.uk/wiki/Particles
    public void spawnParticles() {
//        for (int i = 0; i < 1; i++) {
//            double speed = .02;
//            double dx = Utils.random.nextDouble() * 2 * speed - speed;
//            double dy = Utils.random.nextDouble() * 2 * speed - speed;
//            double dz = Utils.random.nextDouble() * 2 * speed - speed;
//            var tmp = ParticleHelper.UNSTABLE_ENDER;
//            //IronsSpellbooks.LOGGER.debug("WispEntity.spawnParticles isClientSide:{}, position:{}, {} {} {}", this.level.isClientSide, this.position(), dx, dy, dz);
//            level.addParticle(ParticleHelper.WISP, this.xOld - dx, this.position().y, this.zOld - dz, dx, dy, dz);
//            //level.addParticle(ParticleHelper.UNSTABLE_ENDER, this.getX() + dx / 2, this.getY() + dy / 2, this.getZ() + dz / 2, dx, dy, dz);
//        }
    }
}
