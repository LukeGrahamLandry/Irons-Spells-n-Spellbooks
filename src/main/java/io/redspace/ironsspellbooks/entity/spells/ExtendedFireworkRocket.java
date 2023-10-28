package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class ExtendedFireworkRocket extends FireworkRocketEntity implements AntiMagicSusceptible {
    protected static final DataParameter<ItemStack> DATA_ID_FIREWORKS_ITEM = EntityDataManager.defineId(ExtendedFireworkRocket.class, DataSerializers.ITEM_STACK);

    public ExtendedFireworkRocket(World pLevel, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, boolean pShotAtAngle, float damage) {
        super(pLevel, pStack, pShooter, pX, pY, pZ, pShotAtAngle);
        this.damage = damage;
    }

    private final float damage;

    public float getDamage() {
        return damage;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            this.explode();
        }
    }

    private void explode() {
        //Copied from private FireworkRocketEntity explode
        this.level.broadcastEntityEvent(this, (byte) 17);
        this.gameEvent(GameEvent.EXPLODE, this.getOwner());
        this.dealExplosionDamage();
        this.discard();
    }

    private void dealExplosionDamage() {
        //Copied from private FireworkRocketEntity dealExplosionDamage
        Vector3d pos = this.position();
        boolean los = false;
        double explosionRadius = 2;
        for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(explosionRadius))) {
            if (this.distanceToSqr(livingentity) <= explosionRadius * explosionRadius && canHitEntity(livingentity)) {

                for (int i = 0; i < 2; ++i) {
                    Vector3d targetPos = new Vector3d(livingentity.getX(), livingentity.getY(0.5D * (double) i), livingentity.getZ());
                    RayTraceResult hitresult = this.level.clip(new RayTraceContext(pos, targetPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                    if (hitresult.getType() == RayTraceResult.Type.MISS) {
                        los = true;
                        break;
                    }
                }

                if (los) {
                    AbstractSpell spell = SpellRegistry.FIRECRACKER_SPELL.get();
                    DamageSources.applyDamage(livingentity, this.getDamage(), spell.getDamageSource(this, getOwner()), spell.getSchoolType());
                }
            }
        }

    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }
}
