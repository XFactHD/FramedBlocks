package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.Direction;

/**
 * Indicates in which direction the corner above the baseline points and whether the said corner is offset to the block center
 */
public enum QuarterTriangleDir
{
    NULL(null, null, false),

    NORTH_EAST_FALSE(Direction.NORTH, Direction.EAST, false),
    NORTH_WEST_FALSE(Direction.NORTH, Direction.WEST, false),
    SOUTH_EAST_FALSE(Direction.SOUTH, Direction.EAST, false),
    SOUTH_WEST_FALSE(Direction.SOUTH, Direction.WEST, false),

    NORTH_EAST_TRUE(Direction.NORTH, Direction.EAST, true),
    NORTH_WEST_TRUE(Direction.NORTH, Direction.WEST, true),
    SOUTH_EAST_TRUE(Direction.SOUTH, Direction.EAST, true),
    SOUTH_WEST_TRUE(Direction.SOUTH, Direction.WEST, true),

    UP_NORTH_FALSE  (Direction.UP, Direction.NORTH, false),
    UP_SOUTH_FALSE  (Direction.UP, Direction.SOUTH, false),
    DOWN_NORTH_FALSE(Direction.DOWN, Direction.NORTH, false),
    DOWN_SOUTH_FALSE(Direction.DOWN, Direction.SOUTH, false),

    UP_NORTH_TRUE  (Direction.UP, Direction.NORTH, true),
    UP_SOUTH_TRUE  (Direction.UP, Direction.SOUTH, true),
    DOWN_NORTH_TRUE(Direction.DOWN, Direction.NORTH, true),
    DOWN_SOUTH_TRUE(Direction.DOWN, Direction.SOUTH, true),

    UP_EAST_FALSE  (Direction.UP, Direction.EAST, false),
    UP_WEST_FALSE  (Direction.UP, Direction.WEST, false),
    DOWN_EAST_FALSE(Direction.DOWN, Direction.EAST, false),
    DOWN_WEST_FALSE(Direction.DOWN, Direction.WEST, false),

    UP_EAST_TRUE  (Direction.UP, Direction.EAST, true),
    UP_WEST_TRUE  (Direction.UP, Direction.WEST, true),
    DOWN_EAST_TRUE(Direction.DOWN, Direction.EAST, true),
    DOWN_WEST_TRUE(Direction.DOWN, Direction.WEST, true);

    private static final QuarterTriangleDir[][][] FROM_DIRS = makeDirTable();

    private final Direction cardOne;
    private final Direction cardTwo;
    private final boolean offset;

    QuarterTriangleDir(Direction cardOne, Direction cardTwo, boolean offset)
    {
        this.cardOne = cardOne;
        this.cardTwo = cardTwo;
        this.offset = offset;
    }

    public QuarterTriangleDir getOpposite()
    {
        if (this == NULL)
        {
            return this;
        }
        return fromDirections(cardOne.getOpposite(), cardTwo.getOpposite(), offset);
    }

    /**
     * {@return true if both {@code TriangleDir}s are not {@link QuarterTriangleDir#NULL} and the other
     * {@code TriangleDir} has the same edge directions}
     */
    public boolean isEqualTo(QuarterTriangleDir other)
    {
        return this != NULL && this == other;
    }

    public static QuarterTriangleDir fromDirections(Direction dirOne, Direction dirTwo, boolean offset)
    {
        QuarterTriangleDir dir = FROM_DIRS[dirOne.ordinal()][dirTwo.ordinal()][offset ? 1 : 0];
        if (dir == null)
        {
            throw new IllegalArgumentException("Invalid direction pair: edge one:" + dirOne + ", edge two: " + dirTwo);
        }
        return dir;
    }



    private static QuarterTriangleDir[][][] makeDirTable()
    {
        QuarterTriangleDir[][][] table = new QuarterTriangleDir[6][6][2];
        for (QuarterTriangleDir dir : values())
        {
            if (dir == NULL) { continue; }

            table[dir.cardOne.ordinal()][dir.cardTwo.ordinal()][dir.offset ? 1 : 0] = dir;
            table[dir.cardTwo.ordinal()][dir.cardOne.ordinal()][dir.offset ? 1 : 0] = dir;
        }
        return table;
    }
}
