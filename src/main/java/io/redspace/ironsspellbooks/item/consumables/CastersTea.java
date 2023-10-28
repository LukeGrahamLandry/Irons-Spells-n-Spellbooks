package io.redspace.ironsspellbooks.item.consumables;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Item.Properties;

public class CastersTea extends DrinkableItem {
    public CastersTea(Properties pProperties) {
        super(pProperties, CastersTea::onConsume, null, true);
    }

    private static void onConsume(ItemStack itemStack, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayerEntity serverPlayer) {
            var cooldowns = MagicData.getPlayerMagicData(livingEntity).getPlayerCooldowns();
            cooldowns.getSpellCooldowns().forEach((key, value) -> cooldowns.decrementCooldown(value, (int) (value.getSpellCooldown() * .15f)));
            cooldowns.syncToPlayer(serverPlayer);
        }
    }
}
