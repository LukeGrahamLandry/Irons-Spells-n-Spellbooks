package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.creeper_head.CreeperHeadProjectile;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class ChainCreeperSpell extends AbstractSpell {
    public ChainCreeperSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.projectile_count", getCount(caster)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.EVOCATION)
            .setMaxLevel(6)
            .setCooldownSeconds(15)
            .build();

    public ChainCreeperSpell(int level) {
        super(SpellType.CHAIN_CREEPER_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 0;
        this.castTime = 30;
        this.baseManaCost = 40;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.CREEPER_PRIMED);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.EVOKER_CAST_SPELL);
    }

    @Override
    public boolean checkPreCastConditions(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, getSpellType(), 48, .25f, false);
        return true;

    }

    @Override
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        Vector3d spawn = null;
        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData castTargetingData) {
            spawn = castTargetingData.getTargetPosition((ServerWorld) level);
        }
        if (spawn == null) {
            RayTraceResult raycast = Utils.raycastForEntity(level, entity, 32, true);
            if (raycast.getType() == RayTraceResult.Type.ENTITY) {
                spawn = ((EntityRayTraceResult) raycast).getEntity().position();
            } else {
                spawn = Utils.moveToRelativeGroundLevel(level, raycast.getLocation().subtract(entity.getForward().normalize()).add(0, 2, 0), 5);
            }
        }
        summonCreeperRing(level, entity, spawn.add(0, 0.5, 0), getDamage(entity), getCount(entity));

        super.onCast(level, entity, playerMagicData);
    }

    public static void summonCreeperRing(World level, LivingEntity owner, Vector3d origin, float damage, int count) {
        int degreesPerCreeper = 360 / count;
        for (int i = 0; i < count; i++) {

            Vector3d motion = new Vector3d(0, 0, .3 + count * .01f);
            motion = motion.xRot(75 * MathHelper.DEG_TO_RAD);
            motion = motion.yRot(degreesPerCreeper * i * MathHelper.DEG_TO_RAD);


            CreeperHeadProjectile head = new CreeperHeadProjectile(owner, level, motion, damage);
            head.setChainOnKill(true);

            Vector3d spawn = origin.add(motion.multiply(1, 0, 1).normalize().scale(.3f));
            var angle = Utils.rotationFromDirection(motion);

            head.moveTo(spawn.x, spawn.y - head.getBoundingBox().getYsize() / 2, spawn.z, angle.y, angle.x);
            level.addFreshEntity(head);
        }
    }

    private int getCount(LivingEntity entity) {
        return 3 + getLevel(entity) - 1;
    }

    private float getDamage(LivingEntity entity) {
        return this.getSpellPower(entity);
    }
}
