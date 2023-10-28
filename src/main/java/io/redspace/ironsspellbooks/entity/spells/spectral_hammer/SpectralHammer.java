package io.redspace.ironsspellbooks.entity.spells.spectral_hammer;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.HandSide;

public class SpectralHammer extends LivingEntity implements IAnimatable {

    private final int ticksToLive = 30;
    private final int doDamageTick = 13;
    private final int doAnimateTick = 20;

    private int depth = 0;
    private int radius = 0;

    private boolean didDamage = false;
    private boolean didAnimate = false;
    private int ticksAlive = 0;
    private boolean playSwingAnimation = true;
    private BlockRayTraceResult blockHitResult;
    private float damageAmount;
    Set<BlockPos> missedBlocks = new HashSet<>();

    public SpectralHammer(EntityType<? extends SpectralHammer> entityType, World level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public SpectralHammer(World levelIn, LivingEntity owner, BlockRayTraceResult blockHitResult, int depth, int radius) {
        this(EntityRegistry.SPECTRAL_HAMMER.get(), levelIn);

        this.blockHitResult = blockHitResult;
        this.depth = depth;
        this.radius = radius;

        var xRot = blockHitResult.getDirection().getAxis().isVertical() ? 90 : 0;
        var yRot = owner.yRot;
        var yHeadRot = owner.getYHeadRot();

        this.setYRot(yRot);
        this.setXRot(xRot);
        this.setYBodyRot(yRot);
        this.setYHeadRot(yHeadRot);

//        IronsSpellbooks.LOGGER.debug("SpectralHammer: owner - xRot:{}, yRot:{}, yHeadRot:{}", xRot, yRot, yHeadRot);
//        IronsSpellbooks.LOGGER.debug("SpectralHammer: this - xRot:{}, yRot:{}, look:{}", this.xRot, this.yRot, this.getLookAngle());
//        IronsSpellbooks.LOGGER.debug("SpectralHammer: blockHitResult.dir:{}, damageAmount:{}", blockHitResult.getDirection(), damageAmount);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public void tick() {
        if (++ticksAlive >= ticksToLive) {
            discard();
        }

        if (ticksAlive >= doAnimateTick && !didAnimate) {
            missedBlocks.forEach(pos -> {
                FallingBlockEntity.fall(level, pos, level.getBlockState(pos));
            });
            didAnimate = true;
        }

        if (ticksAlive == doDamageTick - 2 && !didDamage) {
            var location = this.position();
            level.playSound(null, location.x, location.y, location.z, SoundRegistry.FORCE_IMPACT.get(), SoundCategory.NEUTRAL, 2f, random.nextIntBetweenInclusive(6, 8) * .1f);
            level.playSound(null, location.x, location.y, location.z, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.NEUTRAL, 1f, random.nextIntBetweenInclusive(6, 8) * .1f);

        }

        if (ticksAlive >= doDamageTick && !didDamage) {
            if (blockHitResult != null && blockHitResult.getType() != RayTraceResult.Type.MISS) {
                var blockPos = blockHitResult.getBlockPos();
                var blockState = level.getBlockState(blockPos);

                if (blockState.is(ModTags.SPECTRAL_HAMMER_MINEABLE)) {
                    var blockCollector = getBlockCollector(blockPos, blockHitResult.getDirection(), radius, depth, new HashSet<>(), new HashSet<>());
                    collectBlocks(blockPos, blockCollector);

                    if (!blockCollector.blocksToRemove.isEmpty()) {
                        //IronsSpellbooks.LOGGER.debug("SpectralHammer.tick: origin:{}", blockCollector.origin);
                        var random = Utils.random;
                        AtomicInteger count = new AtomicInteger();
                        int maxPossibleStacks = (this.radius * 2) * (1 + this.radius * 2) * (this.depth + 1);
                        Inventory drops = new Inventory(maxPossibleStacks);
                        blockCollector.blocksToRemove.forEach(pos -> {
                            var distance = blockCollector.origin.distManhattan(pos);
                            var missChance = random.nextFloat() * 40;
                            float pct = (distance * distance) / (100.0f * this.radius);

                            if (missChance < pct) {
                                //IronsSpellbooks.LOGGER.debug("SpectralHammer.tick: missed pos:{}, dist:{}, missChance:{}, pct:{}", pos, distance, missChance, pct);
                                missedBlocks.add(pos);
                            } else {
                                var blockstate = level.getBlockState(pos);

                                if (count.incrementAndGet() % 5 == 0) {
                                    level.destroyBlock(pos, false);
                                }else{
                                    level.removeBlock(pos, false);
                                }
                                //IronsSpellbooks.LOGGER.debug("SpectralHammer.tick: remove.2 pos:{}, dist:{}, missChance:{}, pct:{}", pos, distance, missChance, pct);
                                dropResources(blockstate, level, pos).forEach(drops::addItem);
                            }
                        });
                        InventoryHelper.dropContents(level, this.blockPosition(), drops);
                    }
                }
            }

            didDamage = true;
        }

        super.tick();
    }

    public static List<ItemStack> dropResources(BlockState pState, World pLevel, BlockPos pos) {
        List<ItemStack> drops = new ArrayList<>();
        if (pLevel instanceof ServerWorld) {
            drops = Block.getDrops(pState, (ServerWorld) pLevel, pos, null);
            pState.spawnAfterBreak((ServerWorld) pLevel, pos, ItemStack.EMPTY, true);
        }
        return drops;
    }

    private void collectBlocks(BlockPos blockPos, BlockCollectorHelper bch) {
        //IronsSpellbooks.LOGGER.debug("SpectralHammer.collectBlocks: blockPos:{} checked:{} toRemove:{}", blockPos, bch.blocksChecked.size(), bch.blocksToRemove.size());

        if (bch.blocksChecked.contains(blockPos) || bch.blocksToRemove.contains(blockPos)) {
            return;
        }

        if (bch.isValidBlockToCollect(level, blockPos)) {
            //IronsSpellbooks.LOGGER.debug("SpectralHammer.collectBlocks: blockPos{} is valid", blockPos);
            bch.blocksToRemove.add(blockPos);
            collectBlocks(blockPos.above(), bch);
            collectBlocks(blockPos.below(), bch);
            collectBlocks(blockPos.north(), bch);
            collectBlocks(blockPos.south(), bch);
            collectBlocks(blockPos.east(), bch);
            collectBlocks(blockPos.west(), bch);
        } else {
            //IronsSpellbooks.LOGGER.debug("SpectralHammer.collectBlocks: blockPos{} is not valid", blockPos);
            bch.blocksChecked.add(blockPos);
        }
    }

    private BlockCollectorHelper getBlockCollector(BlockPos origin, Direction direction, int radius, int depth, Set<BlockPos> blocksToRemove, Set<BlockPos> blocksChecked) {
        int minX = origin.getX() - radius;
        int maxX = origin.getX() + radius;
        int minY = origin.getY() - radius;
        int maxY = origin.getY() + radius;
        int minZ = origin.getZ() - radius;
        int maxZ = origin.getZ() + radius;

        switch (direction) {
            case WEST -> {
                minX = origin.getX();
                maxX = origin.getX() + depth;
            }
            case EAST -> {
                minX = origin.getX() - depth;
                maxX = origin.getX();
            }
            case SOUTH -> {
                minZ = origin.getZ() - depth;
                maxZ = origin.getZ();
            }
            case NORTH -> {
                minZ = origin.getZ();
                maxZ = origin.getZ() + depth;
            }
            case UP -> {
                minY = origin.getY() - depth;
                maxY = origin.getY();
            }
            case DOWN -> {
                minY = origin.getY();
                maxY = origin.getY() + depth;
            }
        }

        return new BlockCollectorHelper(origin, direction, radius, depth, minX, maxX, minY, maxY, minZ, maxZ, blocksToRemove, blocksChecked);
    }

    private record BlockCollectorHelper(
            BlockPos origin,
            Direction originVector,
            int radius,
            int depth,
            int minX,
            int maxX,
            int minY,
            int maxY,
            int minZ,
            int maxZ,
            Set<BlockPos> blocksToRemove,
            Set<BlockPos> blocksChecked) {

        public boolean isValidBlockToCollect(World level, BlockPos bp) {
            return level.getBlockState(bp).is(ModTags.SPECTRAL_HAMMER_MINEABLE)
                    && bp.getX() >= minX
                    && bp.getX() <= maxX
                    && bp.getY() >= minY
                    && bp.getY() <= maxY
                    && bp.getZ() >= minZ
                    && bp.getZ() <= maxZ;
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    protected float getStandingEyeHeight(Pose pPose, EntitySize pDimensions) {
        return pDimensions.height * 0.6F;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return LivingEntity.createLivingAttributes();
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

    @Override
    public HandSide getMainArm() {
        return HandSide.LEFT;
    }

    @SuppressWarnings("removal")
    private final AnimationFactory factory = new AnimationFactory(this);

    @SuppressWarnings("removal")
    private final AnimationBuilder animationBuilder = new AnimationBuilder().addAnimation("hammer_swing", false);
    private final AnimationController animationController = new AnimationController(this, "controller", 0, this::predicate);

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(animationController);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (event.getController().getAnimationState() == AnimationState.Stopped) {
            if (playSwingAnimation) {
                event.getController().setAnimation(animationBuilder);
                playSwingAnimation = false;
            }
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
