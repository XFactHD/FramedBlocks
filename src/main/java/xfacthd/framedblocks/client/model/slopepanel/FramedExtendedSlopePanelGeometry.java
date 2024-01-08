package xfacthd.framedblocks.client.model.slopepanel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedExtendedSlopePanelGeometry implements Geometry
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final Direction orientation;
    private final boolean ySlope;

    public FramedExtendedSlopePanelGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rotation = ctx.state().getValue(PropertyHolder.ROTATION);
        this.orientation = rotation.withFacing(facing);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        boolean yAxis = Utils.isY(orientation);
        if (face == orientation)
        {
            if (Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), .5F))
                        .export(quadMap.get(face));

                if (ySlope)
                {
                    QuadModifier.geometry(quad)
                            .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, orientation))
                            .apply(Modifiers.offset(facing.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), .5F))
                        .export(quadMap.get(face));
            }
        }
        else if ((!rotation.isVertical() || !ySlope) && face == facing.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(FramedSlopePanelGeometry.createSlope(facing, orientation))
                    .export(quadMap.get(null));
        }
        else if (face.getAxis() != facing.getAxis() && face.getAxis() != orientation.getAxis())
        {
            if (yAxis)
            {
                boolean up = orientation == Direction.UP;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), up ? .5F :  1F, up ?  1F : .5F))
                        .export(quadMap.get(face));
            }
            else
            {
                boolean rightRot = rotation == HorizontalRotation.RIGHT;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), rightRot ?  1F : .5F, rightRot ? .5F :  1F))
                        .export(quadMap.get(face));
            }
        }
    }
}
