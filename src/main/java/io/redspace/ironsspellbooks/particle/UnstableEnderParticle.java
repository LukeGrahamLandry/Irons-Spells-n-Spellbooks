package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class UnstableEnderParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;

    public UnstableEnderParticle(ClientWorld level, double xCoord, double yCoord, double zCoord, IAnimatedSprite spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, xd, yd, zd);


        this.friction = .77f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.15F + 0.3F);
        this.scale(2.25f);
        this.lifetime = 5 + (int) (Math.random() * 25);
        sprites = spriteSet;
        this.gravity = 0.0F;
        randomlyAnimate();

        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = f * 0.9F;
        this.gCol = f * 0.3F;
        this.bCol = f;

    }

    @Override
    public void tick() {
        super.tick();
        float xj = (this.random.nextFloat() / 50F * (float) (this.random.nextBoolean() ? 1 : -1));
        float yj = (this.random.nextFloat() / 50f * (float) (this.random.nextBoolean() ? 1 : -1));
        float zj = (this.random.nextFloat() / 50f * (float) (this.random.nextBoolean() ? 1 : -1));
        setPos(x + xj, y + yj, z + zj);
    }
    private void randomlyAnimate() {
        setSprite(sprites.get(level.random));
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
            return new UnstableEnderParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    @Override
    public int getLightColor(float p_107564_) {
        int i = super.getLightColor(p_107564_);
        float f = (float) this.age / (float) this.lifetime;
        f *= f;
        f *= f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k += (int) (f * 15.0F * 16.0F);
        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    @Override
    public float getQuadSize(float p_107567_) {
        float f = ((float) this.age + p_107567_) / (float) this.lifetime;
        f = 1.0F - f;
        f *= f;
        f = 1.0F - f;
        return this.quadSize * f;
    }
}
