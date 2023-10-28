package io.redspace.ironsspellbooks.particle;

import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireflyParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;
    private boolean lit;
    private float litTween;
    private int litTimer;
    private float wander;
    private final float flickerIntensity;
    //private static final Vector3f litColor = new Vector3f(239 / 255f, 255 / 255f, 105 / 255f);
    private static final Vector3f litColor = new Vector3f(1f, 1f, 1f);
    //private static final Vector3f unlitColor = new Vector3f(45 / 255f, 41 / 255f, 36 / 255f);
    private static final Vector3f unlitColor = new Vector3f(22 / 255f, 20 / 255f, 18 / 255f);

    public FireflyParticle(ClientWorld level, double xCoord, double yCoord, double zCoord, IAnimatedSprite spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        //this.friction = 1.0f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.scale(2.5f);
        this.lifetime = 20 + (int) (Math.random() * 90);
        this.sprites = spriteSet;

        this.gravity = 0F;
        lit = Utils.random.nextBoolean();
        litTween = lit ? 1 : 0;
        wander = Utils.random.nextFloat() * 2.5f;
        wander *= wander * wander * wander;
        //this.setSprite(sprites.get(lit ? 0 : 1, 1));
        this.setSprite(sprites.get(0, 1));

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

        this.flickerIntensity = Utils.random.nextIntBetweenInclusive(18, 45) * .01f;
    }

    @Override
    public void tick() {
        float xj = (this.random.nextFloat() * .001f * wander * (this.random.nextBoolean() ? 1 : -1));
        float yj = (this.random.nextFloat() * .001f * wander * (this.random.nextBoolean() ? 1 : -1)) + .00025f;
        float zj = (this.random.nextFloat() * .001f * wander * (this.random.nextBoolean() ? 1 : -1));
        wander *= .98f;
        if (onGround) {
            yd = Math.abs(yd);
        }
        this.xd += xj;
        this.yd += yj;
        this.zd += zj;
        if (--litTimer <= 0) {
            lit = !lit;
            litTimer = random.nextIntBetweenInclusive(5, 20);
            //this.setSprite(sprites.get(lit ? 0 : 1, 1));
        }
        if (lit) {
            litTween = MathHelper.lerp(flickerIntensity, litTween, 1);
        } else {
            litTween = MathHelper.lerp(flickerIntensity, litTween, 0);
        }
        this.rCol = MathHelper.lerp(litTween, unlitColor.x(), litColor.x());
        this.gCol = MathHelper.lerp(litTween, unlitColor.y(), litColor.y());
        this.bCol = MathHelper.lerp(litTween, unlitColor.z(), litColor.z());
        if (age >= lifetime - 1 && litTween >.1f) {
            //they are only allowed to die if they have faded out
            lifetime++;
        }
        super.tick();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    protected int getLightColor(float pPartialTick) {

        return LightTexture.FULL_BRIGHT;
//        float litColor = LightTexture.FULL_BRIGHT;
//        float darkColor = super.getLightColor(pPartialTick);
//
//        return (int) Mth.lerp(litTween, darkColor, litColor);
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
            return new FireflyParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }

    }
}
