package io.redspace.ironsspellbooks.entity.spells.guiding_bolt;

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

public class GuidingBoltRenderer extends EntityRenderer<GuidingBoltProjectile> {
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(IronsSpellbooks.MODID, "guiding_bolt_model"), "main");
    private static final ResourceLocation BASE_TEXTURE = IronsSpellbooks.id("textures/entity/guiding_bolt/guiding_bolt.png");
    private static final ResourceLocation[] FIRE_TEXTURES = {
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_1.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_2.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_3.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_4.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_5.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_6.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_7.png")
    };


    protected final ModelRenderer body;
    protected final ModelRenderer outline;

    public GuidingBoltRenderer(Context context) {
        super(context);
        ModelRenderer modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
        this.outline = modelpart.getChild("outline");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 5.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 16.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 48, 24);
    }

    @Override
    public void render(GuidingBoltProjectile entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        Vector3d motion = entity.getDeltaMovement();
        float xRot = -((float) (MathHelper.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (MathHelper.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.energySwirl(getTextureLocation(entity), 0, 0));
        this.body.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        consumer = bufferSource.getBuffer(RenderType.energySwirl(getFireTextureLocation(entity), 0, 0));
        poseStack.scale(0.4f, 0.4f, 0.4f);
        this.outline.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);


        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(GuidingBoltProjectile entity) {
        return BASE_TEXTURE;
    }

    public ResourceLocation getFireTextureLocation(ProjectileEntity entity) {
        int frame = (entity.tickCount) % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }
}