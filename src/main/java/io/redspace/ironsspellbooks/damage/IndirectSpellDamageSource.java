package io.redspace.ironsspellbooks.damage;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class IndirectSpellDamageSource extends IndirectEntityDamageSource implements ISpellDamageSource {
    AbstractSpell spell;
    float lifesteal;
    int freezeTicks;
    int fireTime;

    public IndirectSpellDamageSource(@NotNull Entity directEntity, @NotNull Entity causingEntity, AbstractSpell spell) {
        super(spell.getDeathMessageId(), directEntity, causingEntity);
        this.spell = spell;
    }

    @Override
    public @NotNull ITextComponent getLocalizedDeathMessage(@NotNull LivingEntity pLivingEntity) {
        String s = "death.attack." + spell.getDeathMessageId();
        ITextComponent component = this.entity != null ? this.getEntity().getDisplayName() : this.getDirectEntity().getDisplayName();
        return new TranslationTextComponent(s, pLivingEntity.getDisplayName(), component);
    }

    public IndirectSpellDamageSource setLifestealPercent(float lifesteal) {
        this.lifesteal = lifesteal;
        return this;
    }

    public IndirectSpellDamageSource setFireTime(int fireTime) {
        this.fireTime = fireTime;
        return this;
    }

    public IndirectSpellDamageSource setFreezeTicks(int freezeTicks) {
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
