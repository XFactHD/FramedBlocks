package xfacthd.framedblocks.client.model.slopeedge;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.TranslatedItemModelInfo;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public class FramedCornerSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final CornerType type;
    private final boolean altType;
    private final boolean ySlope;

    public FramedCornerSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.CORNER_TYPE);
        this.altType = ctx.state().getValue(PropertyHolder.ALT_TYPE);
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
            if (altType)
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(xBackFace, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if (quadDir == yBackFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.cutTopBottom(xBackFace.getOpposite(), .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.cutTopBottom(xBackFace.getOpposite(), right ? 1.5F : .5F, right ? .5F : 1.5F))
                            .apply(Modifiers.cutTopBottom(xBackFace, .5F))
                            .export(quadMap.get(quadDir));
                }
                else if (quadDir == xBackFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .apply(Modifiers.cutSideUpDown(top, right ? .5F : 1.5F, right ? 1.5F : .5F))
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .export(quadMap.get(quadDir));
                }
                else if (!ySlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(xBackFace.getOpposite(), top ? 0F : 1F, top ? 1F : 0F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(xBackFace, .5F))
                            .apply(Modifiers.cutSideUpDown(top, right ? 1F : 0F, right ? 0F : 1F))
                            .apply(Modifiers.makeHorizontalSlope(right, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && quadDir == yBackFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.cutTopBottom(xBackFace.getOpposite(), right ? 1.5F : .5F, right ? .5F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && quadDir == xBackFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .apply(Modifiers.cutSideUpDown(top, right ? 1.5F : .5F, right ? .5F : 1.5F))
                            .apply(Modifiers.makeHorizontalSlope(!right, 45))
                            .apply(Modifiers.offset(xBackFace.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(xBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .export(quadMap.get(quadDir));
                }
                else if (quadDir == yBackFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutTopBottom(xBackFace.getOpposite(), right ? .5F : -.5F, right ? -.5F : .5F))
                            .export(quadMap.get(quadDir));
                }
                else if (quadDir == xBackFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutSideUpDown(top, right ? -.5F : .5F, right ? .5F : -.5F))
                            .export(quadMap.get(quadDir));
                }
                else if (!ySlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(xBackFace.getOpposite(), top ? 0F : .5F, top ? .5F : 0F))
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(xBackFace, top ? 1F : .5F, top ? .5F : 1F))
                            .apply(Modifiers.cutSideLeftRight(xBackFace.getOpposite(), .5F))
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.makeHorizontalSlope(right, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && quadDir == yBackFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutTopBottom(xBackFace.getOpposite(), right ? .5F : -.5F, right ? -.5F : .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(yBackFace, .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && quadDir == xBackFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutSideUpDown(top, right ? .5F : -.5F, right ? -.5F : .5F))
                            .apply(Modifiers.makeHorizontalSlope(!right, 45))
                            .apply(Modifiers.offset(xBackFace, .5F))
                            .export(quadMap.get(null));
                }
            }
        }
        else
        {
            boolean top = type == CornerType.TOP;
            Direction bottomFace = top ? Direction.UP : Direction.DOWN;
            if (altType)
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .export(quadMap.get(quadDir));
                }
                else if (quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .export(quadMap.get(quadDir));
                }
                else if (quadDir == bottomFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if (!ySlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
                else if (!ySlope && quadDir == dir.getClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && quadDir == bottomFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1F, 0F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(bottomFace.getOpposite(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0F, 1F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(bottomFace.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? .5F : -.5F, top ? -.5F : .5F))
                            .export(quadMap.get(quadDir));
                }
                else if (quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                            .export(quadMap.get(quadDir));
                }
                else if (quadDir == bottomFace)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                            .export(quadMap.get(quadDir));
                }
                else if (ySlope && quadDir == bottomFace.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1F, 0F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(bottomFace, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0F, 1F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(bottomFace, .5F))
                            .export(quadMap.get(null));
                }
                else if (!ySlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? .5F : -.5F, top ? -.5F : .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));
                }
                else if (!ySlope && quadDir == dir.getClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap.get(null));
                }
            }
        }
    }

    @Override
    public ItemModelInfo getItemModelInfo()
    {
        return TranslatedItemModelInfo.HAND_Y_HALF_UP;
    }
}
