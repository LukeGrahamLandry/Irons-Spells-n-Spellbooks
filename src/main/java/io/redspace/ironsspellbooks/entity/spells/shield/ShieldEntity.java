package io.redspace.ironsspellbooks.entity.spells.shield;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;

public class ShieldEntity extends AbstractShieldEntity {
    protected ShieldPart[] subEntities;
    protected final Vector3d[] subPositions;
    protected final int LIFETIME;
    protected int width;
    protected int height;
    protected int age;

    public ShieldEntity(EntityType<?> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
        width = 5;
        height = 5;
        subEntities = new ShieldPart[width * height];
        subPositions = new Vector3d[width * height];
        this.setHealth(10);
        //this.xRot = (45);
        //this.yRot = (45);
        LIFETIME = 20 * 20;
        createShield();

    }

    public ShieldEntity(World level, float health) {
        this(EntityRegistry.SHIELD_ENTITY.get(), level);
        this.setHealth(health);
    }

    @Override
    protected void createShield() {
 //Ironsspellbooks.logger.debug("ShieldEntity.createShield");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = x * height + y;
                subEntities[i] = new ShieldPart(this, "part" + (i + 1), 0.5F, 0.5F, true);
                subPositions[i] = new Vector3d((x - width / 2f) * .5f + .25f, (y - height / 2f) * .5f, 0);//.xRot(getXRot()).yRot(getYRot());
            }
        }
    }

    public void setRotation(float x, float y) {
        this.xRot = (x);
        this.xRotO = x;
        this.yRot = (y);
        this.yRotO = y;
    }

    @Override
    public void takeDamage(DamageSource source, float amount, @Nullable Vector3d location) {
        if (!this.isInvulnerableTo(source)) {
            this.setHealth(this.getHealth() - amount);
            if (!level.isClientSide && location != null) {
                MagicManager.spawnParticles(level, ParticleTypes.ELECTRIC_SPARK, location.x, location.y, location.z, 30, .1, .1, .1, .5, false);
                level.playSound(null, location.x, location.y, location.z, SoundRegistry.FORCE_IMPACT.get(), SoundCategory.NEUTRAL, .8f, 1f);
            }
        }
    }

    @Override
    public void tick() {
        hurtThisTick = false;
        if (getHealth() <= 0) {
            destroy();
        } else if (++age > LIFETIME) {
            if (!this.level.isClientSide) {
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.NEUTRAL, 1, 1.4f);
            }
            this.remove();
        } else {
            for (int i = 0; i < subEntities.length; i++) {
                ShieldPart subEntity = subEntities[i];

                Vector3d pos = subPositions[i].xRot(Utils.DEG_TO_RAD * -this.xRot).yRot(Utils.DEG_TO_RAD * -this.yRot).add(this.position());
                subEntity.moveTo(pos);
                //subEntity.setDeltaMovement(newVector);
                //var vec3 = new Vec3(subEntity.getX(), subEntity.getY(), subEntity.getZ());
                subEntity.xo = pos.x;
                subEntity.yo = pos.y;
                subEntity.zo = pos.z;
                subEntity.xOld = pos.x;
                subEntity.yOld = pos.y;
                subEntity.zOld = pos.z;
            }
        }
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    protected void destroy() {
        if (!this.level.isClientSide) {
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundCategory.NEUTRAL, 2, .65f);
        }
        super.destroy();
    }
}
