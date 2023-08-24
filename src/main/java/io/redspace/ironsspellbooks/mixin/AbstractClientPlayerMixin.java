package io.redspace.ironsspellbooks.mixin;

import com.mojang.authlib.GameProfile;
import io.redspace.ironsspellbooks.item.armor.ArmorCapeProvider;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.AbstractClientPlayerMixinHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerMixin {

    @Inject(method = "getCloakTextureLocation", at = @At(value = "HEAD"), cancellable = true)
    public void getCloakTextureLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        var player = (PlayerEntity) (Object) this;
        ItemStack itemstack = player.getItemBySlot(EquipmentSlotType.CHEST);
        if (itemstack.getItem() instanceof ArmorCapeProvider capeProvider && !player.hasEffect(MobEffectRegistry.ANGEL_WINGS.get())) {
            cir.setReturnValue(capeProvider.getCapeResourceLocation());
        }
    }

    @Inject(method = "isCapeLoaded", at = @At(value = "HEAD"), cancellable = true)
    public void isCapeLoaded(CallbackInfoReturnable<Boolean> cir) {
        var player = (PlayerEntity) (Object) this;
        ItemStack itemstack = player.getItemBySlot(EquipmentSlotType.CHEST);
        if (itemstack.getItem() instanceof ArmorCapeProvider capeProvider) {
            cir.setReturnValue(true);
        }
    }

//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void postInit(ClientLevel world, GameProfile profile, ProfilePublicKey publicKey, CallbackInfo ci) {
//        //var player = (Player) (Object) this;
//        //AbstractClientPlayerMixinHelper.playerMixinInit(player);
//    }

}
