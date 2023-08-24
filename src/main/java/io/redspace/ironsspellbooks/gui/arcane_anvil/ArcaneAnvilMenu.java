package io.redspace.ironsspellbooks.gui.arcane_anvil;

import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MenuRegistry;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;

public class ArcaneAnvilMenu extends AbstractRepairContainer {
    public ArcaneAnvilMenu(int pContainerId, PlayerInventory inventory, IWorldPosCallable containerLevelAccess) {
        super(MenuRegistry.ARCANE_ANVIL_MENU.get(), pContainerId, inventory, containerLevelAccess);
    }


    public ArcaneAnvilMenu(int pContainerId, PlayerInventory inventory, PacketBuffer extraData) {
        this(pContainerId, inventory, IWorldPosCallable.NULL);
    }

    @Override
    protected boolean mayPickup(PlayerEntity pPlayer, boolean pHasStack) {
        return true;
    }

    @Override
    protected void onTake(PlayerEntity p_150601_, ItemStack p_150602_) {
        inputSlots.getItem(0).shrink(1);
        inputSlots.getItem(1).shrink(1);

        this.access.execute((level, pos) -> {
            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundCategory.BLOCKS, .8f, 1.1f);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 1f, 1f);
        });
    }

    @Override
    protected boolean isValidBlock(BlockState pState) {
        return pState.is(BlockRegistry.ARCANE_ANVIL_BLOCK.get());
    }

    @Override
    public void createResult() {
        ItemStack result = ItemStack.EMPTY;
        /*
        Actions that can be taken in arcane anvil:
        - Upgrade scroll (scroll + scroll)
        - Imbue Weapon (weapon + scroll)
        - Upgrade item (item + upgrade orb)
         */
        ItemStack baseItemStack = inputSlots.getItem(0);
        ItemStack modifierItemStack = inputSlots.getItem(1);
        if (!baseItemStack.isEmpty() && !modifierItemStack.isEmpty()) {
            //Scroll Merging
            if (baseItemStack.getItem() instanceof Scroll && modifierItemStack.getItem() instanceof Scroll) {
                var scrollData1 = SpellData.getSpellData(baseItemStack);
                var scrollData2 = SpellData.getSpellData(modifierItemStack);
                if (scrollData1.getSpellId() == scrollData2.getSpellId() && scrollData1.getLevel() == scrollData2.getLevel()) {
                    if (scrollData1.getLevel() < ServerConfigs.getSpellConfig(scrollData1.getSpellId()).maxLevel()) {
                        result = new ItemStack(ItemRegistry.SCROLL.get());

                        SpellData.setSpellData(result, scrollData1.getSpellId(), scrollData1.getLevel() + 1);
                    }
                }

            }
            //Weapon Imbuement
            else if (Utils.canImbue(baseItemStack) && modifierItemStack.getItem() instanceof Scroll) {
                result = baseItemStack.copy();
                var scrollData = SpellData.getSpellData(modifierItemStack);
                SpellData.setSpellData(result, scrollData.getSpell());
            }
            //Upgrade System
            else if (Utils.canBeUpgraded(baseItemStack) && UpgradeUtils.getUpgradeCount(baseItemStack) < ServerConfigs.MAX_UPGRADES.get() && modifierItemStack.getItem() instanceof UpgradeOrbItem upgradeOrb) {
                result = baseItemStack.copy();
                EquipmentSlotType slot = UpgradeUtils.getAssignedEquipmentSlot(result);
                UpgradeUtils.appendUpgrade(result, upgradeOrb.getUpgradeType(), slot);
                //IronsSpellbooks.LOGGER.debug("ArcaneAnvilMenu: upgrade system test: total upgrades on {}: {}", result.getDisplayName().getString(), UpgradeUtils.getUpgradeCount(result));
            }
        }

        resultSlots.setItem(0, result);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container != this.resultSlots && super.canTakeItemForPickAll(pStack, pSlot);
    }
}
