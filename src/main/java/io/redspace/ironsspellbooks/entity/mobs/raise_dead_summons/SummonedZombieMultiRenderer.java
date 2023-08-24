package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.GeoHumanoidRenderer;
import io.redspace.ironsspellbooks.render.SpellTargetingLayer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.monster.ZombieEntity;

public class SummonedZombieMultiRenderer extends GeoHumanoidRenderer<SummonedZombie> {
    ZombieRenderer vanillaRenderer;
    public SummonedZombieMultiRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SummonedZombieModel());
        vanillaRenderer = new ZombieRenderer(pContext) {
            @Override
            public ResourceLocation getTextureLocation(ZombieEntity pEntity) {
                return SummonedZombieModel.TEXTURE;
            }
        };
        vanillaRenderer.addLayer(new SpellTargetingLayer.Vanilla<>(vanillaRenderer));
    }

    @Override
    public void render(SummonedZombie entity, float pEntityYaw, float pPartialTick, MatrixStack pPoseStack, IRenderTypeBuffer pBuffer, int pPackedLight) {
        if (entity.isAnimatingRise())
            super.render(entity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        else
            vanillaRenderer.render(entity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
}