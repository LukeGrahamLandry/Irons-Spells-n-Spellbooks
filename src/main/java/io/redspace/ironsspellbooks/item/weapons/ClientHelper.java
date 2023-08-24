package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.render.SpecialItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

import java.util.concurrent.Callable;

public class ClientHelper {
    public static Callable<ItemStackTileEntityRenderer> getISTER(String name) {
        return () -> new SpecialItemRenderer(Minecraft.getInstance().getItemRenderer(), name);
    }
}
