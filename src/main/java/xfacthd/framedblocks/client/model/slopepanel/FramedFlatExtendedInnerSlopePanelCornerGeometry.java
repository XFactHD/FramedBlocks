package xfacthd.framedblocks.client.model.slopepanel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatExtendedInnerSlopePanelCornerGeometry extends Geometry
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final HorizontalRotation rotRotation;
    private final Direction orientation;
    private final Direction rotOrientation;
    private final boolean ySlope;

    public FramedFlatExtendedInnerSlopePanelCornerGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rotation = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotRotation = rotation.rotate(Rotation.COUNTERCLOCKWISE_90);
        this.orientation = rotation.withFacing(facing);
        this.rotOrientation = rotRotation.withFacing(facing);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (face == orientation)
        {
            FramedFlatSlopePanelCornerGeometry.createSideTriangle(quadMap, quad, facing, rotRotation, true, true);

            if (ySlope && Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createVerticalSlopeTriangle(facing.getOpposite(), orientation, false))
                        .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, orientation))
                        .apply(Modifiers.offset(facing.getOpposite(), .5F))
                        .export(quadMap.get(null));
            }
        }
        else if (face == rotOrientation)
        {
            FramedFlatSlopePanelCornerGeometry.createSideTriangle(quadMap, quad, facing, rotation, true, true);

            if (ySlope && Utils.isY(rotOrientation))
            {
                QuadModifier.geometry(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createVerticalSlopeTriangle(facing.getOpposite(), rotOrientation, true))
                        .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, rotOrientation))
                        .apply(Modifiers.offset(facing.getOpposite(), .5F))
                        .export(quadMap.get(null));
            }
        }
        else if (face == facing.getOpposite())
        {
            if (!ySlope || !Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createSlopeTriangle(facing, rotOrientation, true))
                        .apply(FramedSlopePanelGeometry.createSlope(facing, orientation))
                        .export(quadMap.get(null));
            }

            if (!ySlope || !Utils.isY(rotOrientation))
            {
                QuadModifier.geometry(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createSlopeTriangle(facing, orientation, false))
                        .apply(FramedSlopePanelGeometry.createSlope(facing, rotOrientation))
                        .export(quadMap.get(null));
            }
        }
        else if (face == facing)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }
}
