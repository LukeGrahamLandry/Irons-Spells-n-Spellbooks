package io.redspace.ironsspellbooks.gui.arcane_anvil;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.gui.screen.inventory.AbstractRepairScreen;
import net.minecraft.client.renderer.GameRenderer;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.PlayerInventory;

public class ArcaneAnvilScreen extends AbstractRepairScreen<ArcaneAnvilMenu> {
    private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/arcane_anvil.png");

    public ArcaneAnvilScreen(ArcaneAnvilMenu pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle, ANVIL_LOCATION);
        this.titleLabelX = 48;
        this.titleLabelY = 24;
    }

    @Override
    protected void renderBg(MatrixStack pPoseStack, float pPartialTick, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ANVIL_LOCATION);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        this.blit(pPoseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        // X over arrow
        if (((this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem())) {
            this.blit(pPoseStack, leftPos + 99, topPos + 45, this.imageWidth, 0, 28, 21);
        }

    }
}
