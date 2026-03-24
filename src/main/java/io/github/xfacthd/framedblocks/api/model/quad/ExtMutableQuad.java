package io.github.xfacthd.framedblocks.api.model.quad;

import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.neoforged.neoforge.client.model.quad.MutableQuad;

public final class ExtMutableQuad extends MutableQuad
{
    private boolean uvRotated = false;

    public boolean uvRotated()
    {
        return uvRotated;
    }

    @Override
    public MutableQuad setFrom(BakedQuad quad)
    {
        super.setFrom(quad);
        uvRotated = ModelUtils.isQuadRotated(this);
        return this;
    }

    @Override
    public MutableQuad reset()
    {
        super.reset();
        uvRotated = false;
        return this;
    }

    @Override
    public ExtMutableQuad copy()
    {
        return copyInto(new ExtMutableQuad());
    }

    public ExtMutableQuad copyInto(ExtMutableQuad dest)
    {
        super.copyInto(dest);
        dest.uvRotated = uvRotated;
        return dest;
    }
}
