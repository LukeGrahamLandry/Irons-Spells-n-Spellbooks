package io.redspace.ironsspellbooks.damage;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SpellDamageSource extends EntityDamageSource implements ISpellDamageSource {
    AbstractSpell spell;
    float lifesteal;
    int freezeTicks;
    int fireTime;

    public SpellDamageSource(@NotNull Entity causingEntity, AbstractSpell spell) {
        super(spell.getDeathMessageId(), causingEntity);
        this.spell = spell;
    }

    @Override
    public @NotNull ITextComponent getLocalizedDeathMessage(@NotNull LivingEntity pLivingEntity) {
        String s = "death.attack." + spell.getDeathMessageId();
        ITextComponent component = this.entity.getDisplayName();
        return new TranslationTextComponent(s, pLivingEntity.getDisplayName(), component);
    }

    public SpellDamageSource setLifestealPercent(float lifesteal) {
        this.lifesteal = lifesteal;
        return this;
    }

    public SpellDamageSource setFireTime(int fireTime) {
        this.fireTime = fireTime;
        return this;
    }

    public SpellDamageSource setFreezeTicks(int freezeTicks) {
        this.freezeTicks = freezeTicks;
        return this;
    }

    @Override
    public DamageSource get() {
        return this;
    }

    @Override
    public AbstractSpell spell() {
        return this.spell;
    }

    @Override
    public float getLifestealPercent() {
        return this.lifesteal;
    }

    @Override
    public int getFireTime() {
        return this.fireTime;
    }

    @Override
    public int getFreezeTicks() {
        return this.freezeTicks;
    }
}
