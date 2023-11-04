package io.redspace.ironsspellbooks.entity.spells.creeper_head;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.projectile.WitherSkullEntity;

public class CreeperHeadRenderer extends WitherSkullRenderer {
    ResourceLocation TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/creeper_head.png");

    public CreeperHeadRenderer(EntityRendererManager pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(WitherSkullEntity pEntity) {
        return TEXTURE;
    }
}
