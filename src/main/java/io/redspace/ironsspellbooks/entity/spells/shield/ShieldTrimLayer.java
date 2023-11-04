package io.redspace.ironsspellbooks.entity.spells.shield;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;

public class ShieldTrimLayer extends LayerRenderer<ShieldEntity, ShieldModel> {
    private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/shield/shield_trim.png");
    private final ShieldTrimModel model;

    public ShieldTrimLayer(IEntityRenderer<ShieldEntity, ShieldModel> renderer, EntityRendererManager context) {
        super(renderer);
        this.model = new ShieldTrimModel(context.bakeLayer(ShieldTrimModel.LAYER_LOCATION));
    }

    @Override
    public void render(MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight, ShieldEntity entity, float pLimbSwing, float pLimbSwingAmount, float partialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Vector2f offset = ShieldRenderer.getEnergySwirlOffset(entity, partialTicks, 3456);
        //VertexConsumer consumer = bufferSource.getBuffer(RenderType.energySwirl(getTextureLocation(entity), offset.x, offset.y));
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.energySwirl(TEXTURE,0,0));
        model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, .45f);
    }
}
