package io.github.xfacthd.framedblocks.api.model.quad;

import com.mojang.datafixers.util.Pair;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

final class ModifierConfigs
{
    private static final boolean PRE_COMPUTE_CONFIGS = true;
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final @Nullable CuttingConfig[] CUTTING_CONFIGS = computeCuttingConfigs();

    static CuttingConfig getCuttingConfig(Direction quadDir, Direction cutEdge)
    {
        if (PRE_COMPUTE_CONFIGS)
        {
            CuttingConfig config = CUTTING_CONFIGS[makeConfigIndex(quadDir, cutEdge)];
            if (config == null)
            {
                throw new IllegalArgumentException(String.format(Locale.ROOT, "Invalid direction pair (quadDir: %s, cutEdge: %s)", quadDir, cutEdge));
            }
            return config;
        }
        return computeCuttingConfig(quadDir, cutEdge);
    }

    private static CuttingConfig[] computeCuttingConfigs()
    {
        CuttingConfig[] arr = new CuttingConfig[6 * 8];
        for (Direction quadDir : DIRECTIONS)
        {
            for (Direction cutEdge : DIRECTIONS)
            {
                if (quadDir.getAxis() != cutEdge.getAxis())
                {
                    arr[makeConfigIndex(quadDir, cutEdge)] = computeCuttingConfig(quadDir, cutEdge);
                }
            }
        }
        return arr;
    }

    private static int makeConfigIndex(Direction quadDir, Direction cutEdge)
    {
        return quadDir.ordinal() << 3 | cutEdge.ordinal();
    }

    private static CuttingConfig computeCuttingConfig(Direction quadDir, Direction cutEdge)
    {
        Direction.Axis quadAxis = quadDir.getAxis();
        Direction.Axis cutAxis = cutEdge.getAxis();
        CuttingConfig.VertPair cutEdgeVerts = getCutEdgeVertPair(quadDir, cutEdge);
        Pair<CuttingConfig.UvSrcVertSet, CuttingConfig.UvSrcVertSet> uvVerts = getUvVerts(quadAxis, cutAxis);
        return new CuttingConfig(
                getForwardCoord(quadAxis, cutAxis),
                getParallelCoord(quadAxis, cutAxis),
                cutEdgeVerts,
                getCheckEdgeVertPair(quadDir, cutEdge),
                invertParallelEdge(quadDir, cutEdge),
                swapCornerLengths(quadDir, cutEdge),
                uvVerts.getFirst(),
                uvVerts.getSecond(),
                isVAxis(quadAxis, cutAxis)
        );
    }

    /**
     * {@return the coordinate axis index along the axis of the cutting direction}
     */
    private static int getForwardCoord(Direction.Axis quadAxis, Direction.Axis cutAxis)
    {
        return switch (quadAxis)
        {
            case X -> switch (cutAxis)
            {
                case Y -> 1;
                case Z -> 2;
                case X -> throw new IllegalArgumentException();
            };
            case Y -> switch (cutAxis)
            {
                case X -> 0;
                case Z -> 2;
                case Y -> throw new IllegalArgumentException();
            };
            case Z -> switch (cutAxis)
            {
                case X -> 0;
                case Y -> 1;
                case Z -> throw new IllegalArgumentException();
            };
        };
    }

    /**
     * {@return the coordinate axis index parallel to the cutting edge}
     */
    private static int getParallelCoord(Direction.Axis quadAxis, Direction.Axis cutAxis)
    {
        return switch (quadAxis)
        {
            case X -> switch (cutAxis)
            {
                case Y -> 2;
                case Z -> 1;
                case X -> throw new IllegalArgumentException();
            };
            case Y -> switch (cutAxis)
            {
                case X -> 2;
                case Z -> 0;
                case Y -> throw new IllegalArgumentException();
            };
            case Z -> switch (cutAxis)
            {
                case X -> 1;
                case Y -> 0;
                case Z -> throw new IllegalArgumentException();
            };
        };
    }

