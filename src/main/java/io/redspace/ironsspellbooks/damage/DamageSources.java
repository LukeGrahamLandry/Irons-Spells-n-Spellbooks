package io.redspace.ironsspellbooks.damage;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;

//https://github.com/cleannrooster/Spellblade-1.19.2/search?q=MobEffect
//https://github.com/LittleEzra/Augment-1.19.2/blob/334dc95462a3e6b25e6f73d3d909d012d63be109/src/main/java/com/littleezra/augment/item/enchantment/RecoilCurseEnchantment.java
//DamageSource
//StatusEffect
//MobEffect: https://forge.gemwire.uk/wiki/Mob_Effects/1.18

@Mod.EventBusSubscriber
public class DamageSources {


    //    public static EntityDamageSource bloodSlash(Player player) {
//        return new EntityDamageSource(BLOOD_MAGIC_ID, player);
//    }
    public static DamageSource CAULDRON = new DamageSource("blood_cauldron");
    public static DamageSource HEARTSTOP = new DamageSource("heartstop").bypassArmor().bypassMagic();

    /**
     * Use new overload {@link DamageSources#applyDamage(Entity, float, DamageSource)}<br>You can also now utilize the damage source itself to apply lifesteal, fire time, and freeze time <br>
     */
    @Deprecated(forRemoval = true)
    public static boolean applyDamage(Entity target, float baseAmount, DamageSource damageSource, @Nullable SchoolType damageSchool) {
        if (target instanceof LivingEntity) {
            LivingEntity livingTarget = (LivingEntity) target;
            float adjustedDamage = baseAmount * getResist(livingTarget, damageSchool);
            MagicSummon fromSummon = damageSource.getDirectEntity() instanceof MagicSummon ? (MagicSummon) damageSource.getDirectEntity() : damageSource.getEntity() instanceof MagicSummon ? (MagicSummon) damageSource.getEntity() : null;
            if (fromSummon != null) {
                if (fromSummon.getSummoner() != null) {
                    adjustedDamage *= (float) fromSummon.getSummoner().getAttributeValue(AttributeRegistry.SUMMON_DAMAGE.get());
                }
            } else if (damageSource.getDirectEntity() instanceof NoKnockbackProjectile) {
                ignoreNextKnockback(livingTarget);
            }
            if (damageSource.getEntity() instanceof LivingEntity) {
                LivingEntity livingAttacker = (LivingEntity) damageSource.getEntity();
                if (isFriendlyFireBetween(livingAttacker, livingTarget)) {
                    return false;
                }
                livingAttacker.setLastHurtMob(target);
            }
            boolean flag = livingTarget.hurt(damageSource, adjustedDamage);
            if (fromSummon instanceof LivingEntity) {
                LivingEntity livingSummon = (LivingEntity) fromSummon;
                livingTarget.setLastHurtByMob(livingSummon);
            }
            return flag;
        } else {
            return target.hurt(damageSource, baseAmount);
        }
    }

    public static boolean applyDamage(Entity target, float baseAmount, DamageSource damageSource) {
        if (target instanceof LivingEntity && damageSource instanceof ISpellDamageSource) {
            LivingEntity livingTarget = (LivingEntity) target;
            ISpellDamageSource spellDamageSource = (ISpellDamageSource) damageSource;
            SpellDamageEvent e = new SpellDamageEvent(livingTarget, baseAmount, spellDamageSource);
            if (MinecraftForge.EVENT_BUS.post(e)) {
                return false;
            }
            baseAmount = e.getAmount();
            float adjustedDamage = baseAmount * getResist(livingTarget, spellDamageSource.schoolType());
            MagicSummon fromSummon = damageSource.getDirectEntity() instanceof MagicSummon ? (MagicSummon) damageSource.getDirectEntity() : damageSource.getEntity() instanceof MagicSummon ? (MagicSummon) damageSource.getEntity() : null;
            if (fromSummon != null) {
                if (fromSummon.getSummoner() != null) {
                    adjustedDamage *= (float) fromSummon.getSummoner().getAttributeValue(AttributeRegistry.SUMMON_DAMAGE.get());
                }
            } else if (damageSource.getDirectEntity() instanceof NoKnockbackProjectile) {
                ignoreNextKnockback(livingTarget);
            }
            if (damageSource.getEntity() instanceof LivingEntity) {
                LivingEntity livingAttacker = (LivingEntity) damageSource.getEntity();
                if (isFriendlyFireBetween(livingAttacker, livingTarget)) {
                    return false;
                }
                livingAttacker.setLastHurtMob(target);
            }
            boolean flag = livingTarget.hurt(damageSource, adjustedDamage);
            if (fromSummon instanceof LivingEntity) {
                LivingEntity livingSummon = (LivingEntity) fromSummon;
                livingTarget.setLastHurtByMob(livingSummon);
            }
            return flag;
        } else {
            return target.hurt(damageSource, baseAmount);
        }
    }

