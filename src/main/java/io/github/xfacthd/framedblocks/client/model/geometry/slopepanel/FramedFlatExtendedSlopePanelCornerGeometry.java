package io.github.xfacthd.framedblocks.client.model.geometry.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

public class FramedFlatExtendedSlopePanelCornerGeometry extends Geometry
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final HorizontalRotation rotRotation;
    private final Direction orientation;
    private final Direction rotOrientation;
    private final boolean altSlope;

    public FramedFlatExtendedSlopePanelCornerGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rotation = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotRotation = rotation.rotate(Rotation.COUNTERCLOCKWISE_90);
        this.orientation = rotation.withFacing(facing);
        this.rotOrientation = rotRotation.withFacing(facing);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction face = quad.direction();
        if (face == orientation.getOpposite())
        {
            FramedFlatSlopePanelCornerGeometry.createSideTriangle(quadMap, quad, facing, rotRotation, true, true);
        }
        else if (face == rotOrientation.getOpposite())
        {
            FramedFlatSlopePanelCornerGeometry.createSideTriangle(quadMap, quad, facing, rotation, true, true);
        }
        else if (face == orientation || face == rotOrientation)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), .5F))
                    .export(quadMap, face);

            if (altSlope && DirUtils.isY(orientation) && face == orientation)
            {
                QuadModifier.of(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createVerticalSlopeTriangle(facing, orientation, false))
                        .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, orientation))
                        .apply(Modifiers.offset(facing.getOpposite(), .5F))
                        .export(quadMap, null);
            }
            else if (altSlope && DirUtils.isY(rotOrientation) && face == rotOrientation)
            {
                QuadModifier.of(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createVerticalSlopeTriangle(facing, rotOrientation, true))
                        .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, rotOrientation))
                        .apply(Modifiers.offset(facing.getOpposite(), .5F))
                        .export(quadMap, null);
            }
        }
        else if (face == facing.getOpposite())
        {
            if (!altSlope || !DirUtils.isY(orientation))
            {
                QuadModifier.of(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createSlopeTriangle(facing, orientation, false, face))
                        .apply(FramedSlopePanelGeometry.createSlope(facing, orientation))
                        .export(quadMap, null);
            }

            if (!altSlope || !DirUtils.isY(rotOrientation))
            {
                QuadModifier.of(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createSlopeTriangle(facing, rotOrientation, true, face))
                        .apply(FramedSlopePanelGeometry.createSlope(facing, rotOrientation))
                        .export(quadMap, null);
            }
        }
        else if (face == facing)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }
    }
}
