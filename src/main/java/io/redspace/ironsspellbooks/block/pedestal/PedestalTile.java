package io.redspace.ironsspellbooks.block.pedestal;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;

import javax.annotation.Nonnull;

public class PedestalTile extends TileEntity {
    private static final String NBT_HELD_ITEM = "heldItem";

    //    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
//        @Override
//        protected void onContentsChanged(int slot) {
//            updateMenuSlots();
//            setChanged();
//        }
//    };
//
//    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    private ItemStack heldItem = ItemStack.EMPTY;

    public PedestalTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockRegistry.PEDESTAL_TILE.get(), pWorldPosition, pBlockState);
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(ItemStack newItem) {
        heldItem = newItem;
        setChanged();

    }

//    @Override
//    public void onLoad() {
//        super.onLoad();
//        lazyItemHandler = LazyOptional.of(() -> itemHandler);
//
//    }


    public void drops() {
        Inventory simpleContainer = new Inventory(heldItem);

        InventoryHelper.dropContents(this.level, this.worldPosition, simpleContainer);
    }

    @Override
    public void load(CompoundNBT nbt) {
        super.load(nbt);
 //Ironsspellbooks.logger.debug("Loading Pedestal NBT");
        readNBT(nbt);

    }

    @Override
    protected void saveAdditional(@Nonnull CompoundNBT tag) {
        //irons_spellbooks.LOGGER.debug("saveAdditional tag:{}", tag);
        //tag.put("inventory", itemHandler.serializeNBT());
        writeNBT(tag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        //tag.put("inventory", itemHandler.serializeNBT());
        writeNBT(tag);
        //irons_spellbooks.LOGGER.debug("getUpdateTag tag:{}", tag);
        return tag;
    }

    @Override
    public boolean triggerEvent(int pId, int pType) {
        return super.triggerEvent(pId, pType);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        //var packet = ClientboundBlockEntityDataPacket.create(this);
        //irons_spellbooks.LOGGER.debug("getUpdatePacket: packet.getTag:{}", packet.getTag());
        CompoundNBT nbt = writeNBT(new CompoundNBT());
        return SUpdateTileEntityPacket.create(this, (block) -> nbt);
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

    //    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
//        if (cap == ForgeCapabilities.ITEM_HANDLER) {
//            return lazyItemHandler.cast();
//        }
//
//        return super.getCapability(cap, side);
//    }

    private CompoundNBT writeNBT(CompoundNBT nbt) {
        nbt.put(NBT_HELD_ITEM, heldItem.serializeNBT());
        //irons_spellbooks.LOGGER.debug("getUpdateTag tag:{}", tag);
        return nbt;
    }

    private CompoundNBT readNBT(CompoundNBT nbt) {
        if (nbt.contains(NBT_HELD_ITEM)) {
            //itemHandler.deserializeNBT(nbt.getCompound("inventory"));
 //Ironsspellbooks.logger.debug("Pedestal NBT contains held item ({})", nbt.getCompound(NBT_HELD_ITEM));

            //heldItem.deserializeNBT(nbt.getCompound(NBT_HELD_ITEM));
            heldItem = ItemStack.of(nbt.getCompound(NBT_HELD_ITEM));
 //Ironsspellbooks.logger.debug("Held Item: {}", heldItem);

        }
        return nbt;
    }
}
