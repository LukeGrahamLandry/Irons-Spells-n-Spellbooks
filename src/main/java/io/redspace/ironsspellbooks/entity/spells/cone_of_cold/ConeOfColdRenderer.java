package io.redspace.ironsspellbooks.entity.spells.cone_of_cold;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConeOfColdRenderer extends EntityRenderer<ConeOfColdProjectile> {
    public ConeOfColdRenderer(Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ConeOfColdProjectile entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}