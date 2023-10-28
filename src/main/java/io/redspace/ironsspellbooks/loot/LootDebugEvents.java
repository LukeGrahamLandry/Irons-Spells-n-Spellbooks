package io.redspace.ironsspellbooks.loot;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LootDebugEvents {

    private static final boolean debugLootTables = false;
    @SubscribeEvent
    public static void alertLootTable(PlayerInteractEvent.RightClickBlock event) {
        if(debugLootTables){
            var blockEntity = event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());
            if (blockEntity instanceof LockableLootTileEntity chest) {
                var lootTable = chest.lootTable;
                if (lootTable != null) {
                    if (event.getEntity() instanceof ServerPlayerEntity serverPlayer)
                        serverPlayer.sendSystemMessage(ITextComponent.literal(chest.lootTable.toString()).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, lootTable.toString()))));
                    IronsSpellbooks.LOGGER.info("{}", chest.lootTable);
                }
            }
        }
    }
}
