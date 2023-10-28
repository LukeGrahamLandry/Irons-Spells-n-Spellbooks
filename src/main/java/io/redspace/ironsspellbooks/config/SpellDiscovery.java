package io.redspace.ironsspellbooks.config;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.util.*;

public final class SpellDiscovery {
    public static List<AbstractSpell> getSpellsForConfig() {
        List<ModFileScanData> allScanData = ModList.get().getAllScanData();
        Set<String> spellClassNames = new HashSet<>();

        allScanData.forEach(scanData -> {
            scanData.getAnnotations().forEach(annotationData -> {
                if (Objects.equals(annotationData.annotationType(), Type.getType(AutoSpellConfig.class))) {
                    spellClassNames.add(annotationData.memberName());
                }
            });
        });

        List<AbstractSpell> spells = new ArrayList<>();
        spellClassNames.forEach(spellName -> {
            try {
                Class<?> pluginClass = Class.forName(spellName);
                Class<? extends AbstractSpell> pluginClassSubclass = pluginClass.asSubclass(AbstractSpell.class);
                Constructor<? extends AbstractSpell> constructor = pluginClassSubclass.getDeclaredConstructor();
                AbstractSpell instance = constructor.newInstance();
                spells.add(instance);
            } catch (Exception e) {
                IronsSpellbooks.LOGGER.error("SpellDiscovery:  {}, {}", spellName, e);
            }
        });

        return spells;
    }
}