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

public class FramedSlopeSlabGeometry extends Geometry
{
    public static final float SLOPE_ANGLE = (float) (90D - Math.toDegrees(Math.atan(.5)));
    public static final float SLOPE_ANGLE_VERT = (float) Math.toDegrees(Math.atan(.5));
    private final Direction facing;
    private final boolean top;
    private final boolean topHalf;
    private final boolean altSlope;

    public FramedSlopeSlabGeometry(GeometryFactory.Context ctx)
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

        if (!altSlope && face == facing.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeVerticalSlope(!top, SLOPE_ANGLE))
                    .applyIf(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F), offset)
                    .export(quadMap, null);
        }
        else if (altSlope && ((!top && face == Direction.UP) || (top && face == Direction.DOWN)))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeVerticalSlope(facing.getOpposite(), SLOPE_ANGLE_VERT))
                    .applyIf(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F), !offset)
                    .export(quadMap, null);
        }
        else if (face == facing)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(topHalf ? Direction.DOWN : Direction.UP, .5F))
                    .export(quadMap, face);
        }
        else if (face == facing.getClockWise() || face == facing.getCounterClockWise())
        {
            boolean rightFace = face == facing.getClockWise();
            float right = rightFace ? (offset ? .5F : 0) : (offset ? 1 : .5F);
            float left =  rightFace ? (offset ? 1 : .5F) : (offset ? .5F : 0);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, right, left))
                    .applyIf(Modifiers.cut(top ? Direction.UP : Direction.DOWN, .5F), offset)
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
