package io.redspace.ironsspellbooks.entity.spells.fireball;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
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

public class FireballRenderer extends EntityRenderer<ProjectileEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(IronsSpellbooks.MODID, "fireball_model"), "main");
    private static final ResourceLocation BASE_TEXTURE = IronsSpellbooks.id("textures/entity/fireball/magma.png");
    private static final ResourceLocation[] FIRE_TEXTURES = {
            IronsSpellbooks.id("textures/entity/fireball/fire_0.png"),
            IronsSpellbooks.id("textures/entity/fireball/fire_1.png"),
            IronsSpellbooks.id("textures/entity/fireball/fire_2.png"),
            IronsSpellbooks.id("textures/entity/fireball/fire_3.png"),
            IronsSpellbooks.id("textures/entity/fireball/fire_4.png"),
            IronsSpellbooks.id("textures/entity/fireball/fire_5.png"),
            IronsSpellbooks.id("textures/entity/fireball/fire_6.png"),
            IronsSpellbooks.id("textures/entity/fireball/fire_7.png")
    };


    protected final ModelRenderer body;
    protected final ModelRenderer outline;

    protected final float scale;

    public FireballRenderer(Context context, float scale) {
        super(context);
        ModelRenderer modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
        this.outline = modelpart.getChild("outline");
        this.scale = scale;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 16.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 48, 24);
    }

    @Override
    public void render(ProjectileEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        poseStack.scale(scale, scale, scale);
        Vector3d motion = entity.getDeltaMovement();
        float xRot = -((float) (MathHelper.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (MathHelper.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        this.body.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        float f = entity.tickCount + partialTicks;
//        float swirlX = Mth.cos(.08f * f) * 180;
//        float swirlY = Mth.sin(.08f * f) * 180;
//        float swirlZ = Mth.cos(.08f * f + 5464) * 180;
//        poseStack.mulPose(Vector3f.XP.rotationDegrees(swirlX));
//        poseStack.mulPose(Vector3f.YP.rotationDegrees(swirlY));
//        poseStack.mulPose(Vector3f.ZP.rotationDegrees(swirlZ));
//        int frameCount = 32;
//        float uv = 1f / frameCount;
//        int frame = (int) ((f) % frameCount);
        consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getFireTextureLocation(entity)));
        poseStack.scale(1.15f, 1.15f, 1.15f);
        this.outline.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);


        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(ProjectileEntity entity) {
        return BASE_TEXTURE;
    }

    public ResourceLocation getFireTextureLocation(ProjectileEntity entity) {
        int frame = (entity.tickCount) % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }
}