    /**
     * {@return the vertex pair at the cutting edge which represents the vertices that get moved by the cutting operation}
     */
    private static CuttingConfig.VertPair getCutEdgeVertPair(Direction quadDir, Direction cutEdge)
    {
        return switch (quadDir)
        {
            case DOWN -> switch (cutEdge)
            {
                case NORTH -> new CuttingConfig.VertPair(1, 2);
                case SOUTH -> new CuttingConfig.VertPair(0, 3);
                case WEST -> new CuttingConfig.VertPair(1, 0);
                case EAST -> new CuttingConfig.VertPair(2, 3);
                case DOWN, UP -> throw new IllegalArgumentException();
            };
            case UP -> switch (cutEdge)
            {
                case NORTH -> new CuttingConfig.VertPair(0, 3);
                case SOUTH -> new CuttingConfig.VertPair(1, 2);
                case WEST -> new CuttingConfig.VertPair(1, 0);
                case EAST -> new CuttingConfig.VertPair(2, 3);
                case DOWN, UP -> throw new IllegalArgumentException();
            };
            case NORTH -> switch (cutEdge)
            {
                case DOWN -> new CuttingConfig.VertPair(1, 2);
                case UP -> new CuttingConfig.VertPair(0, 3);
                case WEST -> new CuttingConfig.VertPair(3, 2);
                case EAST -> new CuttingConfig.VertPair(0, 1);
                case NORTH, SOUTH -> throw new IllegalArgumentException();
            };
            case SOUTH -> switch (cutEdge)
            {
                case DOWN -> new CuttingConfig.VertPair(1, 2);
                case UP -> new CuttingConfig.VertPair(0, 3);
                case WEST -> new CuttingConfig.VertPair(0, 1);
                case EAST -> new CuttingConfig.VertPair(3, 2);
                case NORTH, SOUTH -> throw new IllegalArgumentException();
            };
            case WEST -> switch (cutEdge)
            {
                case DOWN -> new CuttingConfig.VertPair(1, 2);
                case UP -> new CuttingConfig.VertPair(0, 3);
                case NORTH -> new CuttingConfig.VertPair(0, 1);
                case SOUTH -> new CuttingConfig.VertPair(3, 2);
                case WEST, EAST -> throw new IllegalArgumentException();
            };
            case EAST -> switch (cutEdge)
            {
                case DOWN -> new CuttingConfig.VertPair(1, 2);
                case UP -> new CuttingConfig.VertPair(0, 3);
                case NORTH -> new CuttingConfig.VertPair(3, 2);
                case SOUTH -> new CuttingConfig.VertPair(0, 1);
                case WEST, EAST -> throw new IllegalArgumentException();
            };
        };
    }

