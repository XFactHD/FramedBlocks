package io.github.xfacthd.framedblocks.client.model.geometry.rail;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

public class FramedFancyRailGeometry extends Geometry
{
    private static final int SLEEPER_COUNT = 4;
    private static final int SLEEPER_COUNT_CURVE = 3;
    private static final float SLEEPER_BASE_OFFSET = 1F/16F;
    private static final float SLEEPER_DIST = 4F/16F;
    private static final float SLEEPER_DIST_CURVE = 6F/16F;
    private static final float SLEEPER_WIDTH = 2F/16F;
    private static final float SLEEPER_HEIGHT = 1F/16F;
    private static final float SLEEPER_DIAGONAL_OFFSET = 1.85F/16F;
    private static final Vector3f SCALE_X = new Vector3f(1, 0, 0);
    private static final Vector3f SCALE_Z = new Vector3f(0, 0, 1);
    private static final Vector3f[] SLOPE_ORIGINS = Util.make(new Vector3f[4], arr ->
    {
        arr[Direction.NORTH.get2DDataValue()] = new Vector3f(0, 0, 1);
        arr[Direction.EAST.get2DDataValue()] =  new Vector3f(0, 0, 0);
        arr[Direction.SOUTH.get2DDataValue()] = new Vector3f(0, 0, 0);
        arr[Direction.WEST.get2DDataValue()] =  new Vector3f(1, 0, 0);
    });

    private final BlockState state;
    private final BlockStateModel baseModel;
    private final RailShape shape;
    private final Direction mainDir;
    @Nullable
    private final Direction secDir;
    private final BlockState auxShaderState;