    //I can't tell if this is genius or incredibly stupid
    private static final HashMap<LivingEntity, Integer> knockbackImmunes = new HashMap<>();

    public static void ignoreNextKnockback(LivingEntity livingEntity) {
        if (!livingEntity.level.isClientSide)
            knockbackImmunes.put(livingEntity, livingEntity.tickCount);
    }

    @SubscribeEvent
    public static void cancelKnockback(LivingKnockBackEvent event) {
        //IronsSpellbooks.LOGGER.debug("DamageSources.cancelKnockback {}", event.getEntity().getName().getString());
        if (knockbackImmunes.containsKey(event.getEntity())) {
            Entity entity = event.getEntity();
            if (entity.tickCount - knockbackImmunes.get(entity) <= 1) {
                event.setCanceled(true);
            }
            knockbackImmunes.remove(entity);
        }
    }

    @SubscribeEvent
    public static void postHitEffects(LivingDamageEvent event) {
        if (event.getSource() instanceof ISpellDamageSource && ((ISpellDamageSource) event.getSource()).hasPostHitEffects()) {
            ISpellDamageSource spellDamageSource = (ISpellDamageSource) event.getSource();
            float actualDamage = event.getAmount();
            Entity target = event.getEntity();
            Entity attacker = event.getSource().getEntity();
            if (attacker instanceof LivingEntity) {
                LivingEntity livingAttacker = (LivingEntity) attacker;
                if (spellDamageSource.getLifestealPercent() > 0) {
                    livingAttacker.heal(spellDamageSource.getLifestealPercent() * actualDamage);
                }
            }
            if (spellDamageSource.getFreezeTicks() > 0 && target.canFreeze()) {
                //Freeze ticks count down by 2, so we * 2 so the spell damages source can be dumb
                target.setTicksFrozen(target.getTicksFrozen() + spellDamageSource.getFreezeTicks() * 2);
            }
            if (spellDamageSource.getFireTime() > 0) {
                target.setSecondsOnFire(spellDamageSource.getFireTime());
            }
        }

    }

    public static boolean isFriendlyFireBetween(Entity attacker, Entity target) {
        if (attacker == null || target == null)
            return false;
        Team team = attacker.getTeam();
        if (team != null) {
            return team.isAlliedTo(target.getTeam()) && !team.isAllowFriendlyFire();
        }
        return false;
    }

    public static DamageSource directDamageSource(DamageSource source, Entity attacker) {
        return new EntityDamageSource(source.getMsgId(), attacker);
    }

    public static DamageSource indirectDamageSource(DamageSource source, Entity projectile, @Nullable Entity attacker) {
        return new IndirectEntityDamageSource(source.msgId, projectile, attacker);
    }

    /**
     * Returns the resistance multiplier of the entity. (If they are resistant, the value is < 1)
     */
    public static float getResist(LivingEntity entity, SchoolType damageSchool) {
        if (damageSchool == null)
            return 1;
        else
            return 2 - (float) Utils.softCapFormula(damageSchool.getResistanceFor(entity));
    }
}
