package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class LocalPlayerMixin{

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    public boolean addSlowdownCondition(ClientPlayerEntity instance) {
        return instance.isUsingItem() || ClientMagicData.isCasting();
    }
}