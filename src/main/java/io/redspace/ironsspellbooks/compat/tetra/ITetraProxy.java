package io.redspace.ironsspellbooks.compat.tetra;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public interface ITetraProxy {
    void initClient();

    boolean canImbue(ItemStack itemStack);

    void handleLivingAttackEvent(LivingAttackEvent event);
}

