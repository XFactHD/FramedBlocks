package io.github.xfacthd.framedblocks.client.model.quadmap;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public sealed interface QuadMapBuilderInternal extends QuadMapBuilder permits QuadMapImpl
{
    @Nullable
    ArrayList<BakedQuad> tryGet(@Nullable Direction side);

    void set(@Nullable Direction side, List<BakedQuad> list);

    boolean isEmpty();

    QuadMap build();

    static QuadMapBuilderInternal create()
    {
        return new QuadMapImpl();
    }
}
