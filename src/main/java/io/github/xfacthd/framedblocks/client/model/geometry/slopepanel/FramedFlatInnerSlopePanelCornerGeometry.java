package io.github.xfacthd.framedblocks.client.model.geometry.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
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

public class FramedFlatInnerSlopePanelCornerGeometry extends Geometry
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final HorizontalRotation rotRotation;
    private final Direction orientation;
    private final Direction rotOrientation;
    private final boolean front;
    private final boolean altSlope;

    public FramedFlatInnerSlopePanelCornerGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rotation = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotRotation = rotation.rotate(Rotation.COUNTERCLOCKWISE_90);
        this.orientation = rotation.withFacing(facing);
        this.rotOrientation = rotRotation.withFacing(facing);
        this.front = ctx.state().getValue(PropertyHolder.FRONT);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction face = quad.direction();
        if (face == orientation)
        {
            FramedFlatSlopePanelCornerGeometry.createSideTriangle(quadMap, quad, facing, rotRotation, front, false);

            if (altSlope && DirUtils.isY(orientation))
            {
                QuadModifier.of(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createVerticalSlopeTriangle(facing.getOpposite(), orientation, false))
                        .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, rotOrientation))
                        .applyIf(Modifiers.offset(facing.getOpposite(), .5F), front)
                        .export(quadMap.get(null));
            }
        }
        else if (face == rotOrientation)
        {
            FramedFlatSlopePanelCornerGeometry.createSideTriangle(quadMap, quad, facing, rotation, front, false);

            if (altSlope && DirUtils.isY(rotOrientation))
            {
                QuadModifier.of(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createVerticalSlopeTriangle(facing.getOpposite(), rotOrientation, true))
                        .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, orientation))
                        .applyIf(Modifiers.offset(facing.getOpposite(), .5F), front)
                        .export(quadMap.get(null));
            }
        }
        else if (face == orientation.getOpposite() || face == rotOrientation.getOpposite())
        {
            Direction cutDir = front ? facing : facing.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, .5F))
                    .export(quadMap.get(face));
        }
        else if (face == facing.getOpposite())
        {
            if (!altSlope || !DirUtils.isY(orientation))
            {
                QuadModifier.of(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createSlopeTriangle(facing, rotOrientation, true, face))
                        .apply(FramedSlopePanelGeometry.createSlope(facing, orientation))
                        .applyIf(Modifiers.offset(facing, .5F), !front)
                        .export(quadMap.get(null));
            }

            if (!altSlope || !DirUtils.isY(rotOrientation))
            {
                QuadModifier.of(quad)
                        .apply(FramedFlatSlopePanelCornerGeometry.createSlopeTriangle(facing, orientation, false, face))
                        .apply(FramedSlopePanelGeometry.createSlope(facing, rotOrientation))
                        .applyIf(Modifiers.offset(facing, .5F), !front)
                        .export(quadMap.get(null));
            }
        }
        else if (face == facing && front)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }
}
