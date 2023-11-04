package io.redspace.ironsspellbooks.spells.void_school;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.spells.void_tentacle.VoidTentacle;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class VoidTentaclesSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "void_tentacles");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.radius", Utils.stringTruncation(getRings(spellLevel, caster) * 1.3f, 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(30)
            .build();

    public VoidTentaclesSpell() {
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 2;
        this.castTime = 20;
        this.baseManaCost = 150;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
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
        return Optional.of(SoundRegistry.VOID_TENTACLES_START.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.VOID_TENTACLES_FINISH.get());
    }

    @Override
    public void onCast(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        int rings = getRings(spellLevel, entity);
        int count = 2;
        Vector3d center = Utils.getTargetBlock(level, entity, RayTraceContext.FluidMode.NONE, 48).getLocation();
        level.playSound(entity instanceof PlayerEntity ? (PlayerEntity) entity : null, center.x, center.y, center.z, SoundRegistry.VOID_TENTACLES_FINISH.get(), SoundCategory.AMBIENT, 1, 1);

        for (int r = 0; r < rings; r++) {
            float tentacles = count + r * 2;
            for (int i = 0; i < tentacles; i++) {
                Vector3d random = new Vector3d(Utils.getRandomScaled(1), Utils.getRandomScaled(1), Utils.getRandomScaled(1));
                Vector3d spawn = center.add(new Vector3d(0, 0, 1.3 * (r + 1)).yRot(((6.281f / tentacles) * i))).add(random);

                spawn = Utils.moveToRelativeGroundLevel(level, spawn, 8);
                if (!level.getBlockState(new BlockPos(spawn).below()).isAir()) {
                    VoidTentacle tentacle = new VoidTentacle(level, entity, getDamage(spellLevel, entity));
                    tentacle.moveTo(spawn);
                    tentacle.setYRot(Utils.random.nextInt(360));
                    level.addFreshEntity(tentacle);
                }
            }
        }
        //In order to trigger sculk sensors
        level.gameEvent(null, GameEvent.ENTITY_ROAR, center);
        super.onCast(level, spellLevel, entity, playerMagicData);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity);
    }

    private int getRings(int spellLevel, LivingEntity entity) {
        return 1 + getLevel(spellLevel, entity);
    }
}
