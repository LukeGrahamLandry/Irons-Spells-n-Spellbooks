package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class DeadKingCorpseEntity extends AbstractSpellCastingMob {
    private final static DataParameter<Boolean> TRIGGERED = EntityDataManager.defineId(DeadKingCorpseEntity.class, DataSerializers.BOOLEAN);
    private int currentAnimTime;
    private final int animLength = 20 * 15;

    public DeadKingCorpseEntity(EntityType<? extends CreatureEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (triggered()) {
            ++currentAnimTime;
            if (!level.isClientSide) {
                if (currentAnimTime > animLength) {
                    DeadKingBoss boss = new DeadKingBoss(level);
                    boss.moveTo(this.position().add(0, 1, 0));
                    boss.yRot = (this.yRot);
                    boss.finalizeSpawn((ServerWorld) level, level.getCurrentDifficultyAt(boss.blockPosition()), SpawnReason.TRIGGERED, null, null);
                    int playerCount = Math.max(level.getEntitiesOfClass(PlayerEntity.class, boss.getBoundingBox().inflate(32)).size(), 1);
                    boss.getAttributes().getInstance(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Gank Health Bonus", (playerCount - 1) * .5, AttributeModifier.Operation.MULTIPLY_BASE));
                    boss.setHealth(boss.getMaxHealth());
                    boss.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier("Gank Damage Bonus", (playerCount - 1) * .25, AttributeModifier.Operation.MULTIPLY_BASE));
                    boss.getAttributes().getInstance(AttributeRegistry.SPELL_RESIST.get()).addPermanentModifier(new AttributeModifier("Gank Spell Resist Bonus", (playerCount - 1) * .1, AttributeModifier.Operation.MULTIPLY_BASE));
                    boss.setPersistenceRequired();
                    level.addFreshEntity(boss);
                    // TODO: pick particle instead of SCULK
                    MagicManager.spawnParticles(level, ParticleTypes.FLAME, position().x, position().y + 2.5, position().z, 80, .2, .2, .2, .25, true);
                    level.playSound(null, getX(), getY(), getZ(), SoundRegistry.DEAD_KING_SPAWN.get(), SoundCategory.MASTER, 20, 1);
                    this.remove();
                }
            } else {
                resurrectParticles();
            }
        }
    }

    private void resurrectParticles() {
        float f = (currentAnimTime / (float) animLength);
        float rot = currentAnimTime * 12 + (1 + f * 15);
        float height = f * 4 + (.4f * MathHelper.sin(currentAnimTime * 30 * Utils.DEG_TO_RAD) * f * f);
        float distance = MathHelper.clamp(Utils.smoothstep(0, 1.15f, f * 3), 0, 1.15f);
        Vector3d pos = new Vector3d(0, 0, distance).yRot(rot * Utils.DEG_TO_RAD).add(0, height, 0).add(position());

        // TODO: pick particle instead of SCULK
        level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0, 0);
        float radius = 4;
        if (random.nextFloat() < f * 1.5f) {
            Vector3d random = position().add(new Vector3d(
                    (this.random.nextFloat() * 2 - 1) * radius,
                    3.5 + (this.random.nextFloat() * 2 - 1) * radius,
                    (this.random.nextFloat() * 2 - 1) * radius
            ));
            Vector3d motion = position().subtract(random).scale(.04f);
            // TODO: pick particle instead of SCULK
            level.addParticle(ParticleTypes.FLAME, random.x, random.y, random.z, motion.x, motion.y, motion.z);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.isBypassInvul()) {
            kill();
            return true;
        } else {
            PlayerEntity player = level.getNearestPlayer(this, 8);
            if (player != null) {
                trigger();
            }
            return false;
        }
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity pPlayer, Hand pHand) {
        if (!triggered()) {
            trigger();
            return ActionResultType.sidedSuccess(level.isClientSide);
        }
        return super.mobInteract(pPlayer, pHand);
    }

    private void trigger() {
        if (!triggered()) {
            level.playSound(null, getX(), getY(), getZ(), SoundRegistry.DEAD_KING_RESURRECT.get(), SoundCategory.AMBIENT, 2, 1);
            this.entityData.set(TRIGGERED, true);
        }
    }

    private boolean triggered() {
        return this.entityData.get(TRIGGERED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRIGGERED, false);
    }

    /**
     * GeckoLib
     **/

    private final AnimationBuilder idle = new AnimationBuilder().addAnimation("dead_king_rest", ILoopType.EDefaultLoopTypes.LOOP);
    private final AnimationBuilder rise = new AnimationBuilder().addAnimation("dead_king_rise", ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "idle", 0, this::idlePredicate));
    }

    private PlayState idlePredicate(AnimationEvent event) {
        if (triggered()) {
            event.getController().setAnimation(rise);
        } else {
            event.getController().setAnimation(idle);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public boolean shouldBeExtraAnimated() {
        return false;
    }

    @Override
    public boolean shouldAlwaysAnimateHead() {
        return false;
    }
}
