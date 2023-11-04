package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class FrostbiteSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "frostbite");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList(
                new TranslationTextComponent("ui.irons_spellbooks.frostbite_success_chance", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1))

        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
            .setMaxLevel(0)
            .setCooldownSeconds(0)
            .setEnabled(false)
            .build();

    public FrostbiteSpell() {
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 3;
        this.castTime = 0;
        this.baseManaCost = 100;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
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
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        RayTraceResult raycast = Utils.raycastForEntity(level, entity, 48, true);
        if (raycast.getType() == RayTraceResult.Type.ENTITY) {
            Entity target = ((EntityRayTraceResult) raycast).getEntity();
            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                float threshold = getSpellPower(spellLevel, entity);
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
        super.onCast(level, spellLevel, entity, playerMagicData);
    }
}
