package io.redspace.ironsspellbooks.entity.spells.magic_missile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.vector.Vector3d;

public class MagicMissileRenderer extends EntityRenderer<MagicMissileProjectile> {
    //private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/magic_missile_projectile.png");
    private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/magic_missile/magic_missile.png");
    private final static ResourceLocation FIRE_TEXTURES[] = {
            IronsSpellbooks.id("textures/entity/magic_missile/fire_1.png"),
            IronsSpellbooks.id("textures/entity/magic_missile/fire_2.png"),
            IronsSpellbooks.id("textures/entity/magic_missile/fire_3.png"),
            IronsSpellbooks.id("textures/entity/magic_missile/fire_4.png")
    };
    private final ModelRenderer body;
    protected final ModelRenderer outline;

    public MagicMissileRenderer(Context context) {
        super(context);
        ModelRenderer modelpart = context.bakeLayer(FireballRenderer.MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
        this.outline = modelpart.getChild("outline");

    }

    @Override
    public void render(MagicMissileProjectile entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();
        //poseStack.scale(.6f, .6f, .6f);
        //poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        Vector3d motion = entity.getDeltaMovement();
        float xRot = -((float) (MathHelper.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (MathHelper.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        poseStack.scale(0.35f, 0.35f, 0.45f);

        //poseStack.mulPose(Vector3f.ZP.rotationDegrees((entity.tickCount + partialTicks) * 40));

        IVertexBuilder consumer = bufferSource.getBuffer(renderType(getTextureLocation(entity)));
        this.body.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, .8f, .8f, .8f, 1f);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.translate(0, 0, .4f);
        consumer = bufferSource.getBuffer(renderType(getFireTextureLocation(entity)));
        this.outline.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, .8f, .8f, .8f, 1f);


        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public RenderType renderType(ResourceLocation TEXTURE) {
        return RenderType.energySwirl(TEXTURE, 0, 0);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicMissileProjectile entity) {
        return TEXTURE;
    }


    public ResourceLocation getFireTextureLocation(ProjectileEntity entity) {
        int frame = (entity.tickCount) % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }
}