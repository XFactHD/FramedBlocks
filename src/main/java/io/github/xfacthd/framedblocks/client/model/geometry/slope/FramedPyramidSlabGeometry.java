package io.github.xfacthd.framedblocks.client.model.geometry.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class FramedPyramidSlabGeometry extends Geometry
{
    private static final Vector3f ZERO = new Vector3f();

    private final Direction facing;
    private final boolean altSlope;

    public FramedPyramidSlabGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(BlockStateProperties.FACING);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(facing))
        {
            boolean up = facing == Direction.UP;
            if (!altSlope && quadDir.getAxis() != facing.getAxis())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, .5F))
                        .apply(Modifiers.cut(quadDir.getCounterClockWise(), up ? 0 : 1, up ? 1 : 0))
                        .apply(Modifiers.cut(quadDir.getClockWise(), up ? 0 : 1, up ? 1 : 0))
                        .apply(Modifiers.makeVerticalSlope(up, 45))
                        .export(quadMap, null);
            }
            else if (altSlope && quadDir == facing)
            {
                for (Direction dir : Direction.Plane.HORIZONTAL)
                {
                    boolean northeast = dir == Direction.NORTH || dir == Direction.EAST;
                    float angle = up ? -45 : 45;
                    if (northeast) { angle *= -1F; }
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, .5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), 0, 1))
                            .apply(Modifiers.cut(dir.getClockWise(), 1, 0))
                            .apply(Modifiers.setPosition(.5F))
                            .apply(Modifiers.rotateCentered(dir.getClockWise().getAxis(), angle, true))
                            .export(quadMap, null);
                }
            }
        }
        else
        {
            if (!altSlope && quadDir.getAxis() == facing.getAxis())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.DOWN, .5F))
                        .apply(Modifiers.cut(facing.getClockWise(), 1, 0))
                        .apply(Modifiers.cut(facing.getCounterClockWise(), 1, 0))
                        .apply(Modifiers.makeVerticalSlope(true, 45))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.UP, .5F))
                        .apply(Modifiers.cut(facing.getClockWise(), 0, 1))
                        .apply(Modifiers.cut(facing.getCounterClockWise(), 0, 1))
                        .apply(Modifiers.makeVerticalSlope(false, 45))
                        .export(quadMap, null);
            }
            else if (altSlope && DirUtils.isY(quadDir))
            {
                boolean up = quadDir == Direction.UP;

                float angle = up ? 45 : -45;
                if (facing == Direction.NORTH || facing == Direction.EAST)
                {
                    angle *= -1F;
                }

                Vector3f origin = facing.getOpposite().step().max(ZERO);
                if (up)
                {
                    origin.add(0, 1, 0);
                }

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, .5F))
                        .apply(Modifiers.cut(facing.getCounterClockWise(), 0, 1))
                        .apply(Modifiers.cut(facing.getClockWise(), 1, 0))
                        .apply(Modifiers.rotate(facing.getClockWise().getAxis(), origin, angle, true))
                        .export(quadMap, null);
            }
            else if (quadDir.getAxis() == facing.getClockWise().getAxis())
            {
                boolean right = quadDir == facing.getClockWise();
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, .5F))
                        .apply(Modifiers.cut(Direction.DOWN, right ? 1 : 0, right ? 0 : 1))
                        .apply(Modifiers.cut(Direction.UP, right ? 1 : 0, right ? 0 : 1))
                        .apply(Modifiers.makeHorizontalSlope(!right, 45))
                        .export(quadMap, null);
            }
        }
    }
}
