package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.spectral_hammer.SpectralHammer;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class SpectralHammerSpell extends AbstractSpell {

    public SpectralHammerSpell() {
        this(1);
    }

    private static final int distance = 12;

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.dimensions", 1 + getRadius(caster) * 2, 1 + getRadius(caster) * 2, getDepth(caster) + 1),
                new TranslationTextComponent("ui.irons_spellbooks.distance", distance)
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.EVOCATION)
            .setMaxLevel(5)
            .setCooldownSeconds(10)
            .build();

    public SpectralHammerSpell(int level) {
        super(SpellType.SPECTRAL_HAMMER_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 25;
        this.baseManaCost = 15;
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
    public boolean checkPreCastConditions(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        return Utils.getTargetBlock(level, entity, RayTraceContext.FluidMode.NONE, distance).getType() == RayTraceResult.Type.BLOCK;
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        var blockPosition = Utils.getTargetBlock(world, entity, RayTraceContext.FluidMode.NONE, distance);
        var face = blockPosition.getDirection();

        int radius = getRadius(entity);
        int depth = getDepth(entity);

        var spectralHammer = new SpectralHammer(world, entity, blockPosition, depth, radius);
        Vector3d position = Vector3d.atCenterOf(blockPosition.getBlockPos());

        if (!face.getAxis().isVertical()) {
            position = position.subtract(0, 2, 0).subtract(entity.getForward().normalize().scale(1.5));
        }else if(face == Direction.DOWN){
            position = position.subtract(0, 3, 0);
        }

        spectralHammer.setPos(position.x, position.y, position.z);
        world.addFreshEntity(spectralHammer);
        //IronsSpellbooks.LOGGER.debug("SpectralHammerSpell.onCast pos:{}", position);
        super.onCast(world, entity, playerMagicData);
    }

    private int getDepth(LivingEntity caster) {
        return (int) getSpellPower(caster);
    }

    private int getRadius(LivingEntity caster) {
        return (int) Math.max(getSpellPower(caster) * .5f, 1);
    }
}
