package io.redspace.ironsspellbooks.spells.ender;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.comet.Comet;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class StarfallSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "starfall");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(16)
            .build();

    public StarfallSpell() {
        this.manaCostPerLevel = 1;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 1;
        this.castTime = 160;
        this.baseManaCost = 5;

    }

    @Override
    public CastType getCastType() {
        return CastType.CONTINUOUS;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.ENDER_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (!(playerMagicData.getAdditionalCastData() instanceof TargetAreaCastData)) {
            Vector3d targetArea = Utils.moveToRelativeGroundLevel(world, Utils.raycastForEntity(world, entity, 40, true).getLocation(), 12);
            playerMagicData.setAdditionalCastData(new TargetAreaCastData(targetArea, TargetedAreaEntity.createTargetAreaEntity(world, targetArea, getRadius(entity), 0x60008c)));
        }
        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    @Override
    public void onServerCastTick(World level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 4 == 0)
            if (playerMagicData.getAdditionalCastData() instanceof TargetAreaCastData) {
                TargetAreaCastData targetAreaCastData = (TargetAreaCastData) playerMagicData.getAdditionalCastData();
                for (int i = 0; i < 2; i++) {
                    Vector3d center = targetAreaCastData.getCenter();
                    float radius = getRadius(entity);
                    Vector3d spawn = center.add(new Vector3d(0, 0, entity.getRandom().nextFloat() * radius).yRot(entity.getRandom().nextInt(360)));
                    //TODO: not this
                    spawn = raiseWithCollision(spawn, 12, level);
                    shootComet(level, spellLevel, entity, spawn);
                    MagicManager.spawnParticles(level, ParticleHelper.COMET_FOG, spawn.x, spawn.y, spawn.z, 1, 1, 1, 1, 1, false);
                    MagicManager.spawnParticles(level, ParticleHelper.COMET_FOG, spawn.x, spawn.y, spawn.z, 1, 1, 1, 1, 1, true);
                }
            }
    }


    @Override
    protected void playSound(Optional<SoundEvent> sound, Entity entity, boolean playDefaultSound) {
        super.playSound(sound, entity, false);
    }

    private Vector3d raiseWithCollision(Vector3d start, int blocks, World level) {
        for (int i = 0; i < blocks; i++) {
            Vector3d raised = start.add(0, 1, 0);
            if (level.getBlockState(new BlockPos(raised)).isAir())
                start = raised;
            else
                break;
        }
        return start;
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) * .5f;
    }

    private float getRadius(LivingEntity caster) {
        return 6;
    }

    public void shootComet(World world, int spellLevel, LivingEntity entity, Vector3d spawn) {
        Comet fireball = new Comet(world, entity);
        fireball.setPos(spawn.add(-1, 0, 0));
        fireball.shoot(new Vector3d(.15f, -.85f, 0), .075f);
        fireball.setDamage(getDamage(spellLevel, entity));
        fireball.setExplosionRadius(2f);
        world.addFreshEntity(fireball);
        world.playSound(null, spawn.x, spawn.y, spawn.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 3.0f, 0.7f + Utils.random.nextFloat() * .3f);

    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
    }

}