    /**
     * {@return the vertex pair opposite the cutting edge which represents the vertices against which the cutting target is checked}
     */
    private static CuttingConfig.VertPair getCheckEdgeVertPair(Direction quadDir, Direction cutEdge)
    {
        return switch (quadDir)
        {
            case DOWN -> switch (cutEdge)
            {
                case NORTH -> new CuttingConfig.VertPair(0, 3);
                case SOUTH -> new CuttingConfig.VertPair(1, 2);
                case WEST -> new CuttingConfig.VertPair(2, 3);
                case EAST -> new CuttingConfig.VertPair(1, 0);
                case DOWN, UP -> throw new IllegalArgumentException();
            };
            case UP -> switch (cutEdge)
            {
                case NORTH -> new CuttingConfig.VertPair(1, 2);
                case SOUTH -> new CuttingConfig.VertPair(0, 3);
                case WEST -> new CuttingConfig.VertPair(2, 3);
                case EAST -> new CuttingConfig.VertPair(1, 0);
                case DOWN, UP -> throw new IllegalArgumentException();
            };
            case NORTH -> switch (cutEdge)
            {
                case DOWN -> new CuttingConfig.VertPair(0, 3);
                case UP -> new CuttingConfig.VertPair(1, 2);
                case WEST -> new CuttingConfig.VertPair(0, 1);
                case EAST -> new CuttingConfig.VertPair(3, 2);
                case NORTH, SOUTH -> throw new IllegalArgumentException();
            };
            case SOUTH -> switch (cutEdge)
            {
                case DOWN -> new CuttingConfig.VertPair(0, 3);
                case UP -> new CuttingConfig.VertPair(1, 2);
                case WEST -> new CuttingConfig.VertPair(3, 2);
                case EAST -> new CuttingConfig.VertPair(0, 1);
                case NORTH, SOUTH -> throw new IllegalArgumentException();
            };
            case WEST -> switch (cutEdge)
            {
                case DOWN -> new CuttingConfig.VertPair(0, 3);
                case UP -> new CuttingConfig.VertPair(1, 2);
                case NORTH -> new CuttingConfig.VertPair(3, 2);
                case SOUTH -> new CuttingConfig.VertPair(0, 1);
                case WEST, EAST -> throw new IllegalArgumentException();
            };
            case EAST -> switch (cutEdge)
            {
                case DOWN -> new CuttingConfig.VertPair(0, 3);
                case UP -> new CuttingConfig.VertPair(1, 2);
                case NORTH -> new CuttingConfig.VertPair(0, 1);
                case SOUTH -> new CuttingConfig.VertPair(3, 2);
                case WEST, EAST -> throw new IllegalArgumentException();
            };
        };
    }

    private static boolean invertParallelEdge(Direction quadDir, Direction cutEdge)
    {
        if (DirUtils.isY(quadDir))
        {
            return switch (cutEdge)
            {
                case NORTH, SOUTH -> quadDir == Direction.UP;
                case EAST, WEST -> false;
                case UP, DOWN -> throw new IllegalArgumentException();
            };
        }
        if (DirUtils.isY(cutEdge))
        {
            return DirUtils.isPositive(quadDir.getClockWise());
        }
        return true;
    }

    private static boolean swapCornerLengths(Direction quadDir, Direction cutEdge)
    {
        if (DirUtils.isY(quadDir))
        {
            return switch (cutEdge)
            {
                case NORTH -> quadDir == Direction.DOWN;
                case SOUTH -> quadDir == Direction.UP;
                case WEST -> false;
                case EAST -> true;
                case DOWN, UP -> throw new IllegalArgumentException();
            };
        }
        return false;
    }

    private static Pair<CuttingConfig.UvSrcVertSet, CuttingConfig.UvSrcVertSet> getUvVerts(Direction.Axis quadAxis, Direction.Axis cutAxis)
    {
        if (quadAxis == Direction.Axis.Y)
        {
            return switch (cutAxis)
            {
                case X -> Pair.of(
                        new CuttingConfig.UvSrcVertSet(1, 2, 1, 2),
                        new CuttingConfig.UvSrcVertSet(0, 3, 0, 3)
                );
                case Z -> Pair.of(
                        new CuttingConfig.UvSrcVertSet(1, 0, 0, 1),
                        new CuttingConfig.UvSrcVertSet(2, 3, 3, 2)
                );
                case Y -> throw new IllegalArgumentException();
            };
        }
        if (cutAxis == Direction.Axis.Y)
        {
            return Pair.of(
                    new CuttingConfig.UvSrcVertSet(1, 0, 0, 1),
                    new CuttingConfig.UvSrcVertSet(2, 3, 3, 2)
            );
        }
        return Pair.of(
                new CuttingConfig.UvSrcVertSet(0, 3, 0, 3),
                new CuttingConfig.UvSrcVertSet(1, 2, 1, 2)
        );
    }

    private static boolean isVAxis(Direction.Axis quadAxis, Direction.Axis cutAxis)
    {
        if (quadAxis == Direction.Axis.Y)
        {
            return cutAxis == Direction.Axis.Z;
        }
        return cutAxis == Direction.Axis.Y;
    }

    private ModifierConfigs() { }
}
