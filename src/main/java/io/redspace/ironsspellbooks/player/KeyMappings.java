package io.redspace.ironsspellbooks.player;

import net.minecraft.client.util.InputMappings;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IronsSpellbooks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class KeyMappings {
    public static final String KEY_BIND_GENERAL_CATEGORY = "key.irons_spellbooks.group_1";
    public static final String KEY_BIND_QUICK_CAST_CATEGORY = "key.irons_spellbooks.group_2";
    public static final KeyBinding SPELL_WHEEL_KEYMAP = new KeyBinding(getResourceName("spell_wheel"), KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_BIND_GENERAL_CATEGORY);
    public static final KeyBinding SPELLBAR_SCROLL_MODIFIER_KEYMAP = new KeyBinding(getResourceName("spell_bar_modifier"), KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, KEY_BIND_GENERAL_CATEGORY);

    public static final List<KeyBinding> QUICK_CAST_MAPPINGS = createQuickCastKeybinds();

    private static String getResourceName(String name) {
        return String.format("key.irons_spellbooks.%s", name);
    }

    @SubscribeEvent
    public static void onRegisterKeybinds(FMLClientSetupEvent event) {
 //Ironsspellbooks.logger.debug("KeyMappings.onRegisterKeybinds");
        ClientRegistry.registerKeyBinding(SPELL_WHEEL_KEYMAP);
        ClientRegistry.registerKeyBinding(SPELLBAR_SCROLL_MODIFIER_KEYMAP);
        QUICK_CAST_MAPPINGS.forEach(ClientRegistry::registerKeyBinding);
    }

    private static List<KeyBinding> createQuickCastKeybinds() {
        ArrayList<KeyBinding> qcm = new ArrayList<KeyBinding>();
        for (int i = 1; i <= 15; i++) {
            qcm.add(new KeyBinding(getResourceName(String.format("spell_quick_cast_%d", i)), KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, InputMappings.UNKNOWN.getValue(), KEY_BIND_QUICK_CAST_CATEGORY));
        }
        return qcm;
    }
}
