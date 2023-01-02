package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.Direction;

import java.util.Objects;

/**
 * Indicates the normal and two outer edges of a quarter-block sized face
 */
public enum CornerDir
{
    NULL(null, null, null),

    UP_NORTH_EAST(Direction.UP, Direction.NORTH, Direction.EAST),
    UP_NORTH_WEST(Direction.UP, Direction.NORTH, Direction.WEST),
    UP_SOUTH_EAST(Direction.UP, Direction.SOUTH, Direction.EAST),
    UP_SOUTH_WEST(Direction.UP, Direction.SOUTH, Direction.WEST),

    DOWN_NORTH_EAST(Direction.DOWN, Direction.NORTH, Direction.EAST),
    DOWN_NORTH_WEST(Direction.DOWN, Direction.NORTH, Direction.WEST),
    DOWN_SOUTH_EAST(Direction.DOWN, Direction.SOUTH, Direction.EAST),
    DOWN_SOUTH_WEST(Direction.DOWN, Direction.SOUTH, Direction.WEST),

    NORTH_UP_EAST  (Direction.NORTH, Direction.UP, Direction.EAST),
    NORTH_UP_WEST  (Direction.NORTH, Direction.UP, Direction.WEST),
    NORTH_DOWN_EAST(Direction.NORTH, Direction.DOWN, Direction.EAST),
    NORTH_DOWN_WEST(Direction.NORTH, Direction.DOWN, Direction.WEST),

    SOUTH_UP_EAST  (Direction.SOUTH, Direction.UP, Direction.EAST),
    SOUTH_UP_WEST  (Direction.SOUTH, Direction.UP, Direction.WEST),
    SOUTH_DOWN_EAST(Direction.SOUTH, Direction.DOWN, Direction.EAST),
    SOUTH_DOWN_WEST(Direction.SOUTH, Direction.DOWN, Direction.WEST),

    EAST_UP_NORTH  (Direction.EAST, Direction.UP, Direction.NORTH),
    EAST_UP_SOUTH  (Direction.EAST, Direction.UP, Direction.SOUTH),
    EAST_DOWN_NORTH(Direction.EAST, Direction.DOWN, Direction.NORTH),
    EAST_DOWN_SOUTH(Direction.EAST, Direction.DOWN, Direction.SOUTH),

    WEST_UP_NORTH  (Direction.WEST, Direction.UP, Direction.NORTH),
    WEST_UP_SOUTH  (Direction.WEST, Direction.UP, Direction.SOUTH),
    WEST_DOWN_NORTH(Direction.WEST, Direction.DOWN, Direction.NORTH),
    WEST_DOWN_SOUTH(Direction.WEST, Direction.DOWN, Direction.SOUTH);

    private static final CornerDir[][][] FROM_DIRS = makeDirTable();

    private final Direction normal;
    private final Direction edgeOne;
    private final Direction edgeTwo;

    CornerDir(Direction normal, Direction edgeOne, Direction edgeTwo)
    {
        this.normal = normal;
        this.edgeOne = edgeOne;
        this.edgeTwo = edgeTwo;
    }

    public CornerDir getOppositeNormal()
    {
        if (this == NULL)
        {
            return this;
        }
        return fromDirections(normal.getOpposite(), edgeOne, edgeTwo);
    }

    /**
     * {@return true if both {@code CornerDir}s are not {@link CornerDir#NULL} and the other {@code CornerDir}
     * has the same edge directions and the opposite normal direction}
     */
    public boolean isEqualTo(CornerDir other)
    {
        return this != NULL && getOppositeNormal() == other;
    }

    public static CornerDir fromDirections(Direction normal, Direction edgeOne, Direction edgeTwo)
    {
        CornerDir dir = FROM_DIRS[normal.ordinal()][edgeOne.ordinal()][edgeTwo.ordinal()];
        return Objects.requireNonNull(dir, "Invalid direction triple!");
    }



    private static CornerDir[][][] makeDirTable()
    {
        CornerDir[][][] table = new CornerDir[6][6][6];
        for (CornerDir dir : values())
        {
            if (dir == NULL) { continue; }

            table[dir.normal.ordinal()][dir.edgeOne.ordinal()][dir.edgeTwo.ordinal()] = dir;
            table[dir.normal.ordinal()][dir.edgeTwo.ordinal()][dir.edgeOne.ordinal()] = dir;
        }
        return table;
    }
}
