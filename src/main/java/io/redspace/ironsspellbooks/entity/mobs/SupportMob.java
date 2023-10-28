package io.redspace.ironsspellbooks.entity.mobs;

import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;

public interface SupportMob {
    @Nullable
    LivingEntity getSupportTarget();

    void setSupportTarget(LivingEntity target);
}
