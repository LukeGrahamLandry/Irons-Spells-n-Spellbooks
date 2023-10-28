package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.render.SpecialItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

import net.minecraft.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;


public class TestClaymoreItem extends ExtendedSwordItem {
    public TestClaymoreItem() {
        super(ExtendedWeaponTiers.CLAYMORE, 9, -2.7, Map.of(), new Item.Properties().stacksTo(1));
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public ItemStackTileEntityRenderer getCustomRenderer() {
                return new SpecialItemRenderer(Minecraft.getInstance().getItemRenderer(),
                        Minecraft.getInstance().getEntityModels(),
                        "claymore");
            }
        });
    }
}
