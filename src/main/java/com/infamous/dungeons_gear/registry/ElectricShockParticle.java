package com.infamous.dungeons_gear.registry;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


@OnlyIn(Dist.CLIENT)
public class ElectricShockParticle extends SpriteTexturedParticle {


    private ElectricShockParticle(ClientWorld clientWorld, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        this.xd *= 0.009999999776482582D;
        this.yd *= 0.009999999776482582D;
        this.zd *= 0.009999999776482582D;
        this.yd += 0.1D;
        this.quadSize *= 1.5F;
        this.lifetime = 16;
        this.hasPhysics = false;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public float getQuadSize(float scaleFactor) {
        return this.quadSize * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            if (this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }

            this.xd *= 0.8600000143051147D;
            this.yd *= 0.8600000143051147D;
            this.zd *= 0.8600000143051147D;
            if (this.onGround) {
                this.xd *= 0.699999988079071D;
                this.zd *= 0.699999988079071D;
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        Factory(IAnimatedSprite sprite){
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ElectricShockParticle shockParticle = new ElectricShockParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            shockParticle.setColor(1.0f, 1.0f, 1.0f);
            shockParticle.pickSprite(this.spriteSet);
            return shockParticle;
        }
    }
}
