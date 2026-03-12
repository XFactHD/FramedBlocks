package io.github.xfacthd.framedblocks.client.model.geometry.slopeslab;

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

public class FramedCompoundSlopeSlabGeometry extends Geometry
{
    private final Direction dir;
    private final boolean altSlope;

    public FramedCompoundSlopeSlabGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, .5F))
                    .export(quadMap, quadDir);

            if (!altSlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(false, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.DOWN, .5F))
                        .export(quadMap, null);
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, .5F))
                    .export(quadMap, quadDir);

            if (!altSlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.makeVerticalSlope(true, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.UP, .5F))
                        .export(quadMap, null);
            }
        }
        else if (altSlope && DirUtils.isY(quadDir))
        {
            Direction edge = quadDir == Direction.UP ? dir.getOpposite() : dir;
            QuadModifier.of(quad)
                    .apply(Modifiers.makeVerticalSlope(edge, FramedSlopeSlabGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap, null);
        }
        else if (quadDir.getAxis() == dir.getClockWise().getAxis())
        {
            boolean cw = quadDir == dir.getClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, cw ? .5F : 1F, cw ? 1F : .5F))
                    .apply(Modifiers.cut(Direction.DOWN, cw ? 1F : .5F, cw ? .5F : 1F))
                    .export(quadMap, quadDir);
        }
    }
}
