package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.client.model.geometry.slopepanel.FramedSlopePanelGeometry;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedLargeCornerSlopePanelGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean altSlope;

    public FramedLargeCornerSlopePanelGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            Direction cutDir = quadDir == dir ? dir.getClockWise() : dir.getOpposite();

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, top ? 1F : .5F, top ? .5F : 1F))
                    .apply(Modifiers.cut(cutDir.getOpposite(), .5F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }
        else if (!altSlope && (quadDir == dir.getOpposite() || quadDir == dir.getClockWise()))
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, top ? 1F : .5F, top ? .5F : 1F))
                    .apply(Modifiers.makeVerticalSlope(!top, FramedSlopePanelGeometry.SLOPE_ANGLE))
                    .export(quadMap, null);
        }
        else if (altSlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F, 1))
                    .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                    .apply(Modifiers.offset(dir.getClockWise(), .5F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), 1, .5F))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                    .apply(Modifiers.offset(dir.getOpposite(), .5F))
                    .export(quadMap, null);
        }
        else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .export(quadMap, quadDir);
        }
    }
}
