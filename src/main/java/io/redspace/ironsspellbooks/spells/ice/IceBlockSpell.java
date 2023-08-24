package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class IceBlockSpell extends AbstractSpell {
    public IceBlockSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(caster), 1)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE )
            .setSchool(SchoolType.ICE)
            .setMaxLevel(6)
            .setCooldownSeconds(15)
            .build();

    public IceBlockSpell(int level) {
        super(SpellType.ICE_BLOCK_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 2;
        this.castTime = 30;
        this.baseManaCost = 40;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.ICE_BLOCK_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public boolean checkPreCastConditions(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, getSpellType(), 48, .35f, false);
        return true;
    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        Vector3d spawn = null;
        LivingEntity target = null;

        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData castTargetingData) {
            target = castTargetingData.getTarget((ServerWorld) level);
            if (target != null)
                spawn = target.position();
        }
        if (spawn == null) {
            RayTraceResult raycast = Utils.raycastForEntity(level, entity, 32, true, .25f);
            if (raycast.getType() == RayTraceResult.Type.ENTITY) {
                spawn = ((EntityRayTraceResult) raycast).getEntity().position();
                if (((EntityRayTraceResult) raycast).getEntity() instanceof LivingEntity livingEntity)
                    target = livingEntity;
            } else {
                spawn = raycast.getLocation().subtract(entity.getForward().normalize());
            }
        }

        IceBlockProjectile iceBlock = new IceBlockProjectile(level, entity, target);
        iceBlock.moveTo(raiseWithCollision(spawn, 4, level));
        iceBlock.setAirTime(target == null ? 20 : 50);
        iceBlock.setDamage(getDamage(entity));
        level.addFreshEntity(iceBlock);
        super.onCast(level, entity, playerMagicData);
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

    private float getDamage(LivingEntity entity) {
        return this.getSpellPower(entity);
    }
}
