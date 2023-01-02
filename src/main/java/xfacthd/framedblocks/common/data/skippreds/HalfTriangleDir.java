package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.Direction;

import java.util.Objects;

/**
 * Indicates in which direction the corner above the baseline points and whether the long edge
 * is on the outer edge of the block
 */
public enum HalfTriangleDir
{
    NULL(null, null, false),

    NORTH_UP_FALSE  (Direction.NORTH, Direction.UP, false),
    NORTH_DOWN_FALSE(Direction.NORTH, Direction.DOWN, false),
    NORTH_EAST_FALSE(Direction.NORTH, Direction.EAST, false),
    NORTH_WEST_FALSE(Direction.NORTH, Direction.WEST, false),

    NORTH_UP_TRUE  (Direction.NORTH, Direction.UP, true),
    NORTH_DOWN_TRUE(Direction.NORTH, Direction.DOWN, true),
    NORTH_EAST_TRUE(Direction.NORTH, Direction.EAST, true),
    NORTH_WEST_TRUE(Direction.NORTH, Direction.WEST, true),

    EAST_UP_FALSE   (Direction.EAST, Direction.UP, false),
    EAST_DOWN_FALSE (Direction.EAST, Direction.DOWN, false),
    EAST_NORTH_FALSE(Direction.EAST, Direction.NORTH, false),
    EAST_SOUTH_FALSE(Direction.EAST, Direction.SOUTH, false),

    EAST_UP_TRUE   (Direction.EAST, Direction.UP, true),
    EAST_DOWN_TRUE (Direction.EAST, Direction.DOWN, true),
    EAST_NORTH_TRUE(Direction.EAST, Direction.NORTH, true),
    EAST_SOUTH_TRUE(Direction.EAST, Direction.SOUTH, true),

    SOUTH_UP_FALSE  (Direction.SOUTH, Direction.UP, false),
    SOUTH_DOWN_FALSE(Direction.SOUTH, Direction.DOWN, false),
    SOUTH_EAST_FALSE(Direction.SOUTH, Direction.EAST, false),
    SOUTH_WEST_FALSE(Direction.SOUTH, Direction.WEST, false),

    SOUTH_UP_TRUE  (Direction.SOUTH, Direction.UP, true),
    SOUTH_DOWN_TRUE(Direction.SOUTH, Direction.DOWN, true),
    SOUTH_EAST_TRUE(Direction.SOUTH, Direction.EAST, true),
    SOUTH_WEST_TRUE(Direction.SOUTH, Direction.WEST, true),

    WEST_UP_FALSE   (Direction.WEST, Direction.UP, false),
    WEST_DOWN_FALSE (Direction.WEST, Direction.DOWN, false),
    WEST_NORTH_FALSE(Direction.WEST, Direction.NORTH, false),
    WEST_SOUTH_FALSE(Direction.WEST, Direction.SOUTH, false),

    WEST_UP_TRUE   (Direction.WEST, Direction.UP, true),
    WEST_DOWN_TRUE (Direction.WEST, Direction.DOWN, true),
    WEST_NORTH_TRUE(Direction.WEST, Direction.NORTH, true),
    WEST_SOUTH_TRUE(Direction.WEST, Direction.SOUTH, true),

    UP_NORTH_FALSE(Direction.UP, Direction.NORTH, false),
    UP_EAST_FALSE (Direction.UP, Direction.EAST, false),
    UP_SOUTH_FALSE(Direction.UP, Direction.SOUTH, false),
    UP_WEST_FALSE (Direction.UP, Direction.WEST, false),

    UP_NORTH_TRUE(Direction.UP, Direction.NORTH, true),
    UP_EAST_TRUE (Direction.UP, Direction.EAST, true),
    UP_SOUTH_TRUE(Direction.UP, Direction.SOUTH, true),
    UP_WEST_TRUE (Direction.UP, Direction.WEST, true),

    DOWN_NORTH_FALSE(Direction.DOWN, Direction.NORTH, false),
    DOWN_EAST_FALSE (Direction.DOWN, Direction.EAST, false),
    DOWN_SOUTH_FALSE(Direction.DOWN, Direction.SOUTH, false),
    DOWN_WEST_FALSE (Direction.DOWN, Direction.WEST, false),

    DOWN_NORTH_TRUE(Direction.DOWN, Direction.NORTH, true),
    DOWN_EAST_TRUE (Direction.DOWN, Direction.EAST, true),
    DOWN_SOUTH_TRUE(Direction.DOWN, Direction.SOUTH, true),
    DOWN_WEST_TRUE (Direction.DOWN, Direction.WEST, true);

    private static final HalfTriangleDir[][][] FROM_DIRS = makeDirTable();

    private final Direction longEdge;
    private final Direction shortEdge;
    private final boolean outerEdge;

    HalfTriangleDir(Direction longEdge, Direction shortEdge, boolean outerEdge)
    {
        this.longEdge = longEdge;
        this.shortEdge = shortEdge;
        this.outerEdge = outerEdge;
    }

    public HalfTriangleDir getOpposite()
    {
        if (this == NULL)
        {
            return this;
        }
        return fromDirections(longEdge.getOpposite(), shortEdge.getOpposite(), !outerEdge);
    }

    /**
     * {@return true if both {@code HalfTriangleDir}s are not {@link HalfTriangleDir#NULL} and the other
     * {@code HalfTriangleDir} has the same edge directions and the same connection to the outer edge}
     */
    public boolean isEqualTo(HalfTriangleDir other)
    {
        return this != NULL && this == other;
    }

    public static HalfTriangleDir fromDirections(Direction longEdge, Direction shortEdge, boolean outerEdge)
    {
        HalfTriangleDir dir = FROM_DIRS[longEdge.ordinal()][shortEdge.ordinal()][outerEdge ? 1 : 0];
        return Objects.requireNonNull(dir, "Invalid dir/dir/bool triple!");
    }



    private static HalfTriangleDir[][][] makeDirTable()
    {
        HalfTriangleDir[][][] table = new HalfTriangleDir[6][6][2];
        for (HalfTriangleDir dir : values())
        {
            if (dir == NULL) { continue; }

            table[dir.longEdge.ordinal()][dir.shortEdge.ordinal()][dir.outerEdge ? 1 : 0] = dir;
        }
        return table;
    }
}
