package io.github.xfacthd.framedblocks.client.model.geometry.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.client.model.geometry.slopepanel.FramedSlopePanelGeometry;
import io.github.xfacthd.framedblocks.client.model.geometry.slopeslab.FramedSlopeSlabGeometry;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class FramedPyramidGeometry extends Geometry
{
    static final Vector3f BOTTOM_CENTER = new Vector3f(.5F, 0, .5F);
    static final Vector3f TOP_CENTER = new Vector3f(.5F, 1, .5F);
    static final Vector3f ZERO = new Vector3f();

    final Direction facing;
    final boolean ySlope;
    final boolean hasPillar;
    final float slopeHeight;
    private final float pillarHeight;
    private final float pillarWidth;
    private final float pillarFaceMin;
    private final float pillarFaceMax;

    public FramedPyramidGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(BlockStateProperties.FACING);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
        PillarConnection pillar = ctx.state().getValue(PropertyHolder.PILLAR_CONNECTION);
        this.hasPillar = pillar != PillarConnection.NONE;
        this.slopeHeight = computeSlopeHeight(pillar);
        this.pillarHeight = computePillarHeight(pillar);
        this.pillarWidth = switch (pillar)
        {
            case NONE -> 0F;
            case POST -> 10F/16F;
            case PILLAR -> 12F/16F;
        };
        float pillarFaceRadius = switch (pillar)
        {
            case NONE -> 0F;
            case POST -> 2F/16F;
            case PILLAR -> 4F/16F;
        };
        this.pillarFaceMin = .5F - pillarFaceRadius;
        this.pillarFaceMax = .5F + pillarFaceRadius;
    }

    @Override
    public final void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        buildBody(quadMap, quad, quadDir);
        if (hasPillar)
        {
            buildPillar(quadMap, quad, quadDir);
        }
    }

    protected void buildBody(QuadMap quadMap, BakedQuad quad, Direction quadDir)
    {
        if (DirUtils.isY(facing))
        {
            boolean up = facing == Direction.UP;
            if (!ySlope && quadDir.getAxis() != facing.getAxis())
            {
                QuadModifier.of(quad)
                        .applyIf(Modifiers.cut(facing, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(quadDir.getCounterClockWise(), up ? .5F : 1, up ? 1 : .5F))
                        .apply(Modifiers.cut(quadDir.getClockWise(), up ? .5F : 1, up ? 1 : .5F))
                        .apply(Modifiers.makeVerticalSlope(up, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
            else if (ySlope && quadDir == facing)
            {
                for (Direction dir : Direction.Plane.HORIZONTAL)
                {
                    float angle = up ? -FramedSlopePanelGeometry.SLOPE_ANGLE : FramedSlopePanelGeometry.SLOPE_ANGLE;
                    angle = (up ? -90F : 90F) - angle;
                    if (dir == Direction.NORTH || dir == Direction.EAST) { angle *= -1F; }

                    Vector3f origin = up ? TOP_CENTER : BOTTOM_CENTER;

                    QuadModifier.of(quad)
                            .applyIf(Modifiers.cut(dir, slopeHeight), hasPillar)
                            .apply(Modifiers.cut(dir.getCounterClockWise(), .5F, 1))
                            .apply(Modifiers.cut(dir.getClockWise(), 1, .5F))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .apply(Modifiers.rotate(dir.getClockWise().getAxis(), origin, angle, true))
                            .export(quadMap.get(null));
                }
            }
        }
        else
        {
            if (!ySlope && quadDir.getAxis() == facing.getAxis())
            {
                QuadModifier.of(quad)
                        .applyIf(Modifiers.cut(Direction.DOWN, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(facing.getClockWise(), 1, .5F))
                        .apply(Modifiers.cut(facing.getCounterClockWise(), 1, .5F))
                        .apply(Modifiers.makeVerticalSlope(true, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.UP, .5F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .applyIf(Modifiers.cut(Direction.UP, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(facing.getClockWise(), .5F, 1))
                        .apply(Modifiers.cut(facing.getCounterClockWise(), .5F, 1))
                        .apply(Modifiers.makeVerticalSlope(false, FramedSlopeSlabGeometry.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.DOWN, .5F))
                        .export(quadMap.get(null));
            }
            else if (ySlope && DirUtils.isY(quadDir))
            {
                boolean up = quadDir == Direction.UP;

                float angle = up ? FramedSlopePanelGeometry.SLOPE_ANGLE : -FramedSlopePanelGeometry.SLOPE_ANGLE;
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
                        .apply(Modifiers.cut(facing.getCounterClockWise(), .5F, 1))
                        .apply(Modifiers.cut(facing.getClockWise(), 1, .5F))
                        .apply(Modifiers.rotate(facing.getClockWise().getAxis(), origin, angle, true))
                        .export(quadMap.get(null));
            }
            else if (quadDir.getAxis() == facing.getClockWise().getAxis())
            {
                boolean right = quadDir == facing.getClockWise();
                QuadModifier.of(quad)
                        .applyIf(Modifiers.cut(facing, slopeHeight), hasPillar)
                        .apply(Modifiers.cut(Direction.DOWN, right ? 1 : .5F, right ? .5F : 1))
                        .apply(Modifiers.cut(Direction.UP, right ? 1 : .5F, right ? .5F : 1))
                        .apply(Modifiers.makeHorizontalSlope(!right, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
        }
    }

    private void buildPillar(QuadMap quadMap, BakedQuad quad, Direction quadDir)
    {
        if (DirUtils.isY(facing))
        {
            if (quadDir == facing)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(pillarFaceMin, pillarFaceMin, pillarFaceMax, pillarFaceMax))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir != facing.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), pillarHeight))
                        .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), pillarWidth))
                        .apply(Modifiers.setPosition(pillarWidth))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            if (DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), pillarHeight))
                        .apply(Modifiers.cut(facing.getClockWise().getAxis(), pillarWidth))
                        .apply(Modifiers.setPosition(pillarWidth))
                        .export(quadMap.get(null));
            }
            else if (quadDir == facing)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(pillarFaceMin, pillarFaceMin, pillarFaceMax, pillarFaceMax))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir != facing.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing.getOpposite(), pillarHeight))
                        .apply(Modifiers.cut(Direction.Axis.Y, pillarWidth))
                        .apply(Modifiers.setPosition(pillarWidth))
                        .export(quadMap.get(null));
            }
        }
    }

    protected float computeSlopeHeight(PillarConnection pillar)
    {
        return switch (pillar)
        {
            case NONE -> 1F;
            case POST -> 12F/16F;
            case PILLAR -> 8F/16F;
        };
    }

    protected float computePillarHeight(PillarConnection pillar)
    {
        return switch (pillar)
        {
            case NONE -> 0F;
            case POST -> 4F/16F;
            case PILLAR -> 8F/16F;
        };
    }
}
