package io.redspace.ironsspellbooks.player;

import net.minecraft.client.util.InputMappings;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import io.redspace.ironsspellbooks.gui.overlays.network.ServerboundSetSpellBookActiveIndex;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = IronsSpellbooks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientInputEvents {
    private static final ArrayList<KeyState> KEY_STATES = new ArrayList<>();

    private static final KeyState SPELL_WHEEL_STATE = register(KeyMappings.SPELL_WHEEL_KEYMAP);
    private static final KeyState SPELLBAR_MODIFIER_STATE = register(KeyMappings.SPELLBAR_SCROLL_MODIFIER_KEYMAP);
    private static final List<KeyState> QUICK_CAST_STATES = registerQuickCast(KeyMappings.QUICK_CAST_MAPPINGS);

    private static int useKeyId = Integer.MIN_VALUE;
    public static boolean isUseKeyDown;
    public static boolean hasReleasedSinceCasting;

    public static int test = 0;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        if (player == null)
            return;

        if (SPELL_WHEEL_STATE.wasPressed()) {
            if (minecraft.screen == null && Utils.isPlayerHoldingSpellBook(player))
                SpellWheelOverlay.instance.open();
        }
        if (SPELL_WHEEL_STATE.wasReleased()) {
            if (minecraft.screen == null && SpellWheelOverlay.instance.active)
                SpellWheelOverlay.instance.close();

        }

        update();
    }

    @SubscribeEvent
    public static void clientMouseScrolled(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        if (player == null)
            return;
        if (Utils.isPlayerHoldingSpellBook(player) && minecraft.screen == null) {
            if (SPELLBAR_MODIFIER_STATE.isHeld()) {
                ItemStack spellBookStack = player.getMainHandItem().getItem() instanceof SpellBook ? player.getMainHandItem() : player.getOffhandItem();
                SpellBookData spellBookData = SpellBookData.getSpellBookData(spellBookStack);
                if (spellBookData.getSpellCount() > 0) {
                    int direction = (int) event.getScrollDelta();
//                    irons_spellbooks.LOGGER.debug("original index: {}", spellBookData.getActiveSpellIndex());

                    List<SpellData> spells = new ArrayList<>();
                    for (SpellData spellData : spellBookData.getInscribedSpells()) {
                        if (spellData != null)
                            spells.add(spellData);
                    }
                    int spellCount = spellBookData.getSpellCount();
                    int scrollIndex = (spells.indexOf(spellBookData.getActiveSpell()) - direction);
//                    irons_spellbooks.LOGGER.debug("collapsed new index: {}", scrollIndex);
//                    irons_spellbooks.LOGGER.debug("{} + {} = {}", scrollIndex, spellCount, scrollIndex + spellCount);
//                    irons_spellbooks.LOGGER.debug("{} % {} = {}", scrollIndex + spellCount, spellCount, (scrollIndex + spellCount) % spellCount);
                    scrollIndex = (scrollIndex + spellCount) % spellCount;

//                    irons_spellbooks.LOGGER.debug("wrapped collapsed index: {}", scrollIndex);


                    int selectedIndex = ArrayUtils.indexOf(spellBookData.getInscribedSpells(), spells.get(scrollIndex));

//                    int newIndex = spellBookData.getActiveSpellIndex() - direction;
//                    newIndex = (newIndex + spellBookData.getSpellCount()) % spellBookData.getSpellCount();
//                    while (spellBookData.getInscribedSpells()[newIndex] == null) {
//                        newIndex = (newIndex + spellBookData.getSpellCount() - direction) % spellBookData.getSpellCount();
//                    }
//                    do {
//                        newIndex = (newIndex + direction) % spellBook.getCount();
//                        irons_spellbooks.LOGGER.debug("new index: {}",newIndex);
//                        irons_spellbooks.LOGGER.debug("spell here: {}",spellBookData.getInscribedSpells()[newIndex]);
//                        if(newIndex == spellBookData.getActiveSpellIndex())
//                            break;
//                    }
//                    while (spellBookData.getInscribedSpells()[newIndex] == null);
                    Messages.sendToServer(new ServerboundSetSpellBookActiveIndex(selectedIndex));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onUseInput(InputEvent.InteractionKeyMappingTriggered event) {
        //IronsSpellbooks.LOGGER.debug("InteractionKeyMappingTriggered: {}", event.getKeyMapping().getName());
        if (event.isUseItem()) {
            if (ClientSpellCastHelper.shouldSuppressRightClicks()) {
                event.setSwingHand(false);
                event.setCanceled(true);
            }
        } else if (event.isAttack()) {
            if (ClientMagicData.isCasting()) {
                event.setSwingHand(false);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        //IronsSpellbooks.LOGGER.debug("onKeyInput key:{}", event.getKey());
        handleRightClickSuppression(event.getKey(), event.getAction());

        for (int i = 0; i < QUICK_CAST_STATES.size(); i++) {
            //IronsSpellbooks.LOGGER.debug("onKeyInput i:{}",i);
            if (QUICK_CAST_STATES.get(i).wasPressed()) {
                //IronsSpellbooks.LOGGER.debug("onKeyInput cast}");
                Utils.quickCast(i);
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Pre event) {
        handleRightClickSuppression(event.getButton(), event.getAction());
    }

    private static void handleRightClickSuppression(int button, int action) {
        //IronsSpellbooks.LOGGER.debug("ClientInputEvents.handleRightClickSuppression {} {}", button, action);
        if (useKeyId == Integer.MIN_VALUE) {
            useKeyId = Minecraft.getInstance().options.keyUse.getKey().getValue();
        }

        if (button == useKeyId) {
            if (action == InputMappings.RELEASE) {
                //IronsSpellbooks.LOGGER.debug("ClientInputEvents.handleRightClickSuppression.1");
                ClientSpellCastHelper.setSuppressRightClicks(false);
                isUseKeyDown = false;
                hasReleasedSinceCasting = true;
            } else if (action == InputMappings.PRESS) {
                //IronsSpellbooks.LOGGER.debug("ClientInputEvents.handleRightClickSuppression.2");
                isUseKeyDown = true;
            }
        }
    }

    private static void update() {
        for (KeyState k : KEY_STATES) {
            k.update();
        }
    }

    private static KeyState register(KeyBinding key) {
        KeyState k = new KeyState(key);
        KEY_STATES.add(k);
        return k;
    }

    private static List<KeyState> registerQuickCast(List<KeyBinding> mappings) {
        ArrayList<KeyState> keyStates = new ArrayList<KeyState>();

        mappings.forEach(keyMapping -> {
            KeyState k = new KeyState(keyMapping);
            KEY_STATES.add(k);
            keyStates.add(k);
        });

        return keyStates;
    }
}
