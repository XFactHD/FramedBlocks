package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HopperBlock;
import org.jspecify.annotations.Nullable;

public class FramedHopperGeometry extends Geometry
{
    private final Direction facing;

    public FramedHopperGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(HopperBlock.FACING);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();

        if (DirUtils.isY(quadDir) && facing != Direction.DOWN)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), 4F/16F))
                    .apply(Modifiers.cut(facing.getClockWise().getAxis(), 10F/16F))
                    .apply(Modifiers.setPosition(quadDir == Direction.UP ? 8F/16F : 12F/16F))
                    .export(quadMap, null);
        }
        if (quadDir == Direction.UP)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.NORTH, 2F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.SOUTH, 2F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.Axis.Z, 14F/16F))
                    .apply(Modifiers.cut(Direction.EAST, 2F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.Axis.Z, 14F/16F))
                    .apply(Modifiers.cut(Direction.WEST, 2F/16F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(2F/16F, 2F/16F, 14F/16F, 14F/16F))
                    .apply(Modifiers.setPosition(11F/16F))
                    .export(quadMap, null);
        }
        else if (quadDir == Direction.DOWN)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.NORTH, 4F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.SOUTH, 4F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.Axis.Z, 12F/16F))
                    .apply(Modifiers.cut(Direction.EAST, 4F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.Axis.Z, 12F/16F))
                    .apply(Modifiers.cut(Direction.WEST, 4F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(4F/16F, 4F/16F, 12F/16F, 12F/16F))
                    .apply(Modifiers.setPosition(12F/16F))
                    .export(quadMap, null);

            if (facing == Direction.DOWN)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                        .export(quadMap, null);
            }
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, 6F/16F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(4F/16F, 4F/16F, 12F/16F, 10F/16F))
                    .apply(Modifiers.setPosition(12F/16F))
                    .export(quadMap, null);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, 5F/16F))
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 14F/16F))
                    .apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap, null);

            if (facing == Direction.DOWN)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(6F/16F, 0F, 10F/16F, 4F/16F))
                        .apply(Modifiers.setPosition(10F/16F))
                        .export(quadMap, null);
            }
            else if (quadDir == facing)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(6F/16F, 4F/16F, 10F/16F, 8F/16F))
                        .export(quadMap, quadDir);
            }
            else if (quadDir.getAxis() == facing.getClockWise().getAxis())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), 4F/16F))
                        .apply(Modifiers.cut(Direction.DOWN, 12F/16F))
                        .apply(Modifiers.cut(Direction.UP, 8F/16F))
                        .apply(Modifiers.setPosition(10F/16F))
                        .export(quadMap, null);
            }
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}
