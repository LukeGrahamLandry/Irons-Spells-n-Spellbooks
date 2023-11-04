package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.item.Item.Properties;

public class WaywardCompass extends Item {
    private static final ITextComponent description = new TranslationTextComponent("item.irons_spellbooks.wayward_compass_desc").withStyle(TextFormatting.DARK_AQUA);
    public WaywardCompass() {
        super(new Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB));
    }

    public static GlobalPos getCatacombsLocation(Entity entity, CompoundNBT compoundTag) {
        if (!(entity.level.dimension() == World.OVERWORLD && compoundTag.contains("CatacombsPos")))
            return null;

        return GlobalPos.of(entity.level.dimension(), NBTUtil.readBlockPos(compoundTag.getCompound("CatacombsPos")));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World level, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!level.isClientSide) {
            CompoundNBT tag = itemStack.getOrCreateTag();
            if (!tag.contains("isInInventory")) {
                tag.putBoolean("isInInventory", true);
            }
        }
    }

    @Override
    public void onCraftedBy(ItemStack pStack, World pLevel, PlayerEntity pPlayer) {
        findCatacombs(pStack, pLevel, pPlayer);
    }

    private static void findCatacombs(ItemStack pStack, World pLevel, PlayerEntity pPlayer) {
        if (pLevel instanceof ServerWorld) {
            ServerWorld serverlevel = (ServerWorld) pLevel;
            BlockPos blockpos = serverlevel.findNearestMapStructure(ModTags.WAYWARD_COMPASS_LOCATOR, pPlayer.blockPosition(), 100, false);
            if (blockpos != null) {
                CompoundNBT tag = pStack.getOrCreateTag();
                tag.put("CatacombsPos", NBTUtil.writeBlockPos(blockpos));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> use(World pLevel, PlayerEntity pPlayer, Hand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (missingWarning(itemStack)) {
            findCatacombs(itemStack, pLevel, pPlayer);
            pPlayer.getCooldowns().addCooldown(ItemRegistry.WAYWARD_COMPASS.get(), 200);
            return ActionResult.sidedSuccess(itemStack, pLevel.isClientSide);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public boolean missingWarning(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().contains("isInInventory") && !itemStack.getTag().contains("CatacombsPos");
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> pTooltipComponents, ITooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(description);
        if (missingWarning(pStack)) {
            pTooltipComponents.add(new TranslationTextComponent("item.irons_spellbooks.wayward_compass.error", Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage()).withStyle(TextFormatting.RED));
        }
    }
}