    private FramedFancyRailGeometry(GeometryFactory.Context ctx, Property<RailShape> shapeProperty, BlockState auxShaderState)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.auxShaderState = auxShaderState;
        this.shape = ctx.state().getValue(shapeProperty);
        this.mainDir = getDirectionFromRailShape(shape);
        this.secDir = getSecondaryDirectionFromRailShape(shape);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        if (shape.isSlope())
        {
            makeAscendingRailSleepers(quadMap, quad, mainDir);
        }
        else if (shape == RailShape.NORTH_SOUTH || shape == RailShape.EAST_WEST)
        {
            makeStraightRailSleepers(quadMap, quad, mainDir, Modifiers.noop(), UnaryOperator.identity());
        }
        else
        {
            makeCurvedRailSleepers(quadMap, quad, mainDir, Objects.requireNonNull(secDir));
        }
    }

    private static void makeStraightRailSleepers(QuadMapBuilder quadMap, BakedQuad quad, Direction dir, QuadModifier.Modifier lastMod, UnaryOperator<@Nullable Direction> cullFaceMod)
    {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir))
        {
            Direction targetDir = cullFaceMod.apply(quadDir == Direction.UP ? null : quadDir);
            forAllSleepers((i, distDir, distOpp) ->
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(dir, distDir))
                            .apply(Modifiers.cut(dir.getOpposite(), distOpp))
                            .applyIf(Modifiers.setPosition(SLEEPER_HEIGHT), quadDir == Direction.UP)
                            .apply(lastMod)
                            .export(quadMap, targetDir)
            );
        }
        else if (quadDir.getAxis() == dir.getAxis())
        {
            forAllSleepers((i, distDir, distOpp) ->
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(Direction.UP, SLEEPER_HEIGHT))
                            .apply(Modifiers.setPosition(distDir))
                            .apply(lastMod)
                            .export(quadMap, null)
            );
        }
        else
        {
            Direction targetDir = cullFaceMod.apply(quadDir);
            forAllSleepers((i, distDir, distOpp) ->
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(Direction.UP, SLEEPER_HEIGHT))
                            .apply(Modifiers.cut(dir, distDir))
                            .apply(Modifiers.cut(dir.getOpposite(), distOpp))
                            .apply(lastMod)
                            .export(quadMap, targetDir)
            );
        }
    }

    private static void makeAscendingRailSleepers(QuadMapBuilder quadMap, BakedQuad quad, Direction dir)
    {
        Direction.Axis axis = dir.getClockWise().getAxis();
        Vector3f origin = SLOPE_ORIGINS[dir.get2DDataValue()];
        float angle = DirUtils.isPositive(dir) == DirUtils.isX(dir) ? 45F : -45F;
        Vector3f scaleVec = DirUtils.isX(dir) ? SCALE_X : SCALE_Z;

        makeStraightRailSleepers(quadMap, quad, dir, Modifiers.rotate(axis, origin, angle, true, scaleVec), cullFace -> cullFace == Direction.DOWN ? null : cullFace);
    }

    private static void makeCurvedRailSleepers(QuadMapBuilder quadMap, BakedQuad quad, Direction dir, Direction secDir)
    {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir))
        {
            Direction targetDir = quadDir == Direction.UP ? null : quadDir;
            forAllSleepersCurve((i, distDir, distOpp) ->
            {
                boolean nonDiagUp = quadDir == Direction.UP && i != 1;
                float height = nonDiagUp ? (SLEEPER_HEIGHT - .001F) : SLEEPER_HEIGHT;
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, distDir))
                        .apply(Modifiers.cut(dir.getOpposite(), distOpp))
                        .applyIf(Modifiers.setPosition(height), quadDir == Direction.UP)
                        .applyIf(rotateCurveSleeper(dir, secDir, i), i < 2)
                        .applyIf(Modifiers.offset(dir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                        .applyIf(Modifiers.offset(secDir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                        .export(quadMap, targetDir);
            });
        }
        else if (quadDir.getAxis() == dir.getAxis())
        {
            boolean inDir = quadDir == dir;
            forAllSleepersCurve((i, distDir, distOpp) ->
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(Direction.UP, SLEEPER_HEIGHT))
                            .apply(Modifiers.setPosition(inDir ? distDir : distOpp))
                            .applyIf(rotateCurveSleeper(dir, secDir, i), i < 2)
                            .applyIf(Modifiers.offset(dir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                            .applyIf(Modifiers.offset(secDir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                            .export(quadMap, null)
            );
        }
        else
        {
            forAllSleepersCurve((i, distDir, distOpp) ->
                    QuadModifier.of(quad)
                            .apply(Modifiers.cut(Direction.UP, SLEEPER_HEIGHT))
                            .apply(Modifiers.cut(dir, distDir))
                            .apply(Modifiers.cut(dir.getOpposite(), distOpp))
                            .applyIf(rotateCurveSleeper(dir, secDir, i), i < 2)
                            .applyIf(Modifiers.offset(dir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                            .applyIf(Modifiers.offset(secDir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                            .export(quadMap, quadDir)
            );
        }
    }

    private static QuadModifier.Modifier rotateCurveSleeper(Direction dir, Direction secDir, int i)
    {
        float angle = 45F * (SLEEPER_COUNT_CURVE - 1 - i);
        if (secDir == dir.getCounterClockWise())
        {
            angle *= -1F;
        }
        return Modifiers.rotateCentered(Direction.Axis.Y, angle, false);
    }

    private static void forAllSleepers(SleeperConsumer consumer)
    {
        for (int i = 0; i < SLEEPER_COUNT; i++)
        {
            float distDir = SLEEPER_BASE_OFFSET + (i * SLEEPER_DIST) + SLEEPER_WIDTH;
            float distOpp = 1F - distDir + SLEEPER_WIDTH;
            consumer.accept(i, distDir, distOpp);
        }
    }

    private static void forAllSleepersCurve(SleeperConsumer consumer)
    {
        for (int i = 0; i < SLEEPER_COUNT_CURVE; i++)
        {
            float distDir = SLEEPER_BASE_OFFSET + (i * SLEEPER_DIST_CURVE) + SLEEPER_WIDTH;
            float distOpp = 1F - distDir + SLEEPER_WIDTH;
            consumer.accept(i, distDir, distOpp);
        }
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, false, auxShaderState, null);
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public static Direction getDirectionFromRailShape(RailShape shape)
    {
        return switch (shape)
        {
            case NORTH_SOUTH -> Direction.NORTH;
            case EAST_WEST -> Direction.EAST;
            case ASCENDING_NORTH -> Direction.NORTH;
            case ASCENDING_EAST -> Direction.EAST;
            case ASCENDING_SOUTH -> Direction.SOUTH;
            case ASCENDING_WEST -> Direction.WEST;
            case NORTH_EAST, NORTH_WEST -> Direction.NORTH;
            case SOUTH_EAST, SOUTH_WEST -> Direction.SOUTH;
        };
    }

    @Nullable
    private static Direction getSecondaryDirectionFromRailShape(RailShape shape)
    {
        return switch (shape)
        {
            case NORTH_EAST, SOUTH_EAST -> Direction.EAST;
            case NORTH_WEST, SOUTH_WEST -> Direction.WEST;
            default -> null;
        };
    }

    public static FramedFancyRailGeometry normal(GeometryFactory.Context ctx)
    {
        return new FramedFancyRailGeometry(ctx, BlockStateProperties.RAIL_SHAPE, Blocks.RAIL.defaultBlockState());
    }

    public static FramedFancyRailGeometry powered(GeometryFactory.Context ctx)
    {
        return new FramedFancyRailGeometry(ctx, BlockStateProperties.RAIL_SHAPE_STRAIGHT, Blocks.POWERED_RAIL.defaultBlockState());
    }

    public static FramedFancyRailGeometry detector(GeometryFactory.Context ctx)
    {
        return new FramedFancyRailGeometry(ctx, BlockStateProperties.RAIL_SHAPE_STRAIGHT, Blocks.DETECTOR_RAIL.defaultBlockState());
    }

    public static FramedFancyRailGeometry activator(GeometryFactory.Context ctx)
    {
        return new FramedFancyRailGeometry(ctx, BlockStateProperties.RAIL_SHAPE_STRAIGHT, Blocks.ACTIVATOR_RAIL.defaultBlockState());
    }

    @FunctionalInterface
    public interface SleeperConsumer
    {
        void accept(int index, float distDir, float distOpp);
    }
}
