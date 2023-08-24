package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.devour_jaw.DevourJaw;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;


public class DevourSpell extends AbstractSpell {
    public DevourSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.max_hp_on_kill", getHpBonus(caster))
        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.BLOOD)
            .setMaxLevel(10)
            .setCooldownSeconds(20)
            .build();

    public DevourSpell(int level) {
        super(SpellType.DEVOUR_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 4;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 25;

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
        return Utils.preCastTargetHelper(level, entity, playerMagicData, getSpellType(), 6, .1f);
    }

    @Override
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData targetData) {
            var targetEntity = targetData.getTarget((ServerWorld) world);
            if (targetEntity != null) {
                targetEntity.setDeltaMovement(targetEntity.getDeltaMovement().add(targetEntity.position().subtract(entity.position()).scale(-.25f)));
                targetEntity.hurtMarked = true;
                DevourJaw devour = new DevourJaw(world, entity, targetEntity);
                devour.setPos(targetEntity.position());
                devour.setYRot(entity.getYRot());
                devour.setDamage(getDamage(entity));
                devour.vigorLevel = (getHpBonus(entity) / 2) - 1;
                world.addFreshEntity(devour);
            }
        }

        super.onCast(world, entity, playerMagicData);
    }

    public float getDamage(LivingEntity caster) {
        return getSpellPower(caster);
    }

    public int getHpBonus(LivingEntity caster) {
        return 2 * (int) (getSpellPower(caster) * .25f);
    }

}
