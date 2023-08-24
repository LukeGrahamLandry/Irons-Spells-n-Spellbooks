package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import java.util.List;
import java.util.Optional;

public class FrostbiteSpell extends AbstractSpell {
    public FrostbiteSpell() {
        this(1);
    }
    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                ITextComponent.translatable("ui.irons_spellbooks.frostbite_success_chance", Utils.stringTruncation(getSpellPower(caster), 1))

        );
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchool(SchoolType.ICE)
            .setMaxLevel(0)
            .setCooldownSeconds(0)
            .build();

    public FrostbiteSpell(int level) {
        super(SpellType.FROSTBITE_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 3;
        this.castTime = 0;
        this.baseManaCost = 100;
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
    public void onCast(World level, LivingEntity entity, PlayerMagicData playerMagicData) {
        RayTraceResult raycast = Utils.raycastForEntity(level, entity, 48, true);
        if (raycast.getType() == RayTraceResult.Type.ENTITY) {
            Entity target = ((EntityRayTraceResult) raycast).getEntity();
            if (target instanceof LivingEntity livingTarget) {
                float threshold = getSpellPower(entity);
                float hpPercent = livingTarget.getHealth() / livingTarget.getMaxHealth();
                boolean success = false;
                /*
                *   The Chance to succeed and inflict frostbite is based off of the current target's health
                *   If their health is below our spell power, we automatically succeed
                *   Otherwise, we have a chance to succeed
                * */
                if (livingTarget.getHealth() <= threshold)
                    success = true;
                //else if()
                //livingTarget.addEffect();
            }
        }
        super.onCast(level, entity, playerMagicData);
    }
}
