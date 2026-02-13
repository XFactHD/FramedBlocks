package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.geometry.slopepanel.FramedSlopePanelGeometry;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

public class FramedLargeInnerCornerSlopePanelWallGeometry extends Geometry
{
    private final Direction dir;
    private final Direction horRotDir;
    private final Direction vertRotDir;
    private final boolean ySlope;

    public FramedLargeInnerCornerSlopePanelWallGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        this.horRotDir = Utils.isY(rotDir) ? perpRotDir : rotDir;
        this.vertRotDir = Utils.isY(rotDir) ? rotDir : perpRotDir;
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        boolean cw = horRotDir == dir.getClockWise();
        boolean up = vertRotDir == Direction.UP;
        if (quadDir == dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(horRotDir, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(horRotDir.getOpposite(), .5F))
                    .apply(Modifiers.cut(vertRotDir, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == horRotDir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertRotDir, cw ? 0F : .5F, cw ? .5F : 0F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertRotDir.getOpposite(), cw ? 1F : .5F, cw ? .5F : 1F))
                    .apply(Modifiers.makeHorizontalSlope(cw, FramedSlopePanelGeometry.SLOPE_ANGLE))
                    .apply(Modifiers.offset(horRotDir.getOpposite(), .5F))
                    .export(quadMap.get(null));
        }
        else if (!ySlope && quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(horRotDir.getOpposite(), up ? .5F : 1F, up ? 1F : .5F))
                    .apply(Modifiers.makeVerticalSlope(up, FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));
        }
        else if (quadDir == vertRotDir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(horRotDir, cw ? 0F : .5F, cw ? .5F : 0F))
                    .export(quadMap.get(quadDir));

            if (ySlope)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(horRotDir.getOpposite(), cw ? .5F : 1F, cw ? 1F : .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(vertRotDir.getOpposite(), .5F))
                        .export(quadMap.get(null));
            }
        }
    }
}
