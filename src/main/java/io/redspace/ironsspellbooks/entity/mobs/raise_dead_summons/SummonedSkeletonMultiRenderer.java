package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.GeoHumanoidRenderer;
import io.redspace.ironsspellbooks.render.SpellTargetingLayer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import org.jetbrains.annotations.Nullable;

public class SummonedSkeletonMultiRenderer extends GeoHumanoidRenderer<SummonedSkeleton> {
    SkeletonRenderer vanillaRenderer;
    public static final ResourceLocation TEXTURE_ALT = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/summoned_skeleton_alt.png");

    public SummonedSkeletonMultiRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SummonedSkeletonModel());
        vanillaRenderer = new SkeletonRenderer(pContext) {
            @Override
            public ResourceLocation getTextureLocation(AbstractSkeletonEntity pEntity) {
                return TEXTURE_ALT;
            }
        };
        vanillaRenderer.addLayer(new SpellTargetingLayer.Vanilla<>(vanillaRenderer));
    }

    @Override
    public void render(SummonedSkeleton entity, float pEntityYaw, float pPartialTick, MatrixStack pPoseStack, IRenderTypeBuffer pBuffer, int pPackedLight) {
        if (entity.isAnimatingRise())
            super.render(entity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        else
            vanillaRenderer.render(entity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
    @Override
    public RenderType getRenderType(SummonedSkeleton animatable, float partialTick, MatrixStack poseStack, @Nullable IRenderTypeBuffer bufferSource, @Nullable IVertexBuilder buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}