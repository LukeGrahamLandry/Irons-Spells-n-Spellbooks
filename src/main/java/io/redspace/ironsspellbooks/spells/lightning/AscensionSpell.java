package io.redspace.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.capabilities.magic.CastData;
import io.redspace.ironsspellbooks.capabilities.magic.CastDataSerializable;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;


public class AscensionSpell extends AbstractSpell {
    public AscensionSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(ITextComponent.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(caster), 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchool(SchoolType.LIGHTNING)
            .setMaxLevel(10)
            .setCooldownSeconds(15)
            .build();

    public AscensionSpell(int level) {
        super(SpellType.ASCENSION_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 1;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 50;

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public CastDataSerializable getEmptyCastData() {
        return new ImpulseCastData();
    }

    @Override
    public void onClientCast(World level, LivingEntity entity, CastData castData) {
        if (castData instanceof ImpulseCastData data) {
            entity.hasImpulse = data.hasImpulse;
            double y = Math.max(entity.getDeltaMovement().y, data.y);
            entity.setDeltaMovement(data.x, y, data.z);
        }
        super.onClientCast(level, entity, castData);
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {

        entity.addEffect(new EffectInstance(MobEffectRegistry.ASCENSION.get(), 80, 0, false, false, true));

        Vector3d vec = entity.position();
        for (int i = 0; i < 32; i++) {
            if (!level.getBlockState(new BlockPos(vec).below()).isAir())
                break;
            vec = vec.subtract(0, 1, 0);
        }
        Vector3d strikePos = vec;

        LightningBoltEntity lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
        lightningBolt.setVisualOnly(true);
        lightningBolt.setDamage(0);
        lightningBolt.setPos(strikePos);
        level.addFreshEntity(lightningBolt);

        //livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100));
        float radius = 5;
        level.getEntities(entity, entity.getBoundingBox().inflate(radius)).forEach(target -> {
            double distance = target.distanceToSqr(strikePos);
            if (distance < radius * radius) {
                float finalDamage = (float) (getDamage(entity) * (1 - distance / (radius * radius)));
                DamageSources.applyDamage(target, finalDamage, SpellType.ASCENSION_SPELL.getDamageSource(lightningBolt, entity), SchoolType.LIGHTNING);
                if (target instanceof CreeperEntity creeper)
                    creeper.thunderHit((ServerWorld) level, lightningBolt);
            }
        });

        Vector3d motion = entity.getLookAngle().multiply(1, 0, 1).normalize().add(0, 5, 0).scale(.125);
        playerMagicData.setAdditionalCastData(new ImpulseCastData((float) motion.x, (float) motion.y, (float) motion.z, true));
        entity.setDeltaMovement(entity.getDeltaMovement().add(motion));
        entity.hasImpulse = true;


        super.onCast(level, entity, playerMagicData);
    }

    private int getDamage(LivingEntity caster) {
        return (int) getSpellPower(caster);
    }
}
