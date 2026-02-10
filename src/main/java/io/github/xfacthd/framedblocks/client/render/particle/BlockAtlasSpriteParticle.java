package io.github.xfacthd.framedblocks.client.render.particle;

import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public abstract class BlockAtlasSpriteParticle extends SingleQuadParticle
{
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
            Identifier sprite
    )
    {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, ClientUtils.getBlockSprite(sprite));
        this.pos = BlockPos.containing(x, y, z);
        this.gravity = 1F;
        this.quadSize /= 2F;
        this.uo = random.nextFloat() * 3F;
        this.vo = random.nextFloat() * 3F;
    }

    @Override
    public Layer getLayer()
    {
        return Layer.TERRAIN;
    }

    @Override
    protected float getU0()
    {
        return sprite.getU((uo + 1.0F) / 4.0F);
    }

    @Override
    protected float getU1()
    {
        return sprite.getU(uo / 4.0F);
    }

    @Override
    protected float getV0()
    {
        return sprite.getV(vo / 4.0F);
    }

    @Override
    protected float getV1()
    {
        return sprite.getV((vo + 1.0F) / 4.0F);
    }
}
