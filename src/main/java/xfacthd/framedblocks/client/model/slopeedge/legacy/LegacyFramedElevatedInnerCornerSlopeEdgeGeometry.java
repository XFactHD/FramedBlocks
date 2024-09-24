package xfacthd.framedblocks.client.model.slopeedge.legacy;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public class LegacyFramedElevatedInnerCornerSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final CornerType type;
    private final boolean ySlope;

    public LegacyFramedElevatedInnerCornerSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.CORNER_TYPE);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (type.isHorizontal())
        {
            boolean top = type.isTop();
            boolean right = type.isRight();
            Direction xBackFace = right ? dir.getClockWise() : dir.getCounterClockWise();
            Direction yBackFace = top ? Direction.UP : Direction.DOWN;
            if (quadDir == dir.getOpposite())
            {
                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(xBackFace, .5F))
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideUpDown(top, right ? 1F : 0F, right ? 0F : 1F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(xBackFace, .5F))
                            .apply(Modifiers.cutSideLeftRight(xBackFace.getOpposite(), top ? 0F : 1F, top ? 1F : 0F))
                            .apply(Modifiers.makeHorizontalSlope(right, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(xBackFace.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == xBackFace.getOpposite())
            {
                if (ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideUpDown(!top, right ? -.5F : .5F, right ? .5F : -.5F))
                            .apply(Modifiers.makeHorizontalSlope(!right, 45))
                            .apply(Modifiers.offset(xBackFace.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == yBackFace.getOpposite())
            {
                if (ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.cutTopBottom(xBackFace, .5F))
                            .apply(Modifiers.cutTopBottom(xBackFace, right ? .5F : -.5F, right ? -.5F : .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(xBackFace, .5F))
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), right ? .5F : 1.5F, right ? 1.5F : .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(xBackFace.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else
        {
            boolean top = type == CornerType.TOP;
            Direction topDir = top ? Direction.DOWN : Direction.UP;
            if (quadDir == dir.getOpposite())
            {
                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), top ? -.5F : .5F, top ? .5F : -.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir.getClockWise())
            {
                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir, top ? -.5F : .5F, top ? .5F : -.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == topDir)
            {
                if (ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cutTopBottom(dir, 0F, 1F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(topDir, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), 1F, 0F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(topDir, .5F))
                            .export(quadMap.get(null));
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .5F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
    }
}
