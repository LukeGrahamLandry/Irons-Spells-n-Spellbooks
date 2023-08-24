package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;

public class ReplacedFireballRenderer extends FireballRenderer {
    SpriteRenderer<AbstractFireballEntity> backupRenderer;

    public ReplacedFireballRenderer(EntityRendererProvider.Context context, float scale, float backupScale) {
        super(context, scale);
        backupRenderer = new SpriteRenderer<AbstractFireballEntity>(context, backupScale, true);
    }

    @Override
    public void render(ProjectileEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        if (entity instanceof FireballEntity && ClientConfigs.REPLACE_GHAST_FIREBALL.get() || entity instanceof SmallFireballEntity && ClientConfigs.REPLACE_BLAZE_FIREBALL.get())
            super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
        else
            backupRenderer.render((AbstractFireballEntity) entity, yaw, partialTicks, poseStack, bufferSource, light);
    }


}
