package io.redspace.ironsspellbooks.gui.scroll_forge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.gui.scroll_forge.network.ServerboundScrollForgeSelectSpell;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScrollForgeScreen extends ContainerScreen<ScrollForgeMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/scroll_forge.png");
    private static final int SPELL_LIST_X = 89;
    private static final int SPELL_LIST_Y = 15;
    public static final ResourceLocation RUNIC_FONT = new ResourceLocation("illageralt");

    private List<SpellCardInfo> availableSpells;
    private ItemStack[] oldMenuSlots = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};

    private AbstractSpell selectedSpell = SpellRegistry.none();
    private int scrollOffset;

    public ScrollForgeScreen(ScrollForgeMenu menu, PlayerInventory inventory, ITextComponent title) {
        super(menu, inventory, title);
        this.imageWidth = 218;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        availableSpells = new ArrayList<>();
        generateSpellList();
        super.init();
    }

    @Override
    public void onClose() {
        setSelectedSpell(SpellRegistry.none());
        resetList();
        super.onClose();
    }

    private void resetList() {
        if (!(!menu.getInkSlot().getItem().isEmpty() && (menu.getInkSlot().getItem().getItem() instanceof InkItem inkItem && inkItem.getRarity().compareRarity(ServerConfigs.getSpellConfig(selectedSpell).minRarity()) >= 0)))
            setSelectedSpell(SpellRegistry.none());
        //TODO: reorder setting old focus to test if we actually need to reset the spell... or just give ink its own path since we dont even need to regenerate the list anyways
        //TODO: update: what the fuck does that mean
        scrollOffset = 0;

        for (SpellCardInfo s : availableSpells) {
            removeWidget(s.button);
        }
        availableSpells.clear();
    }

    @Override
    public void render(MatrixStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        setTexture(TEXTURE);

        this.blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

//        if (lastFocusItem != menu.getFocusSlot().getItem()) {
//            generateSpellList();
//            lastFocusItem = menu.getFocusSlot().getItem();
//        }
        if (menuSlotsChanged())
            generateSpellList();
        renderSpellList(poseStack, partialTick, mouseX, mouseY);
        //irons_spellbooks.LOGGER.debug("{}", this.menu.getFocusSlot().getItem().getItem().toString());

    }

    private boolean menuSlotsChanged() {
        if (menu.getInkSlot().getItem().getItem() != oldMenuSlots[0].getItem() || /*menu.getBlankScrollSlot().getItem().getItem() != oldMenuSlots[1].getItem() || */menu.getFocusSlot().getItem().getItem() != oldMenuSlots[2].getItem()) {
            oldMenuSlots = new ItemStack[]{
                    menu.getInkSlot().getItem(),
                    menu.getBlankScrollSlot().getItem(),
                    menu.getFocusSlot().getItem()
            };
            return true;
        } else
            return false;
    }

    private void renderSpellList(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        ItemStack inkStack = menu.getInkSlot().getItem();

        SpellRarity inkRarity = getRarityFromInk(inkStack.getItem());

        availableSpells.sort((a, b) -> ServerConfigs.getSpellConfig(a.spell).minRarity().compareRarity(ServerConfigs.getSpellConfig(b.spell).minRarity()));

        List<IReorderingProcessor> additionalTooltip = null;
        for (int i = 0; i < availableSpells.size(); i++) {
            SpellCardInfo spellCard = availableSpells.get(i);

            if (i - scrollOffset >= 0 && i - scrollOffset < 3) {
                spellCard.button.active = inkRarity != null && spellCard.spell.getMinRarity() <= inkRarity.getValue();
                int x = leftPos + SPELL_LIST_X;
                int y = topPos + SPELL_LIST_Y + (i - scrollOffset) * 19;
                spellCard.button.x = x;
                spellCard.button.y = y;
                spellCard.draw(this, poseStack, x, y, mouseX, mouseY);
                if (additionalTooltip == null)
                    additionalTooltip = spellCard.getTooltip(x, y, mouseX, mouseY);
            } else {
                spellCard.button.active = false;
            }
        }

        if (additionalTooltip != null) {
            this.renderTooltip(poseStack, additionalTooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double direction) {
        int length = availableSpells.size();
        int newScroll = scrollOffset - (int) direction;
        if (newScroll <= length - 3 && newScroll >= 0) {
            scrollOffset -= direction;
            return true;
        } else {
            return false;
        }
    }

    public void generateSpellList() {
        this.resetList();

        ItemStack focusStack = menu.getFocusSlot().getItem();
        IronsSpellbooks.LOGGER.info("ScrollForgeMenu.generateSpellSlots.focus: {}", focusStack.getItem());
        if (!focusStack.isEmpty() && focusStack.is(ModTags.SCHOOL_FOCUS)) {
            SchoolType school = SchoolRegistry.getSchoolFromFocus(focusStack);
            //irons_spellbooks.LOGGER.info("ScrollForgeMenu.generateSpellSlots.school: {}", school.toString());
            var spells = SpellRegistry.getSpellsForSchool(school);
            for (int i = 0; i < spells.size(); i++) {
                //int id = spells[i].getValue();
                int tempIndex = i;
                //IronsSpellbooks.LOGGER.debug("ScrollForgeScreen.generateSpellList: {} isEnabled: {}", spells[i], spells[i].isEnabled());
                if (spells.get(i).isEnabled())
                    availableSpells.add(new SpellCardInfo(spells.get(i), i + 1, i, this.addWidget(
                            new Button(0, 0, 108, 19,
                                    spells.get(i).getDisplayName(),
                                    (b) -> this.setSelectedSpell(spells.get(tempIndex)))
                    )));
            }
        }
    }

    private void setSelectedSpell(AbstractSpell spell) {
        selectedSpell = spell;
        Messages.sendToServer(new ServerboundScrollForgeSelectSpell(this.menu.blockEntity.getBlockPos(), spell.getSpellId()));
    }

    private SpellRarity getRarityFromInk(Item ink) {
        if (ink instanceof InkItem inkItem) {
            return inkItem.getRarity();
        } else {
            return null;
        }
    }

    public AbstractSpell getSelectedSpell() {
        return selectedSpell;
    }

    private void setTexture(ResourceLocation texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
    }

    private class SpellCardInfo {
        AbstractSpell spell;
        int spellLevel;
        SpellRarity rarity;
        Button button;
        int index;

        SpellCardInfo(AbstractSpell spell, int spellLevel, int index, Button button) {
            this.spell = spell;
            this.spellLevel = spellLevel;
            this.index = index;
            this.button = button;
            this.rarity = spell.getRarity(spellLevel);
        }

        void draw(ScrollForgeScreen screen, MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
            setTexture(TEXTURE);
            if (this.button.active) {
                if (spell == screen.getSelectedSpell())//mouseX >= x && mouseY >= y && mouseX < x + 108 && mouseY < y + 19)
                    screen.blit(poseStack, x, y, 0, 204, 108, 19);
                else
                    screen.blit(poseStack, x, y, 0, 166, 108, 19);

            } else {
                screen.blit(poseStack, x, y, 0, 185, 108, 19);
                //font.drawWordWrap(, x + 2, y + 2, maxWidth, 0xFFFFFF);
            }
            setTexture(this.button.active ? spell.getSpellIconResource() : SpellRegistry.none().getSpellIconResource());
            screen.blit(poseStack, x + 108 - 18, y + 1, 0, 0, 16, 16, 16, 16);

            int maxWidth = 108 - 20;
            var text = trimText(font, getDisplayName().withStyle(this.button.active ? Style.EMPTY : Style.EMPTY.withFont(RUNIC_FONT)), maxWidth);
            int textX = x + 2;
            int textY = y + 3;
            font.drawWordWrap(text, textX, textY, maxWidth, 0xFFFFFF);
        }

        @Nullable
        List<IReorderingProcessor> getTooltip(int x, int y, int mouseX, int mouseY) {
            var text = getDisplayName();
            int textX = x + 2;
            int textY = y + 3;
            if (mouseX >= textX && mouseY >= textY && mouseX < textX + font.width(text) && mouseY < textY + font.lineHeight) {
                return getHoverText();
            } else {
                return null;
            }
        }

        List<IReorderingProcessor> getHoverText() {
            if (!this.button.active) {
                return List.of(IReorderingProcessor.forward(ITextComponent.translatable("ui.irons_spellbooks.ink_rarity_error").getString(), Style.EMPTY));
            } else {
                return TooltipsUtils.createSpellDescriptionTooltip(this.spell, font);
            }
        }

        private ITextProperties trimText(FontRenderer font, ITextComponent component, int maxWidth) {
            var text = font.getSplitter().splitLines(component, maxWidth, component.getStyle()).get(0);
            if (text.getString().length() < component.getString().length())
                text = ITextProperties.composite(text, ITextProperties.of("..."));
            return text;
        }

        IFormattableTextComponent getDisplayName() {
            return spell.getDisplayName();
        }
    }
}
