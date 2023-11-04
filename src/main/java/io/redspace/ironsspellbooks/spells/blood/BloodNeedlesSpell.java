package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.ISpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class BloodNeedlesSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "blood_needles");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(10)
            .build();

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 2)),
                new TranslationTextComponent("ui.irons_spellbooks.projectile_count", getCount(spellLevel)));
    }

    public BloodNeedlesSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 25;
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
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        int count = getCount(spellLevel);
        float damage = getDamage(spellLevel, entity);
        int degreesPerNeedle = 360 / count;
        RayTraceResult raycast = Utils.raycastForEntity(world, entity, 32, true);
        for (int i = 0; i < count; i++) {
            BloodNeedle needle = new BloodNeedle(world, entity);
            int rotation = degreesPerNeedle * i - (degreesPerNeedle / 2);
            needle.setDamage(damage);
            needle.setZRot(rotation);
            Vector3d spawn = entity.getEyePosition(0).add(new Vector3d(0, 1.5, 0).zRot(rotation * Utils.DEG_TO_RAD).xRot(-entity.xRot * Utils.DEG_TO_RAD).yRot(-entity.yRot * Utils.DEG_TO_RAD));
            needle.moveTo(spawn);
            needle.shoot(raycast.getLocation().subtract(spawn).normalize());
            world.addFreshEntity(needle);
        }
        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    @Override
    public DamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return ((ISpellDamageSource) super.getDamageSource(projectile, attacker)).setLifestealPercent(.25f).get();
    }

    private int getCount(int spellLevel) {
        return 5;
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) * .25f;
    }
}
