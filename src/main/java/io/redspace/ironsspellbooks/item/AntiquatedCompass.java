package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.item.Item.Properties;

public class AntiquatedCompass extends Item {
    private static final ITextComponent description = new TranslationTextComponent("item.irons_spellbooks.antiquated_compass_desc").withStyle(Style.EMPTY.withColor(0x873200));
    public AntiquatedCompass() {
        super(new Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB));
    }

    public static GlobalPos getCitadelLocation(Entity entity, CompoundNBT compoundTag) {
        if (!(entity.level.dimension() == World.NETHER && compoundTag.contains("CitadelPos")))
            return null;

        return GlobalPos.of(entity.level.dimension(), NBTUtil.readBlockPos(compoundTag.getCompound("CitadelPos")));
    }

    @Override
    public void onCraftedBy(ItemStack pStack, World pLevel, PlayerEntity pPlayer) {
        if (pLevel instanceof ServerWorld) {
            ServerWorld serverlevel = (ServerWorld) pLevel;
            BlockPos blockpos = serverlevel.findNearestMapStructure(ModTags.ANTIQUATED_COMPASS_LOCATOR, pPlayer.blockPosition(), 100, false);
            if (blockpos != null) {
                CompoundNBT tag = pStack.getOrCreateTag();
                tag.put("CitadelPos", NBTUtil.writeBlockPos(blockpos));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> pTooltipComponents, ITooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(description);
    }
}
