package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow
    private void fillRect(BufferBuilder p_115153_, int p_115154_, int p_115155_, int p_115156_, int p_115157_, int p_115158_, int p_115159_, int p_115160_, int p_115161_) {
    }

    @Inject(method = "renderGuiItemDecorations", at = @At(value = "TAIL"))
    public void renderSpellbookCooldown(FontRenderer font, ItemStack stack, int one, int two, CallbackInfo ci) {
        Item item = stack.getItem();
        if (item instanceof SpellBook) {
            AbstractSpell spell = SpellBookData.getSpellBookData(stack).getActiveSpell().getSpell();
            renderSpellCooldown(one, two, spell);
        } else if (SpellData.hasSpellData(stack) && !stack.getItem().equals(ItemRegistry.SCROLL.get())) {
            AbstractSpell spell = SpellData.getSpellData(stack).getSpell();
            renderSpellCooldown(one, two, spell);
        }
    }

    private void renderSpellCooldown(int one, int two, AbstractSpell spell) {
        if (!spell.equals(SpellRegistry.none())) {
            float f = ClientMagicData.getCooldownPercent(spell);

            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tessellator tesselator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                fillRect(bufferbuilder, one, two + MathHelper.floor(16.0F * (1.0F - f)), 16, MathHelper.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }
}
