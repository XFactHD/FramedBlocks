package io.github.xfacthd.framedblocks.client.model.geometry.slope;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

public class FramedUpperPyramidSlabGeometry extends FramedPyramidGeometry
{
    public FramedUpperPyramidSlabGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    protected void buildBody(QuadMapBuilder quadMap, BakedQuad quad, Direction quadDir)
    {
        if (DirUtils.isY(facing))
        {
            boolean up = facing == Direction.UP;
            if (!altSlope && quadDir.getAxis() != facing.getAxis())
            {
                QuadModifier.of(quad)
                        .applyIf(Modifiers.cut(facing, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(facing.getOpposite(), .5F))
                        .apply(Modifiers.cut(quadDir.getCounterClockWise(), up ? .5F : 1.5F, up ? 1.5F : .5F))
                        .apply(Modifiers.cut(quadDir.getClockWise(), up ? .5F : 1.5F, up ? 1.5F : .5F))
                        .apply(Modifiers.makeVerticalSlope(up, 45))
                        .apply(Modifiers.offset(quadDir, .5F))
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
                            .applyIf(Modifiers.cut(dir, slopeHeight), hasPillar)
                            .apply(Modifiers.cut(dir.getOpposite(), .5F))
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F, 1.5F))
                            .apply(Modifiers.cut(dir.getClockWise(), 1.5F, .5F))
                            .apply(Modifiers.rotateCentered(dir.getClockWise().getAxis(), angle, true))
                            .apply(Modifiers.offset(facing.getOpposite(), .5F))
                            .export(quadMap, null);
                }
            }
        }
        else
        {
            if (!altSlope && quadDir.getAxis() == facing.getAxis())
            {
                QuadModifier.of(quad)
                        .applyIf(Modifiers.cut(Direction.DOWN, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(Direction.UP, .5F))
                        .apply(Modifiers.cut(facing.getClockWise(), 1.5F, .5F))
                        .apply(Modifiers.cut(facing.getCounterClockWise(), 1.5F, .5F))
                        .apply(Modifiers.makeVerticalSlope(true, 45))
                        .apply(Modifiers.offset(Direction.UP, .5F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .applyIf(Modifiers.cut(Direction.UP, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(Direction.DOWN, .5F))
                        .apply(Modifiers.cut(facing.getClockWise(), .5F, 1.5F))
                        .apply(Modifiers.cut(facing.getCounterClockWise(), .5F, 1.5F))
                        .apply(Modifiers.makeVerticalSlope(false, 45))
                        .apply(Modifiers.offset(Direction.DOWN, .5F))
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
                        .applyIf(Modifiers.cut(facing, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(facing.getOpposite(), .5F))
                        .apply(Modifiers.cut(facing.getCounterClockWise(), .5F, 1.5F))
                        .apply(Modifiers.cut(facing.getClockWise(), 1.5F, .5F))
                        .apply(Modifiers.rotate(facing.getClockWise().getAxis(), origin, angle, true))
                        .apply(Modifiers.offset(quadDir, .5F))
                        .export(quadMap, null);
            }
            else if (quadDir.getAxis() == facing.getClockWise().getAxis())
            {
                boolean right = quadDir == facing.getClockWise();
                QuadModifier.of(quad)
                        .applyIf(Modifiers.cut(facing, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(facing.getOpposite(), .5F))
                        .apply(Modifiers.cut(Direction.DOWN, right ? 1.5F : .5F, right ? .5F : 1.5F))
                        .apply(Modifiers.cut(Direction.UP, right ? 1.5F : .5F, right ? .5F : 1.5F))
                        .apply(Modifiers.makeHorizontalSlope(!right, 45))
                        .apply(Modifiers.offset(quadDir, .5F))
                        .export(quadMap, null);
            }
        }
        if (quadDir == facing.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }
    }

    @Override
    protected float computeSlopeHeight(PillarConnection pillar)
    {
        return switch (pillar)
        {
            case NONE -> 1F;
            case POST -> 14F/16F;
            case PILLAR -> 12F/16F;
        };
    }

    @Override
    protected float computePillarHeight(PillarConnection pillar)
    {
        return switch (pillar)
        {
            case NONE -> 0F;
            case POST -> 2F/16F;
            case PILLAR -> 4F/16F;
        };
    }
}
