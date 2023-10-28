package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BloodGroundParticle extends SpriteTexturedParticle {

    public BloodGroundParticle(ClientWorld level, double xCoord, double yCoord, double zCoord, IAnimatedSprite spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        //this.friction = 1.0f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1f;
        this.scale(1 + (float) Math.random());
        this.lifetime = 40 + (int) (Math.random() * 41);
        this.gravity = 1.0F;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public Provider(IAnimatedSprite spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(BasicParticleType particleType, ClientWorld level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new BloodGroundParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
