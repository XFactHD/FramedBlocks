package io.github.xfacthd.framedblocks.client.model.geometry.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedFlatInnerSlopeSlabCornerGeometry extends Geometry
{
    private final Direction facing;
    private final boolean top;
    private final boolean topHalf;
    private final boolean altSlope;

    public FramedFlatInnerSlopeSlabCornerGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.topHalf = ctx.state().getValue(PropertyHolder.TOP_HALF);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction face = quad.direction();
        boolean offset = top != topHalf;

        if (face == facing.getOpposite() || face == facing.getClockWise())
        {
            if (!altSlope)
            {
                Direction cutDir = face != facing.getClockWise() ? face.getClockWise() : face.getCounterClockWise();
                float lenTop = top ? 0F : 1F;
                float lenBot = top ? 1F : 0F;

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(cutDir, lenTop, lenBot))
                        .apply(Modifiers.makeVerticalSlope(!top, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .applyIf(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F), offset)
                        .export(quadMap, null);
            }

            boolean rightFace = face == facing.getClockWise();
            float lenRight = rightFace ? (offset ? .5F : 0) : (offset ? 1 : .5F);
            float lenLeft =  rightFace ? (offset ? 1 : .5F) : (offset ? .5F : 0);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, lenRight, lenLeft))
                    .applyIf(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F), offset)
                    .export(quadMap, face);
        }
        else if (altSlope && ((!top && face == Direction.UP) || (top && face == Direction.DOWN)))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getCounterClockWise(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(facing.getOpposite(), FramedSlopeSlabGeometry.SLOPE_ANGLE_VERT))
                    .applyIf(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F), !offset)
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, 0, 1))
                    .apply(Modifiers.makeVerticalSlope(facing.getClockWise(), FramedSlopeSlabGeometry.SLOPE_ANGLE_VERT))
                    .applyIf(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F), !offset)
                    .export(quadMap, null);
        }
        else if (face == facing || face == facing.getCounterClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(topHalf ? Direction.DOWN : Direction.UP, .5F))
                    .export(quadMap, face);
        }
        else if ((top && !topHalf && face == Direction.UP) || (!top && topHalf && face == Direction.DOWN))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }
    }
}
