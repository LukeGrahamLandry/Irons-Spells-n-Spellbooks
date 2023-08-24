package io.redspace.ironsspellbooks.spells.void_school;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.void_tentacle.VoidTentacle;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
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

public class VoidTentaclesSpell extends AbstractSpell {
    public VoidTentaclesSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.radius", Utils.stringTruncation(getRings(caster) * 1.3f, 1))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchool(SchoolType.VOID)
            .setMaxLevel(3)
            .setCooldownSeconds(30)
            .build();

    public VoidTentaclesSpell(int level) {
        super(SpellType.VOID_TENTACLES_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 2;
        this.castTime = 20;
        this.baseManaCost = 150;
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
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        int rings = getRings(entity);
        int count = 2;
        Vector3d center = Utils.getTargetBlock(level, entity, RayTraceContext.FluidMode.NONE, 48).getLocation();
        level.playSound(entity instanceof PlayerEntity player ? player : null, center.x, center.y, center.z, SoundRegistry.VOID_TENTACLES_FINISH.get(), SoundCategory.AMBIENT, 1, 1);

        for (int r = 0; r < rings; r++) {
            float tentacles = count + r * 2;
            for (int i = 0; i < tentacles; i++) {
                Vector3d random = new Vector3d(Utils.getRandomScaled(1), Utils.getRandomScaled(1), Utils.getRandomScaled(1));
                Vector3d spawn = center.add(new Vector3d(0, 0, 1.3 * (r + 1)).yRot(((6.281f / tentacles) * i))).add(random);

                spawn = new Vector3d(spawn.x, Utils.findRelativeGroundLevel(level, spawn, 8), spawn.z);
                if (!level.getBlockState(new BlockPos(spawn).below()).isAir()) {
                    VoidTentacle tentacle = new VoidTentacle(level, entity, getDamage(entity));
                    tentacle.moveTo(spawn);
                    tentacle.setYRot(level.getRandom().nextInt(360));
                    level.addFreshEntity(tentacle);
                }
            }
        }
        //In order to trigger sculk sensors
        level.gameEvent(null, GameEvent.ENTITY_ROAR, center);
        super.onCast(level, entity, playerMagicData);
    }

    private float getDamage(LivingEntity entity) {
        return getSpellPower(entity);
    }

    private int getRings(LivingEntity entity) {
        return 1 + getLevel(entity);
    }
}
