package io.github.xfacthd.framedblocks.client.model.geometry.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedSlabGeometry extends Geometry
{
    private final boolean top;

    public FramedSlabGeometry(GeometryFactory.Context ctx)
    {
        this.top = ctx.state().getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if ((top && quadDir == Direction.DOWN) || (!top && quadDir == Direction.UP))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }
        else if (!DirUtils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                    .export(quadMap, quadDir);
        }
    }
}