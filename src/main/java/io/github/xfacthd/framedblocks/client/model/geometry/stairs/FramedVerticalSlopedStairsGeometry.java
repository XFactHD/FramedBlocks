package io.github.xfacthd.framedblocks.client.model.geometry.stairs;

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

public class FramedVerticalSlopedStairsGeometry extends Geometry
{
    private final Direction facing;
    private final Direction rotDir;
    private final Direction rotDirTwo;
    private final boolean altSlope;

    public FramedVerticalSlopedStairsGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotDir = rot.withFacing(facing);
        this.rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == rotDir || quadDir == rotDirTwo)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == facing.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(rotDir, 1F, 0F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(rotDir.getOpposite(), 1F, 0F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }

        boolean useRotDirQuad = DirUtils.isY(rotDir) == altSlope;
        Direction slopeQuadDir = useRotDirQuad ? rotDir : rotDirTwo;
        Direction slopeRotDir = useRotDirQuad ? rotDirTwo : rotDir;

        if (quadDir == slopeQuadDir)
        {
            if (DirUtils.isY(slopeQuadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, .5F))
                        .apply(Modifiers.makeVerticalSlope(slopeRotDir, 45F))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, .5F))
                        .apply(Modifiers.makeVerticalSlope(slopeRotDir == Direction.UP, 45F))
                        .export(quadMap.get(null));
            }
        }
    }
}
