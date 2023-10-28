package io.redspace.ironsspellbooks.entity.spells.shield;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class ShieldRenderer extends EntityRenderer<ShieldEntity> implements IEntityRenderer<ShieldEntity, ShieldModel> {

    public static ResourceLocation SPECTRAL_OVERLAY_TEXTURE = IronsSpellbooks.id("textures/entity/shield/shield_overlay.png");
    private static ResourceLocation SIGIL_TEXTURE = IronsSpellbooks.id("textures/block/scroll_forge_sigil.png");
    //private static ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final ShieldModel model;
    protected final List<LayerRenderer<ShieldEntity, ShieldModel>> layers = new ArrayList<>();

    public ShieldRenderer(Context context) {
        super(context);
        this.model = new ShieldModel(context.bakeLayer(ShieldModel.LAYER_LOCATION));
        layers.add(new ShieldTrimLayer(this, context));
    }

    @Override
    public void render(ShieldEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();

        Entry pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-entity.yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(entity.xRot));

        //VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        var offset = getEnergySwirlOffset(entity, partialTicks);
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.energySwirl(getTextureLocation(entity), offset.x, offset.y));

        float width = entity.width * .65f;
        poseStack.scale(width, width, width);
        RenderSystem.disableBlend();
        model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0.65F, 0.65F, 0.65F, 1.0F);


        for (LayerRenderer<ShieldEntity, ShieldModel> layer : layers) {
            layer.render(poseStack, bufferSource, light, entity, 0f, 0f, 0f, 0f, 0f, 0f);
        }
        
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    private static float shittyNoise(float f) {
        return (float) (Math.sin(f / 4) + 2 * Math.sin(f / 3) + 3 * Math.sin(f / 2) + 4 * Math.sin(f)) * .25f;
    }

    public static Vector2f getEnergySwirlOffset(ShieldEntity entity, float partialTicks, int offset) {
        float f = (entity.tickCount + partialTicks) * .02f;
        return new Vector2f(shittyNoise(1.2f * f + offset), shittyNoise(f + 456 + offset));
    }

    public static Vector2f getEnergySwirlOffset(ShieldEntity entity, float partialTicks) {
        return getEnergySwirlOffset(entity, partialTicks, 0);
    }

    @Override
    public ShieldModel getModel() {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureLocation(ShieldEntity entity) {
        return SPECTRAL_OVERLAY_TEXTURE;
    }

}