package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ElectricityParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;

    public ElectricityParticle(ClientWorld level, double xCoord, double yCoord, double zCoord, IAnimatedSprite spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, xd, yd, zd);


        this.friction = .77f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1f;
        this.scale(1.5f);
        this.lifetime = 5 + (int) (Math.random() * 15);
        sprites = spriteSet;
        this.gravity = 0.0F;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

    }

    @Override
    public void tick() {
        super.tick();
        float xj = (this.random.nextFloat() / 12.0F * (float) (this.random.nextBoolean() ? 1 : -1));
        float yj = (this.random.nextFloat() / 12.0F * (float) (this.random.nextBoolean() ? 1 : -1));
        float zj = (this.random.nextFloat() / 12.0F * (float) (this.random.nextBoolean() ? 1 : -1));
        setPos(x + xj, y + yj, z + zj);
        randomlyAnimate();
    }

    private void randomlyAnimate() {
        setSprite(sprites.get(level.random));
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            return new ElectricityParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BRIGHT;
    }
}
