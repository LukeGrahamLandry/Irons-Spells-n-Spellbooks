package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class CommonPlayerEvents {
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        //IronsSpellbooks.LOGGER.debug("CommonPlayerEvents.onPlayerRightClickItem {}", event.getSide());
        ItemStack stack = event.getItemStack();

        if (Utils.canImbue(stack)) {
            SpellData spellData = SpellData.getSpellData(stack);
            ActionResult<ItemStack> result = Utils.onUseCastingHelper(event.getWorld(), event.getPlayer(), event.getHand(), stack, spellData);

            if (result != null) {
                event.setCancellationResult(result.getResult());
                event.setCanceled(true);
            }
        }
    }

    public static void onUseItemStop(LivingEntityUseItemEvent.Stop event) {
        //IronsSpellbooks.LOGGER.debug("CommonPlayerEvents.onUseItemStop {} {}", event.getEntity().getLevel().isClientSide, event.getItem().getItem());
        ItemStack stack = event.getItem();
        if (Utils.canImbue(stack)) {
            AbstractSpell spell = SpellData.getSpellData(stack).getSpell();
            LivingEntity entity = event.getEntityLiving();

            if (spell != SpellRegistry.none()) {
                entity.stopUsingItem();
                Utils.releaseUsingHelper(entity, stack, event.getDuration());
            }
        }
    }
}
