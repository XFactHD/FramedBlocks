package xfacthd.framedblocks.client.model.slopeedge.modern;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.TranslatedItemModelInfo;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.slopeedge.legacy.LegacyFramedSlopeEdgeGeometry;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public class FramedSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final SlopeType type;
    private final boolean altType;
    private final boolean ySlope;

    private FramedSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.SLOPE_TYPE);
        this.altType = ctx.state().getValue(PropertyHolder.ALT_TYPE);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (altType)
        {
            if (type == SlopeType.HORIZONTAL)
            {
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if (quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if (!ySlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .25F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .25F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && quadDir == dir.getClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .25F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .25F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
                else if (Utils.isY(quadDir))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1.5F, .5F))
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .export(quadMap.get(quadDir));
                }
            }
            else
            {
                boolean top = type == SlopeType.TOP;
                if (quadDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
                else if (!ySlope && quadDir == dir.getOpposite())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .25F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .25F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                            .export(quadMap.get(null));
                }
                else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .25F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), .25F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
                else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .export(quadMap.get(quadDir));
                }
            }
            return;
        }

        if (type == SlopeType.HORIZONTAL)
        {
            if (!ySlope && quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .25F))
                        .apply(Modifiers.makeHorizontalSlope(false, 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .25F))
                        .apply(Modifiers.makeHorizontalSlope(false, 45))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .export(quadMap.get(null));
            }
            else if (ySlope && quadDir == dir.getClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .25F))
                        .apply(Modifiers.makeHorizontalSlope(true, 45))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir, .25F))
                        .apply(Modifiers.makeHorizontalSlope(true, 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap.get(null));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F, -.5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else
        {
            boolean top = type == SlopeType.TOP;
            if (!ySlope && quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .25F))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .25F))
                        .apply(Modifiers.makeVerticalSlope(!top, 45))
                        .apply(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F))
                        .export(quadMap.get(null));
            }
            else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .25F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .25F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap.get(null));
            }
            else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .export(quadMap.get(quadDir));
            }
            else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
    }

    @Override
    public ItemModelInfo getItemModelInfo()
    {
        return TranslatedItemModelInfo.HAND_Y_HALF_UP;
    }



    public static Geometry create(GeometryFactory.Context ctx)
    {
        if (ClientConfig.VIEW.useLegacySlopeEdgeModel())
        {
            return new LegacyFramedSlopeEdgeGeometry(ctx);
        }
        return new FramedSlopeEdgeGeometry(ctx);
    }
}
