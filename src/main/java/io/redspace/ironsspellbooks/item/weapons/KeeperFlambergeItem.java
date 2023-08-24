package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.registries.AttributeRegistry;
import io.redspace.ironsspellbooks.render.SpecialItemRenderer;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;


import net.minecraft.item.Item.Properties;

public class KeeperFlambergeItem extends ExtendedSwordItem {
    //TODO: custom tier
    public KeeperFlambergeItem() {
        super(ExtendedWeaponTiers.KEEPER_FLAMBERGE, 10, -2.7, Map.of(Attributes.ARMOR, new AttributeModifier(UUID.fromString("c552273e-6669-4cd2-80b3-a703b7616336"), "weapon mod", 5, AttributeModifier.Operation.ADDITION)), new Properties().stacksTo(1).rarity(Rarity.UNCOMMON).tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB));
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public ItemStackTileEntityRenderer getCustomRenderer() {
                return new SpecialItemRenderer(Minecraft.getInstance().getItemRenderer(),
                        Minecraft.getInstance().getEntityModels(),
                        "keeper_flamberge");
            }
        });
    }
}
