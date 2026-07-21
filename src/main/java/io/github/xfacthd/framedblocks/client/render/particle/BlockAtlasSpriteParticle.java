package io.github.xfacthd.framedblocks.client.render.particle;

import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public class BlockAtlasSpriteParticle extends SingleQuadParticle {
    private final SingleQuadParticle.Layer layer;
    protected final BlockPos pos;
    private final float uo;
    private final float vo;

    public BlockAtlasSpriteParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            BlockPos pos,
            Identifier sprite,
            int tintColor
    ) {
        this(level, x, y, z, xSpeed, ySpeed, zSpeed, pos, ClientUtils.getBlockSprite(sprite), tintColor);
    }

    public BlockAtlasSpriteParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            BlockPos pos,
            TextureAtlasSprite sprite,
            int tintColor
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
        this.pos = pos;
        this.gravity = 1F;
        this.quadSize /= 2F;
        this.uo = random.nextFloat() * 3F;
        this.vo = random.nextFloat() * 3F;
        this.rCol = .6F * (float) (tintColor >> 16 & 0xFF) / 255F;
        this.gCol = .6F * (float) (tintColor >> 8 & 0xFF) / 255F;
        this.bCol = .6F * (float) (tintColor & 0xFF) / 255F;
        this.layer = SingleQuadParticle.Layer.bySprite(this.sprite);
    }

    @Override
    public Layer getLayer() {
        return layer;
    }

    @Override
    protected float getU0() {
        return sprite.getU((uo + 1.0F) / 4.0F);
    }

    @Override
    protected float getU1() {
        return sprite.getU(uo / 4.0F);
    }

    @Override
    protected float getV0() {
        return sprite.getV(vo / 4.0F);
    }

    @Override
    protected float getV1() {
        return sprite.getV((vo + 1.0F) / 4.0F);
    }
}
