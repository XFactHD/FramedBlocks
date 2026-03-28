package io.github.xfacthd.framedblocks.client.model.quadmap;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract sealed class QuadMap permits QuadMapImpl {
    public abstract List<BakedQuad> get(@Nullable Direction cullFace);

    public abstract int materialFlags();
}
