package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;

    public FireParticle(ClientWorld level, double xCoord, double yCoord, double zCoord, IAnimatedSprite spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, xd, yd, zd);


        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.scale(this.random.nextFloat() * 1.75f + 1f);
        this.friction -= this.random.nextFloat() * .1;
        this.lifetime = 10 + (int) (Math.random() * 25);
        sprites = spriteSet;
        this.setSpriteFromAge(spriteSet);
        this.gravity = -0.01F;


    }

    @Override
    public void tick() {
        super.tick();
        this.xd += this.random.nextFloat() / 500.0F * (float) (this.random.nextBoolean() ? 1 : -1);
        this.zd += this.random.nextFloat() / 500.0F * (float) (this.random.nextBoolean() ? 1 : -1);

        animateContinuously();

        if (this.random.nextFloat() <= .25f) {
            this.level.addParticle(ParticleHelper.EMBERS, this.x, this.y, this.z, this.xd, this.yd, this.zd);
        }
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
            return new FireParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    @Override
    public int getLightColor(float p_107564_) {
        return LightTexture.FULL_BRIGHT;
    }

    private void animateContinuously() {
        if (age % 8 == 0)
            setSprite(sprites.get(this.random));
    }
//
//    @Override
//    public float getQuadSize(float p_107567_) {
//        float f = ((float) this.age + p_107567_) / (float) this.lifetime;
//        f = 1.0F - f;
//        f *= f;
//        f = 1.0F - f;
//        return this.quadSize * f;
//    }
}
