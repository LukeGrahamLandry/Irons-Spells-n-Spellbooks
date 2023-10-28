package io.redspace.ironsspellbooks.entity.mobs.necromancer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class NecromancerRenderer extends AbstractSpellCastingMobRenderer {

    public NecromancerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NecromancerModel());
    }

    @Override
    public RenderType getRenderType(AbstractSpellCastingMob animatable, float partialTick, MatrixStack poseStack, @Nullable IRenderTypeBuffer bufferSource, @Nullable IVertexBuilder buffer, int packedLight, ResourceLocation texture) {
        return animatable.isInvisible() ? RenderType.entityTranslucent(texture) : RenderType.entityCutoutNoCull(texture);
    }
}
