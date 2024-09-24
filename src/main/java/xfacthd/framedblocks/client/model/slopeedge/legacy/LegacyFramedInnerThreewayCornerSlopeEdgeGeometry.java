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

public class LegacyFramedInnerThreewayCornerSlopeEdgeGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean right;
    private final boolean altType;
    private final boolean ySlope;

    public LegacyFramedInnerThreewayCornerSlopeEdgeGeometry(GeometryFactory.Context ctx)
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
            if (quadDir == dir || quadDir == dir.getCounterClockWise())
            {
                Direction cutDir = quadDir == dir ? dir.getCounterClockWise() : dir;
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
            else if (quadDir == yBackFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .5F))
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
            else if (quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap.get(quadDir));

                if (!right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .25F))
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), top ? -.5F : .5F, top ? .5F : -.5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? 0F : 1F, top ? 1F : 0F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }

                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .25F))
                            .apply(Modifiers.cutSideUpDown(false, top ? -.5F : 0F, top ? .5F : 1F))
                            .apply(Modifiers.cutSideUpDown(true, top ? 0F : -.5F, top ? 1F : .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (quadDir == dir.getClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap.get(quadDir));

                if (right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .25F))
                            .apply(Modifiers.cutSideLeftRight(dir, top ? -.5F : .5F, top ? .5F : -.5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 0F : 1F, top ? 1F : 0F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));
                }

                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .25F))
                            .apply(Modifiers.cutSideUpDown(false, top ? .5F : 1F, top ? -.5F : 0F))
                            .apply(Modifiers.cutSideUpDown(true, top ? 1F : .5F, top ? 0F : -.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (quadDir == yBackFace.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .5F))
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F, 1.5F))
                        .export(quadMap.get(quadDir));

                if (ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .25F))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1F, 0F))
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), -.5F, .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .25F))
                            .apply(Modifiers.cutTopBottom(dir, .5F, -.5F))
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0F, 1F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(yBackFace.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
            }
        }
        else
        {
            if (quadDir == dir || quadDir == dir.getCounterClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(quadDir == dir ? dir.getClockWise() : dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == yBackFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .5F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? .5F : -.5F, top ? -.5F : .5F))
                        .export(quadMap.get(quadDir));

                if (!right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .75F))
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), top ? .5F : 1.5F, top ? 1.5F : .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), top ? 0F : 1F, top ? 1F : 0F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));
                }

                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .75F))
                            .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                            .apply(Modifiers.cutSideUpDown(false, top ? .5F : 0F, top ? 1.5F : 1F))
                            .apply(Modifiers.cutSideUpDown(true, top ? 0F : .5F, top ? 1F : 1.5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir, .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (quadDir == dir.getClockWise())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? .5F : -.5F, top ? -.5F : .5F))
                        .export(quadMap.get(quadDir));

                if (right)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .75F))
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir, top ? .5F : 1.5F, top ? 1.5F : .5F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 0F : 1F, top ? 1F : 0F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap.get(null));
                }

                if (!ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(top, .5F))
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .75F))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutSideUpDown(false, top ? 1.5F : 1F, top ? .5F : 0F))
                            .apply(Modifiers.cutSideUpDown(true, top ? 1F : 1.5F, top ? 0F : .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (quadDir == yBackFace.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), -.5F, .5F))
                        .export(quadMap.get(quadDir));

                if (ySlope)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(yBackFace, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(yBackFace, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir, .75F))
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1F, 0F))
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F, 1.5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                            .apply(Modifiers.offset(yBackFace, .5F))
                            .export(quadMap.get(null));

                    QuadModifier.of(quad)
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .75F))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0F, 1F))
                            .apply(Modifiers.cutTopBottom(dir, 1.5F, .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(yBackFace, .5F))
                            .export(quadMap.get(null));
                }
            }
        }
    }
}
