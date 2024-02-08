package xfacthd.framedblocks.client.model.slopepanel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedCompoundSlopePanelGeometry extends Geometry
{
    private final Direction dir;
    private final HorizontalRotation rot;
    private final Direction orientation;
    private final Direction.Axis triangleAxis;
    private final boolean ySlope;

    public FramedCompoundSlopePanelGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rot = ctx.state().getValue(PropertyHolder.ROTATION);
        this.orientation = rot.withFacing(dir);
        this.triangleAxis = rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir).getAxis();
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == orientation)
        {
            if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else if (quadDir == orientation.getOpposite())
        {
            if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir, .5F))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir, .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else if (quadDir == dir)
        {
            if (!Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeHorizontalSlope(rot == HorizontalRotation.LEFT, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
            else if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeVerticalSlope(rot == HorizontalRotation.DOWN, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            if (!Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeHorizontalSlope(rot == HorizontalRotation.LEFT, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
            else if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeVerticalSlope(rot == HorizontalRotation.UP, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
        }
        else if (triangleAxis == Direction.Axis.Y && Utils.isY(quadDir))
        {
            boolean right = rot == HorizontalRotation.RIGHT;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, right ? 1F : .5F, right ? .5F : 1F))
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), right ? 1F : .5F, right ? .5F : 1F))
                    .export(quadMap.get(quadDir));
        }
        else if (triangleAxis != Direction.Axis.Y && quadDir.getAxis() == triangleAxis)
        {
            boolean up = rot == HorizontalRotation.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, up ? 1F : .5F, up ? .5F : 1F))
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), up ? .5F : 1F, up ? 1F : .5F))
                    .export(quadMap.get(quadDir));
        }
    }
}
