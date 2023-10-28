package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameGui.class)
public class GuiMixin {

    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"), cancellable = true)
    public void renderExperienceBar(MatrixStack pPoseStack, int pXPos, CallbackInfo ci) {
        if (ClientConfigs.MANA_BAR_ANCHOR.get() == ManaBarOverlay.Anchor.XP && Minecraft.getInstance().player != null && ManaBarOverlay.shouldShowManaBar(Minecraft.getInstance().player))
            ci.cancel();
    }
}
