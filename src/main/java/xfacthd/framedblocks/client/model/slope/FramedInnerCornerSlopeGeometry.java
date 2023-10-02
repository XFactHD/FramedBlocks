package xfacthd.framedblocks.client.model.slope;

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
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public class FramedInnerCornerSlopeGeometry implements Geometry
{
    private final Direction dir;
    private final CornerType type;
    private final boolean ySlope;

    public FramedInnerCornerSlopeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.CORNER_TYPE);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        if (type.isHorizontal())
        {
            createHorizontalCorner(quadMap, quad);
        }
        else
        {
            createVerticalCorner(quadMap, quad);
        }
    }

    private void createHorizontalCorner(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        boolean top = type.isTop();
        boolean right = type.isRight();

        if ((quadDir == Direction.UP && !top) || (quadDir == Direction.DOWN && top))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), right ? 0 : 1, right ? 1 : 0))
                    .export(quadMap.get(quadDir));

            if (ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir, right ? 0 : 1, right ? 1 : 0))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            Direction cutDir = right ? dir.getCounterClockWise() : dir.getClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? 0 : 1, top ? 1 : 0))
                    .apply(Modifiers.makeHorizontalSlope(right, 45))
                    .export(quadMap.get(null));

            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(top, right ? 1 : 0, right ? 0 : 1))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .export(quadMap.get(null));
            }
        }
        else if ((quadDir == dir.getClockWise() && !right) || (quadDir == dir.getCounterClockWise() && right))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));
        }
    }

    private void createVerticalCorner(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        boolean top = type.isTop();

        if (quadDir == dir.getClockWise())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir, top ? 0 : 1, top ? 1 : 0))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), top ? 0 : 1, top ? 1 : 0))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .export(quadMap.get(null));
            }
        }
        else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0, 1))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                    .export(quadMap.get(null));
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}