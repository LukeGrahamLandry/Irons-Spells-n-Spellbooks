package io.redspace.ironsspellbooks.compat.tetra.effects;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.network.ClientboundSyncMana;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.StatsHelper;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterPercentage;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import static io.redspace.ironsspellbooks.api.registry.AttributeRegistry.MAX_MANA;

public class ManaSiphonTetraEffect {
    public static final ItemEffect manaSiphon = ItemEffect.get(IronsSpellbooks.MODID + ":mana_siphon");
    public static final String siphonName = IronsSpellbooks.MODID + ".tetra_effect.mana_siphon";
    public static final String siphonTooltip = IronsSpellbooks.MODID + ".tetra_effect.mana_siphon.tooltip";

    @OnlyIn(Dist.CLIENT)
    public static void addGuiBars() {
        final IStatGetter effectStatGetter = new StatGetterEffectLevel(manaSiphon, 1);
        final GuiStatBar effectBar = new GuiStatBar(0, 0, StatsHelper.barLength, siphonName, 0, 30, false, effectStatGetter, LabelGetterBasic.percentageLabel,
                new TooltipGetterPercentage(siphonTooltip, effectStatGetter));
        WorkbenchStatsGui.addBar(effectBar);
        HoloStatsGui.addBar(effectBar);
    }

    public static void handleLivingAttackEvent(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        if (attacker instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) attacker;
            ItemStack heldStack = player.getMainHandItem();
            if (heldStack.getItem() instanceof ModularItem) {
                ModularItem item = (ModularItem) heldStack.getItem();
                int level = item.getEffectLevel(heldStack, manaSiphon);
                if (level > 0) {
                    level *= .01f;
                    int increment = (int) Math.min(level * event.getAmount(), 50);
                    int maxMana = (int) player.getAttributeValue(MAX_MANA.get());
                    MagicData playerMagicData = MagicData.getPlayerMagicData(player);
                    int newMana = Math.min(increment + playerMagicData.getMana(), maxMana);
                    playerMagicData.setMana(newMana);
                    Messages.sendToPlayer(new ClientboundSyncMana(playerMagicData), player);
                }
            }
        }
    }
}
