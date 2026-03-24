package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedCornerPillarGeometry extends Geometry
{
    private final Direction dir;

    public FramedCornerPillarGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == dir || quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), quadDir != dir)
                    .export(quadMap, quadDir == dir ? quadDir : null);
        }
        else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
        {
            boolean isCCW = quadDir == dir.getCounterClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), !isCCW)
                    .export(quadMap, isCCW ? quadDir : null);
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .export(quadMap, quadDir);
        }
    }
}
