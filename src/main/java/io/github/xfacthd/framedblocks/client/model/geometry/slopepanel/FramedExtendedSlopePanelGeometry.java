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
import org.jspecify.annotations.Nullable;

public class FramedExtendedSlopePanelGeometry extends Geometry
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final Direction orientation;
    private final boolean altSlope;

    public FramedExtendedSlopePanelGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.rotation = ctx.state().getValue(PropertyHolder.ROTATION);
        this.orientation = rotation.withFacing(facing);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction face = quad.direction();
        boolean yAxis = DirUtils.isY(orientation);
        if (face == orientation)
        {
            if (DirUtils.isY(orientation))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), .5F))
                        .export(quadMap.get(face));

                if (altSlope)
                {
                    QuadModifier.of(quad)
                            .apply(FramedSlopePanelGeometry.createVerticalSlope(facing, orientation))
                            .apply(Modifiers.offset(facing.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), .5F))
                        .export(quadMap.get(face));
            }
        }
        else if ((!rotation.isVertical() || !altSlope) && face == facing.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(FramedSlopePanelGeometry.createSlope(facing, orientation))
                    .export(quadMap.get(null));
        }
        else if (face.getAxis() != facing.getAxis() && face.getAxis() != orientation.getAxis())
        {
            if (yAxis)
            {
                boolean up = orientation == Direction.UP;
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), up ? .5F :  1F, up ?  1F : .5F))
                        .export(quadMap.get(face));
            }
            else
            {
                boolean rightRot = rotation == HorizontalRotation.RIGHT;
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), rightRot ?  1F : .5F, rightRot ? .5F :  1F))
                        .export(quadMap.get(face));
            }
        }
    }
}
