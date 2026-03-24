package io.github.xfacthd.framedblocks.client.model.geometry.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedHalfSlopeGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean right;
    private final boolean altSlope;

    public FramedHalfSlopeGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.right = ctx.state().getValue(PropertyHolder.RIGHT);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();

        Direction cutDir = right ? dir.getCounterClockWise() : dir.getClockWise();

        if (!altSlope && quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.makeVerticalSlope(!top, 45))
                    .apply(Modifiers.cut(cutDir, .5F))
                    .export(quadMap, null);
        }
        else if (altSlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, .5F))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                    .export(quadMap, null);
        }
        else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
        {
            boolean needOffset = right == (quadDir == dir.getCounterClockWise());

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                    .applyIf(Modifiers.setPosition(.5F), needOffset)
                    .export(quadMap, needOffset ? null : quadDir);
        }
        else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, .5F))
                    .export(quadMap, quadDir);
        }
        else if (quadDir == dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, .5F))
                    .export(quadMap, quadDir);
        }
    }
}
