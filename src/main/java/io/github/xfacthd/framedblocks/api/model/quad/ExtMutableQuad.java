package io.github.xfacthd.framedblocks.api.model.quad;

import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.neoforged.neoforge.client.model.quad.MutableQuad;

/// Extended mutable quad storing additional properties required for quad cutting.
public final class ExtMutableQuad extends MutableQuad {
    private boolean uvRotated = false;

    /// {@return whether the UVs of this quad are rotated}
    public boolean uvRotated() {
        return uvRotated;
    }

    @Override
    public MutableQuad setFrom(BakedQuad quad) {
        super.setFrom(quad);
        uvRotated = ModelUtils.isQuadRotated(this);
        return this;
    }

    @Override
    public MutableQuad reset() {
        super.reset();
        uvRotated = false;
        return this;
    }

    @Override
    public ExtMutableQuad copy() {
        return copyInto(new ExtMutableQuad());
    }

    /// Copy this quad's data into the given quad and return the target quad.
    ///
    /// @param dest The quad to copy into
    /// @return the target quad
    public ExtMutableQuad copyInto(ExtMutableQuad dest) {
        super.copyInto(dest);
        dest.uvRotated = uvRotated;
        return dest;
    }
}
