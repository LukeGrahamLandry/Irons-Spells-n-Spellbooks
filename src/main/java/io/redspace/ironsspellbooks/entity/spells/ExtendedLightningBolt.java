package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class ExtendedLightningBolt extends LightningBoltEntity {
    Entity owner;
    float damage;

    public ExtendedLightningBolt(World pLevel, Entity owner, float damage) {
        //None of us is serialized but that's okay we're just lightning
        super(EntityType.LIGHTNING_BOLT, pLevel);
        this.owner = owner;
        this.damage = damage;
    }

    @Override
    public float getDamage() {
        //So that thunderHit doesn't deal extra damage; we handle custom damage logic instead
        return 0;
    }

    public void tick() {
        super.tick();
        //Copied from base
        if (tickCount == 1) {
            if (!level.isClientSide) {
                List<Entity> list1 = this.level.getEntities(this, new AxisAlignedBB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), Entity::isAlive);

                for (Entity entity : list1) {
                    AbstractSpell spell = SpellRegistry.LIGHTNING_BOLT_SPELL.get();
                    DamageSources.applyDamage(entity, damage, spell.getDamageSource(owner), spell.getSchoolType());
                }
            }
        }
    }
}
