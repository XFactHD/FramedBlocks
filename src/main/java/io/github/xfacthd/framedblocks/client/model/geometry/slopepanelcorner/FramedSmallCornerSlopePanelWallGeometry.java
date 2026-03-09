package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.client.model.geometry.slopepanel.FramedSlopePanelGeometry;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

public class FramedSmallCornerSlopePanelWallGeometry extends Geometry
{
    private final Direction dir;
    private final Direction horRotDir;
    private final Direction vertRotDir;
    private final boolean altSlope;

    public FramedSmallCornerSlopePanelWallGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        this.horRotDir = DirUtils.isY(rotDir) ? perpRotDir : rotDir;
        this.vertRotDir = DirUtils.isY(rotDir) ? rotDir : perpRotDir;
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
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
                    .apply(Modifiers.cut(horRotDir.getOpposite(), .5F))
                    .apply(Modifiers.cut(vertRotDir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == horRotDir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertRotDir.getOpposite(), cw ? 0F : .5F, cw ? .5F : 0F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == vertRotDir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(horRotDir.getOpposite(), cw ? .5F : 0F, cw ? 0F : .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == horRotDir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertRotDir.getOpposite(), cw ? .5F : 0F, cw ? 0F : .5F))
                    .apply(Modifiers.makeHorizontalSlope(!cw, FramedSlopePanelGeometry.SLOPE_ANGLE))
                    .apply(Modifiers.offset(horRotDir, .5F))
                    .export(quadMap.get(null));
        }
        else if (!altSlope && quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(horRotDir.getOpposite(), up ? 0F : .5F, up ? .5F : 0F))
                    .apply(Modifiers.makeVerticalSlope(!up, FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));
        }
        else if (altSlope && quadDir == vertRotDir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(horRotDir.getOpposite(), cw ? .5F : 0F, cw ? 0F : .5F))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), FramedSlopePanelGeometry.SLOPE_ANGLE))
                    .apply(Modifiers.offset(vertRotDir, .5F))
                    .export(quadMap.get(null));
        }
    }
}
