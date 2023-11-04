package io.redspace.ironsspellbooks.item.consumables;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.World;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.DrinkHelper;

public class DrinkableItem extends Item {
    public DrinkableItem(Properties pProperties, BiConsumer<ItemStack, LivingEntity> drinkAction, @Nullable Item returnItem, boolean showDescription) {
        super(pProperties);
        this.drinkAction = drinkAction;
        this.returnItem = returnItem;
        this.showDesc = showDescription;
    }

    public DrinkableItem(Properties pProperties, BiConsumer<ItemStack, LivingEntity> drinkAction) {
        this(pProperties, drinkAction, null, true);
    }

    private final BiConsumer<ItemStack, LivingEntity> drinkAction;
    private final Item returnItem;
    private final boolean showDesc;

    public ItemStack finishUsingItem(ItemStack pStack, World pLevel, LivingEntity pEntityLiving) {
        PlayerEntity player = pEntityLiving instanceof PlayerEntity ? (PlayerEntity) pEntityLiving : null;
        if (player instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) player, pStack);
        }

        if (!pLevel.isClientSide) {
            this.drinkAction.accept(pStack, pEntityLiving);
        }

        if (player != null && !player.getAbilities().instabuild) {
            pStack.shrink(1);
        }

        if (returnItem != null && (player == null || !player.getAbilities().instabuild)) {
            if (pStack.isEmpty()) {
                return new ItemStack(returnItem);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(returnItem));
            }
        }

        pEntityLiving.gameEvent(GameEvent.DRINK);
        return pStack;
    }

    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

    public UseAction getUseAnimation(ItemStack pStack) {
        return UseAction.DRINK;
    }

    public ActionResult<ItemStack> use(World pLevel, PlayerEntity pPlayer, Hand pHand) {
        return DrinkHelper.startUsingInstantly(pLevel, pPlayer, pHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable World pLevel, List<ITextComponent> pTooltipComponents, ITooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if(showDesc ){
            pTooltipComponents.add(ITextComponent.empty());
            pTooltipComponents.add(new TranslationTextComponent("potion.whenDrank").withStyle(TextFormatting.DARK_PURPLE));
            pTooltipComponents.add(new TranslationTextComponent(this.getDescriptionId() + ".desc").withStyle(TextFormatting.BLUE));
        }
    }
}
