package xfacthd.framedblocks.client.model.slopeedge.modern;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.client.model.slopeedge.legacy.LegacyFramedThreewayCornerSlopeEdgeGeometry;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedThreewayCornerSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean right;
    private final boolean altType;
    private final boolean ySlope;

    private FramedThreewayCornerSlopeEdgeGeometry(GeometryFactory.Context ctx)
    {
        Direction dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.right = ctx.state().getValue(PropertyHolder.RIGHT);
        this.dir = right ? dir.getClockWise() : dir;
        this.altType = ctx.state().getValue(PropertyHolder.ALT_TYPE);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        Direction yBackFace = top ? Direction.UP : Direction.DOWN;
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
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
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
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == yBackFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .5F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .5F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F, 1.5F))
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir.getOpposite())
            {
                if (!right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .25F))
                            .apply(Modifiers.cutSideUpDown(top, .5F, 1.5F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .25F))
                            .apply(Modifiers.cutSideUpDown(top, 1.5F, .5F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }

                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .25F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? .5F : 1.5F, top ? 1.5F : .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .25F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (quadDir == dir.getClockWise())
            {
                if (right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .25F))
                            .apply(Modifiers.cutSideUpDown(top, .5F, 1.5F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .25F))
                            .apply(Modifiers.cutSideUpDown(top, 1.5F, .5F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }

                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .25F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? .5F : 1.5F, top ? 1.5F : .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .25F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (ySlope && quadDir == yBackFace.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .25F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F, 1.5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .25F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1.5F, .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(dir.getOpposite(), .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .25F))
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1.5F, .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                        .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .25F))
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F, 1.5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                        .apply(Modifiers.offset(dir.getClockWise(), .5F))
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
            else if (quadDir == yBackFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), -.5F, .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir.getOpposite())
            {
                if (!right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .25F))
                            .apply(Modifiers.cutSideUpDown(top, 0, 1))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .25F))
                            .apply(Modifiers.cutSideUpDown(top, 1, 0))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap.get(null));
                }

                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .25F))
                            .apply(Modifiers.cutSideLeftRight(false, top ? 0 : 1, top ? 1 : 0))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .25F))
                            .apply(Modifiers.cutSideLeftRight(false, top ? 1 : 0, top ? 0 : 1))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(yBackFace, .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (quadDir == dir.getClockWise())
            {
                if (right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .25F))
                            .apply(Modifiers.cutSideUpDown(top, 1, 0))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .25F))
                            .apply(Modifiers.cutSideUpDown(top, 0, 1))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));
                }

                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .25F))
                            .apply(Modifiers.cutSideLeftRight(true, top ? 0 : 1, top ? 1 : 0))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .25F))
                            .apply(Modifiers.cutSideLeftRight(true, top ? 1 : 0, top ? 0 : 1))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(yBackFace, .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (ySlope && quadDir == yBackFace.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .25F))
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0, 1))
                        .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                        .apply(Modifiers.offset(yBackFace, .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .25F))
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1, 0))
                        .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .25F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1, 0))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(yBackFace, .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .25F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), 0, 1))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap.get(null));
            }
        }
    }



    public static Geometry create(GeometryFactory.Context ctx)
    {
        if (ClientConfig.VIEW.useLegacySlopeEdgeModel())
        {
            return new LegacyFramedThreewayCornerSlopeEdgeGeometry(ctx);
        }
        return new FramedThreewayCornerSlopeEdgeGeometry(ctx);
    }
}
