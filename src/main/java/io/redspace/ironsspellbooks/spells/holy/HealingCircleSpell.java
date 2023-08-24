package io.redspace.ironsspellbooks.spells.holy;


import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.HealingAoe;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class HealingCircleSpell extends AbstractSpell {
    public HealingCircleSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                ITextComponent.translatable("ui.irons_spellbooks.aoe_healing", Utils.stringTruncation(getHealing(caster), 2)),
                ITextComponent.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(caster), 1)),
                ITextComponent.translatable("ui.irons_spellbooks.duration", Utils.timeFromTicks(getDuration(caster), 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.HOLY)
            .setMaxLevel(10)
            .setCooldownSeconds(25)
            .build();

    public HealingCircleSpell(int level) {
        super(SpellType.HEALING_CIRCLE_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 2;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 40;

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.CLOUD_OF_REGEN_LOOP.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public boolean checkPreCastConditions(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, getSpellType(), 32, .15f, false);
        return true;
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        Vector3d spawn = null;
        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData castTargetingData) {
            var target = castTargetingData.getTarget((ServerWorld) world);
            if (target != null)
                spawn = target.position();
        }
        if (spawn == null)
            spawn = Utils.raycastForEntity(world, entity, 32, true, .15f).getLocation();

        int duration = getDuration(entity);
        float radius = getRadius(entity);

        TargetedAreaEntity visualEntity = TargetedAreaEntity.createTargetAreaEntity(world, spawn, radius, 0xc80000);
        visualEntity.setDuration(duration);

        HealingAoe aoeEntity = new HealingAoe(world);
        aoeEntity.setOwner(entity);
        aoeEntity.setCircular();
        aoeEntity.setRadius(radius);
        aoeEntity.setDuration(duration);
        aoeEntity.setDamage(getHealing(entity));
        aoeEntity.setPos(spawn);
        world.addFreshEntity(aoeEntity);

        super.onCast(world, entity, playerMagicData);
    }

    private float getHealing(LivingEntity caster) {
        return getSpellPower(caster) * .25f;
    }

    private float getRadius(LivingEntity caster) {
        return 4;
    }

    private int getDuration(LivingEntity caster) {
        return 200;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return ANIMATION_CONTINUOUS_OVERHEAD;
    }

}
