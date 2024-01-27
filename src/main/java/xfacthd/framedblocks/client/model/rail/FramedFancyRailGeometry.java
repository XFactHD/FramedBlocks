package xfacthd.framedblocks.client.model.rail;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;

public class FramedFancyRailGeometry implements Geometry
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
    private final BakedModel baseModel;
    private final RailShape shape;
    private final Direction mainDir;
    private final Direction secDir;

    private FramedFancyRailGeometry(GeometryFactory.Context ctx, Property<RailShape> shapeProperty)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.shape = state.getValue(shapeProperty);
        this.mainDir = getDirectionFromRailShape(shape);
        this.secDir = getSecondaryDirectionFromRailShape(shape);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Pair<List<BakedQuad>, Direction> result;
        if (shape.isAscending())
        {
            result = makeAscendingRailSleepers(quad, mainDir);
        }
        else if (shape == RailShape.NORTH_SOUTH || shape == RailShape.EAST_WEST)
        {
            result = makeStraightRailSleepers(quad, mainDir);
        }
        else
        {
            result = makeCurvedRailSleepers(quad, mainDir, secDir);
        }
        quadMap.get(result.getSecond()).addAll(result.getFirst());
    }

    private static Pair<List<BakedQuad>, Direction> makeStraightRailSleepers(BakedQuad quad, Direction dir)
    {
        List<BakedQuad> result = new ArrayList<>(SLEEPER_COUNT);
        Direction targetDir;

        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            targetDir = quadDir == Direction.UP ? null : quadDir;

            forAllSleepers((i, distDir, distOpp) ->
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutTopBottom(dir, distDir))
                            .apply(Modifiers.cutTopBottom(dir.getOpposite(), distOpp))
                            .applyIf(Modifiers.setPosition(SLEEPER_HEIGHT), quadDir == Direction.UP)
                            .export(result)
            );
        }
        else if (quadDir.getAxis() == dir.getAxis())
        {
            targetDir = null;

            forAllSleepers((i, distDir, distOpp) ->
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideUpDown(false, SLEEPER_HEIGHT))
                            .apply(Modifiers.setPosition(distDir))
                            .export(result)
            );
        }
        else
        {
            targetDir = quadDir;

            forAllSleepers((i, distDir, distOpp) ->
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideUpDown(false, SLEEPER_HEIGHT))
                            .apply(Modifiers.cutSideLeftRight(dir, distDir))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), distOpp))
                            .export(result)
            );
        }

        return Pair.of(result, targetDir);
    }

    private static Pair<List<BakedQuad>, Direction> makeAscendingRailSleepers(BakedQuad quad, Direction dir)
    {
        Pair<List<BakedQuad>, Direction> result = makeStraightRailSleepers(quad, dir);

        Direction.Axis axis = dir.getClockWise().getAxis();
        Vector3f origin = SLOPE_ORIGINS[dir.get2DDataValue()];
        float angle = Utils.isPositive(dir) == Utils.isX(dir) ? 45F : -45F;
        Vector3f scaleVec = Utils.isX(dir) ? SCALE_X : SCALE_Z;

        List<BakedQuad> quads = result.getFirst();
        for (BakedQuad resultQuad : quads)
        {
            QuadModifier.geometry(resultQuad)
                    .apply(Modifiers.rotate(axis, origin, angle, true, scaleVec))
                    .modifyInPlace();
        }

        Direction targetDir = result.getSecond() == Direction.DOWN ? null : result.getSecond();
        return Pair.of(quads, targetDir);
    }

    private static Pair<List<BakedQuad>, Direction> makeCurvedRailSleepers(BakedQuad quad, Direction dir, Direction secDir)
    {
        List<BakedQuad> result = new ArrayList<>(SLEEPER_COUNT_CURVE);
        Direction targetDir;

        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            targetDir = quadDir == Direction.UP ? null : quadDir;

            forAllSleepersCurve((i, distDir, distOpp) ->
            {
                boolean nonDiagUp = quadDir == Direction.UP && i != 1;
                float height = nonDiagUp ? (SLEEPER_HEIGHT - .001F) : SLEEPER_HEIGHT;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir, distDir))
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), distOpp))
                        .applyIf(Modifiers.setPosition(height), quadDir == Direction.UP)
                        .applyIf(rotateCurveSleeper(dir, secDir, i), i < 2)
                        .applyIf(Modifiers.offset(dir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                        .applyIf(Modifiers.offset(secDir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                        .export(result);
            });
        }
        else if (quadDir.getAxis() == dir.getAxis())
        {
            targetDir = null;

            boolean inDir = quadDir == dir;
            forAllSleepersCurve((i, distDir, distOpp) ->
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideUpDown(false, SLEEPER_HEIGHT))
                            .apply(Modifiers.setPosition(inDir ? distDir : distOpp))
                            .applyIf(rotateCurveSleeper(dir, secDir, i), i < 2)
                            .applyIf(Modifiers.offset(dir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                            .applyIf(Modifiers.offset(secDir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                            .export(result)
            );
        }
        else
        {
            targetDir = quadDir;

            forAllSleepersCurve((i, distDir, distOpp) ->
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideUpDown(false, SLEEPER_HEIGHT))
                            .apply(Modifiers.cutSideLeftRight(dir, distDir))
                            .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), distOpp))
                            .applyIf(rotateCurveSleeper(dir, secDir, i), i < 2)
                            .applyIf(Modifiers.offset(dir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                            .applyIf(Modifiers.offset(secDir, SLEEPER_DIAGONAL_OFFSET), i == 1)
                            .export(result)
            );
        }

        return Pair.of(result, targetDir);
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
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return baseModel.getRenderTypes(state, rand, extraData);
    }

    @Override
    public void getAdditionalQuads(
            QuadMap quadMap,
            RandomSource rand,
            ModelData data,
            RenderType renderType
    )
    {
        Utils.forAllDirections(dir -> quadMap.get(dir).addAll(baseModel.getQuads(state, dir, rand, data, renderType)));
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
        return new FramedFancyRailGeometry(ctx, BlockStateProperties.RAIL_SHAPE);
    }

    public static FramedFancyRailGeometry straight(GeometryFactory.Context ctx)
    {
        return new FramedFancyRailGeometry(ctx, BlockStateProperties.RAIL_SHAPE_STRAIGHT);
    }

    @FunctionalInterface
    public interface SleeperConsumer
    {
        void accept(int index, float distDir, float distOpp);
    }
}
