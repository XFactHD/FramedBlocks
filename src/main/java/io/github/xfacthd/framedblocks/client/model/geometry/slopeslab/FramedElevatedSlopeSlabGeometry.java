package io.github.xfacthd.framedblocks.client.model.geometry.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedElevatedSlopeSlabGeometry extends Geometry
{
    private final Direction facing;
    private final boolean top;
    private final boolean altSlope;

    public FramedElevatedSlopeSlabGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction face = quad.direction();
        if (face == facing.getOpposite())
        {
            if (!altSlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(!top, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                        .export(quadMap, null);
            }

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                    .export(quadMap, face);
        }
        else if (altSlope && ((!top && face == Direction.UP) || (top && face == Direction.DOWN)))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeVerticalSlope(facing.getOpposite(), FramedSlopeSlabGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap, null);
        }
        else if (face == facing.getClockWise() || face == facing.getCounterClockWise())
        {
            boolean rightFace = face == facing.getClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, rightFace ? .5F : 1, rightFace ? 1 : .5F))
                    .export(quadMap, face);
        }
    }
}
