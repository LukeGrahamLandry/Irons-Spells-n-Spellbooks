package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.item.IScroll;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;

public class Scroll extends Item implements IScroll {

    public Scroll() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void fillItemCategory(@NotNull ItemGroup category, @NotNull NonNullList<ItemStack> items) {
        if (/*category == SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB ||*/ category == ItemGroup.TAB_SEARCH) {
            SpellRegistry.REGISTRY.get().getValues().stream()
                    .filter(AbstractSpell::isEnabled)
                    .forEach(spell -> {
                        int min = category == SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB ? spell.getMaxLevel() : spell.getMinLevel();

                        for (int i = min; i <= spell.getMaxLevel(); i++) {
                            ItemStack itemstack = new ItemStack(ItemRegistry.SCROLL.get());
                            SpellData.setSpellData(itemstack, spell, i);
                            items.add(itemstack);
                        }
                    });
        }
    }

    protected void removeScrollAfterCast(ServerPlayerEntity serverPlayer, ItemStack stack) {
        if (!serverPlayer.isCreative()) {
            stack.shrink(1);
        }
    }

    public static boolean attemptRemoveScrollAfterCast(ServerPlayerEntity serverPlayer) {
        ItemStack potentialScroll = MagicData.getPlayerMagicData(serverPlayer).getPlayerCastingItem();
        if (potentialScroll.getItem() instanceof Scroll) {
            Scroll scroll = (Scroll) potentialScroll.getItem();
            scroll.removeScrollAfterCast(serverPlayer, potentialScroll);
            return true;
        } else
            return false;
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(World level, PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            if (ClientMagicData.isCasting()) {
                return ActionResult.fail(stack);
            } else {
                return ActionResult.sidedSuccess(stack, level.isClientSide());
            }
        }

        SpellData spellData = SpellData.getSpellData(stack);
        AbstractSpell spell = spellData.getSpell();

        if (spell.attemptInitiateCast(stack, spellData.getLevel(), level, player, CastSource.SCROLL, false)) {
            if (spell.getCastType() == CastType.INSTANT) {
                removeScrollAfterCast((ServerPlayerEntity) player, stack);
            }
            if (spell.getCastType().holdToCast()) {
                player.startUsingItem(hand);
            }
            return ActionResult.success(stack);
        } else {
            return ActionResult.fail(stack);
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack) {
        return 7200;//return getScrollData(itemStack).getSpell().getCastTime();
    }

    @Override
    public UseAction getUseAnimation(ItemStack pStack) {
        return UseAction.BOW;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemStack, @NotNull World level, LivingEntity entity, int ticksUsed) {
        //entity.stopUsingItem();
        if (SpellData.getSpellData(itemStack).getSpell().getCastType() != CastType.CONTINUOUS || getUseDuration(itemStack) - ticksUsed >= 4) {
            Utils.releaseUsingHelper(entity, itemStack, ticksUsed);
        }
        super.releaseUsing(itemStack, level, entity, ticksUsed);
    }

    @Override
    public @NotNull ITextComponent getName(@NotNull ItemStack itemStack) {
        SpellData scrollData = SpellData.getSpellData(itemStack);
        return scrollData.getDisplayName();

    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable World level, List<ITextComponent> lines, @NotNull ITooltipFlag flag) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null)
            lines.addAll(TooltipsUtils.formatScrollTooltip(itemStack, player));
        super.appendHoverText(itemStack, level, lines, flag);
    }
}
