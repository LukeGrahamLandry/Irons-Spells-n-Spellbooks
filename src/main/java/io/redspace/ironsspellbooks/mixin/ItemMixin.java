package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.capabilities.magic.UpgradeData;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Default priority is 1000
@Mixin(Item.class)
public abstract class ItemMixin {

    /*
    Necessary to display how many times a piece of gear has been upgraded on its name
     */
    @Inject(method = "getName", at = @At("TAIL"), cancellable = true)
    public void getHoverName(ItemStack stack, CallbackInfoReturnable<ITextComponent> cir) {
        //IronsSpellbooks.LOGGER.info("{}", cir.getReturnValue().getString());
        if (UpgradeData.hasUpgradeData(stack)) {
            cir.setReturnValue(ITextComponent.translatable("tooltip.irons_spellbooks.upgrade_plus_format", cir.getReturnValue(), UpgradeData.getUpgradeData(stack).getCount()));
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!SpellData.getSpellData(stack).equals(SpellData.EMPTY)) {
            cir.setReturnValue(7200);
        }
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    public void getUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAction> cir) {
        if (!SpellData.getSpellData(stack).equals(SpellData.EMPTY)) {
            cir.setReturnValue(UseAction.BOW);
        }
    }
}
