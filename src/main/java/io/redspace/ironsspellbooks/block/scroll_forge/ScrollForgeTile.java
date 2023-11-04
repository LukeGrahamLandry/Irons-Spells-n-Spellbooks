package io.redspace.ironsspellbooks.block.scroll_forge;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeMenu;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ScrollForgeTile extends TileEntity implements INamedContainerProvider {
    private ScrollForgeMenu menu;

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            updateMenuSlots(slot);
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    public ScrollForgeTile() {
        super(BlockRegistry.SCROLL_FORGE_TILE.get());
    }

    private void updateMenuSlots(int slot) {
        if (menu != null) {
            menu.onSlotsChanged(slot);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack getStackInSlot(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public IFormattableTextComponent getDisplayName() {
        return new TranslationTextComponent("ui.irons_spellbooks.scroll_forge_title");
    }

    @Nullable
    @Override
    public Container createMenu(int containerId, PlayerInventory inventory, PlayerEntity player) {
        menu = new ScrollForgeMenu(containerId, inventory, this);
        return menu;
    }

    public void setRecipeSpell(String spellId) {
        menu.setRecipeSpell(SpellRegistry.getSpell(spellId));
    }

//    @Override
//    public void onLoad() {
//        super.onLoad();
//        lazyItemHandler = LazyOptional.of(() -> itemHandler);
//        irons_spellbooks.LOGGER.debug("ScrollForgeTile.chunkOnLoad: {}", itemHandler.getSlots());
//
//    }


    public void drops() {
        Inventory simpleContainer
                = new Inventory(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots() - 1; i++) {
            simpleContainer.setItem(i, itemHandler.getStackInSlot(i));
        }

        InventoryHelper.dropContents(this.level, this.worldPosition, simpleContainer);
    }

    @Override
    public void load(CompoundNBT nbt) {
        //irons_spellbooks.LOGGER.debug("ScrollForgeTile.loadingFromNBT: {}", nbt);
        super.load(nbt);
        if (nbt.contains("inventory")) {
            itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        lazyItemHandler.invalidate();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundNBT tag) {
        //irons_spellbooks.LOGGER.debug("saveAdditional tag:{}", tag);
        tag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("inventory", itemHandler.serializeNBT());
        //irons_spellbooks.LOGGER.debug("getUpdateTag tag:{}", tag);
        return tag;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        var packet = SUpdateTileEntityPacket.create(this);
        //irons_spellbooks.LOGGER.debug("getUpdatePacket: packet.getTag:{}", packet.getTag());
        return packet;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        //irons_spellbooks.LOGGER.debug("onDataPacket: pkt.getTag:{}", pkt.getTag());
        handleUpdateTag(pkt.getTag());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        //irons_spellbooks.LOGGER.debug("handleUpdateTag: tag:{}", tag);
        if (tag != null) {
            load(tag);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

//    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, InscriptionTableTile pBlockEntity) {
//        if(hasRecipe(pBlockEntity) && hasNotReachedStackLimit(pBlockEntity)) {
//            craftItem(pBlockEntity);
//        }
//    }
//
//    private static void craftItem(GemCuttingStationBlockEntity entity) {
//        entity.itemHandler.extractItem(0, 1, false);
//        entity.itemHandler.extractItem(1, 1, false);
//        entity.itemHandler.getStackInSlot(2).hurt(1, new Random(), null);
//
//        entity.itemHandler.setStackInSlot(3, new ItemStack(ModItems.CITRINE.get(),
//                entity.itemHandler.getStackInSlot(3).getCount() + 1));
//    }
//
//    private static boolean hasRecipe(GemCuttingStationBlockEntity entity) {
//        boolean hasItemInWaterSlot = PotionUtils.getPotion(entity.itemHandler.getStackInSlot(0)) == Potions.WATER;
//        boolean hasItemInFirstSlot = entity.itemHandler.getStackInSlot(1).getItem() == ModItems.RAW_CITRINE.get();
//        boolean hasItemInSecondSlot = entity.itemHandler.getStackInSlot(2).getItem() == ModItems.GEM_CUTTER_TOOL.get();
//
//        return hasItemInWaterSlot && hasItemInFirstSlot && hasItemInSecondSlot;
//    }
//
//    private static boolean hasNotReachedStackLimit(GemCuttingStationBlockEntity entity) {
//        return entity.itemHandler.getStackInSlot(3).getCount() < entity.itemHandler.getStackInSlot(3).getMaxStackSize();
//    }
}
