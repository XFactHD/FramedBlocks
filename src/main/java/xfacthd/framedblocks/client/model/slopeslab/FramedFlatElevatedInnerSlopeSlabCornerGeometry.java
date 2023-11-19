package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;

public class FramedFlatElevatedInnerSlopeSlabCornerGeometry implements Geometry
{
    private final Direction facing;
    private final boolean top;
    private final boolean ySlope;

    public FramedFlatElevatedInnerSlopeSlabCornerGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();

        if (face == facing.getOpposite() || face == facing.getClockWise())
        {
            if (!ySlope)
            {
                boolean right = face != facing.getClockWise();
                float lenTop = top ? 0F : 1F;
                float lenBot = top ? 1F : 0F;

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(right, lenTop, lenBot))
                        .apply(Modifiers.makeVerticalSlope(!top, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                        .export(quadMap.get(null));
            }

            boolean rightFace = face == facing.getOpposite();
            float lenRight = rightFace ? 1 : .5F;
            float lenLeft =  rightFace ? .5F : 1;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, lenRight, lenLeft))
                    .export(quadMap.get(face));
        }
        else if (ySlope && ((!top && face == Direction.UP) || (top && face == Direction.DOWN)))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(facing.getOpposite(), FramedSlopeSlabGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing, 0, 1))
                    .apply(Modifiers.makeVerticalSlope(facing.getClockWise(), FramedSlopeSlabGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.value()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
