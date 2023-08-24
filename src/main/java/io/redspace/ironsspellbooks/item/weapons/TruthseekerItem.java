package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.registries.AttributeRegistry;
import io.redspace.ironsspellbooks.render.SpecialItemRenderer;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;


import net.minecraft.item.Item.Properties;

public class TruthseekerItem extends ExtendedSwordItem {
    public TruthseekerItem() {
        super(ExtendedWeaponTiers.TRUTHSEEKER, 11, -3, Map.of(), new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public ItemStackTileEntityRenderer getCustomRenderer() {
                return new SpecialItemRenderer(Minecraft.getInstance().getItemRenderer(),
                        Minecraft.getInstance().getEntityModels(),
                        "truthseeker");
            }
        });
    }
}
