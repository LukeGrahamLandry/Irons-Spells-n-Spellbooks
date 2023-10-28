package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AcidParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;

    public AcidParticle(ClientWorld level, double xCoord, double yCoord, double zCoord, IAnimatedSprite spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        //this.friction = 1.0f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1f;
        this.scale(1.15f);
        this.lifetime = 5 + (int) (Math.random() * 25);
        sprites = spriteSet;
        this.gravity = 0.35F;
        this.setSpriteFromAge(spriteSet);

        float f = this.random.nextFloat() * .3f + .7f;
        this.rCol = .41f * f;
        this.gCol = .88f * f;
        this.bCol = .22f * f;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
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
            return new AcidParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
